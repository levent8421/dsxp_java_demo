package com.monolith.hik.dau;

import com.monolith.dsxp.driver.DsxpWorker;
import com.monolith.dsxp.tree.DsxpDauNode;
import com.monolith.dsxp.util.DeviceTreeUtils;
import com.monolith.dsxp.util.DsxpEventUtils;
import com.monolith.dsxp.util.ExceptionUtils;
import com.monolith.dsxp.util.ListUtils;
import com.monolith.dsxp.util.StateMachine;
import com.monolith.hik.HIKConstants;
import com.monolith.hik.HIKException;
import com.monolith.hik.HIKNVREventIds;
import com.monolith.hik.dau.event.CameraDauDownloadCompleteEvent;
import com.monolith.hik.device.HIKCameraDeviceWorker;
import com.monolith.hik.nvr.HIKDVRClient;
import com.monolith.hik.util.NVRClientUtils;
import com.monolith.hik.util.StorageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Date: 2025/6/30 10:51
 * Author: Levent
 * 海康监控相机采集点
 */
public class HIKCameraDauWorker extends AbstractHIKDauWorker {
    private static final Logger log = LoggerFactory.getLogger(HIKCameraDauWorker.class);
    public static final int PROGRESS_CHECK_INTERVAL = 500;
    public static final String NAME = "CAM";
    private final HIKCameraDauState state;
    private HIKCameraDeviceWorker cameraDevice;
    private int cameraChannelId;
    private long lastProgressCheckTime = -1;

    public HIKCameraDauWorker(DsxpDauNode node) {
        super(node);
        this.state = new HIKCameraDauState();
    }

    @Override
    public void onBuildComplete() throws Exception {
        List<DsxpWorker> workers = DeviceTreeUtils.findAllChildrenWorkers(node());
        for (DsxpWorker worker : workers) {
            if (worker instanceof HIKCameraDeviceWorker) {
                this.cameraDevice = (HIKCameraDeviceWorker) worker;
                this.cameraChannelId = this.cameraDevice.getChannelId();
            } else {
                log.warn("{}:Can not resolve child:{}", node(), worker.node());
            }
        }
    }

    @Override
    public void onTick() throws Exception {
        resolveTasks();
    }

    private void resolveTasks() {
        Map<String, MediaDownloadTask> taskTable = state.getTaskTable();
        if (taskTable.isEmpty()) {
            return;
        }
        synchronized (this) {
            List<String> completedRequestIds = ListUtils.newArrayList();
            Collection<MediaDownloadTask> tasks = taskTable.values();
            for (MediaDownloadTask task : tasks) {
                try {
                    resolveTaskStateMachine(task);
                    StateMachine workState = task.getWorkState();
                    if (workState.isInStates(MediaDownloadTask.STATE_COMPLETED)) {
                        completedRequestIds.add(task.getDownloadRequestId());
                    }
                } catch (Exception e) {
                    log.error("Error on resolve task state machine:{}", task.getDownloadRequestId(), e);
                }
            }
            for (String requestId : completedRequestIds) {
                taskTable.remove(requestId);
            }
        }
    }

    public void requestDownload(String requestId, int mode, Date start, int duration, File file) throws HIKException {
        MediaDownloadTask task = new MediaDownloadTask(this, requestId, mode, start, duration, file);
        synchronized (this) {
            Map<String, MediaDownloadTask> taskTable = state.getTaskTable();
            if (taskTable.containsKey(task.getDownloadRequestId())) {
                throw new HIKException("Task " + task.getDownloadRequestId() + " downloading!");
            }
            taskTable.put(task.getDownloadRequestId(), task);
        }
    }

    private void resolveTaskStateMachine(MediaDownloadTask task) {
        StateMachine workState = task.getWorkState();
        switch (workState.getState()) {
            case MediaDownloadTask.STATE_DOWNLOAD_REQUESTED:
                resolveDownloadRequested(task);
                break;
            case MediaDownloadTask.STATE_DOWNLOADING:
                resolveDownloading(task);
                break;
            case MediaDownloadTask.STATE_DOWNLOAD_DONE:
                resolveDownloadDone(task);
                break;
            case MediaDownloadTask.STATE_DOWNLOAD_ERROR:
                resolveDownloadError(task);
                break;
            default:
                log.error("{}:Can not resolve state,reset to idle!", workState.getState());
                workState.setState(MediaDownloadTask.STATE_COMPLETED, -1);
        }
    }

    private void resolveDownloadRequested(MediaDownloadTask task) {
        HIKDVRClient client = NVRClientUtils.getClient(node());
        if (client == null) {
            return;
        }
        int downloadMode = task.getDownloadMode();
        Date downloadTimeStart = task.getDownloadTimeStart();
        int downloadVideoDuration = task.getDownloadVideoDuration();
        File downloadFile = task.getDownloadFile();
        if (downloadMode == MediaDownloadTask.MODE_IMAGE) {
            downloadError(task, "Image capture unsupported!");
        } else if (downloadMode == MediaDownloadTask.MODE_VIDEO) {
            downloadVideo(task, downloadTimeStart, downloadVideoDuration, downloadFile);
        } else {
            downloadError(task, "Can not resolve download mode:" + downloadMode);
        }
    }

    private void downloadError(MediaDownloadTask task, String error) {
        task.setErrorMsg(error);
        task.getWorkState().setState(MediaDownloadTask.STATE_DOWNLOAD_ERROR, -1);
    }

    private void downloadVideo(MediaDownloadTask task, Date start, int duration, File file) {
        if (file == null) {
            try {
                file = StorageUtils.getNextTmpVideoFile();
            } catch (Exception e) {
                downloadError(task, ExceptionUtils.getMessage(e));
                return;
            }
        }
        if (start == null) {
            start = new Date();
        }
        if (duration <= 0) {
            duration = 1000;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.MILLISECOND, duration);
        Date endTime = calendar.getTime();

        VideoDownloadTask videoDownloadTask = new VideoDownloadTask(this, task);
        videoDownloadTask.setChannel(cameraChannelId);
        videoDownloadTask.setFile(file);
        videoDownloadTask.setStartTime(start);
        videoDownloadTask.setEndTime(endTime);
        runNVRTask(videoDownloadTask);
    }

    private void resolveDownloading(MediaDownloadTask task) {
        StateMachine workState = task.getWorkState();
        if (workState.isExpired()) {
            downloadError(task, "Download Timeout!");
            return;
        }
        long now = System.currentTimeMillis();
        long timeSpan = now - lastProgressCheckTime;
        if (timeSpan < PROGRESS_CHECK_INTERVAL) {
            return;
        }
        runNVRTask(new DownloadProgressCheckTask(this, task));
        now = System.currentTimeMillis();
        lastProgressCheckTime = now;
    }

    private void resolveDownloadDone(MediaDownloadTask task) {
        emitDownloadCompleteEvent(task, true);
        StateMachine workState = task.getWorkState();
        workState.setState(MediaDownloadTask.STATE_COMPLETED, -1);
    }

    private void resolveDownloadError(MediaDownloadTask task) {
        emitDownloadCompleteEvent(task, false);
        StateMachine workState = task.getWorkState();
        workState.setState(MediaDownloadTask.STATE_COMPLETED, -1);
    }

    private void emitDownloadCompleteEvent(MediaDownloadTask task, boolean success) {
        String downloadRequestId = task.getDownloadRequestId();
        int downloadMode = task.getDownloadMode();
        File downloadFile = task.getDownloadFile();
        Date downloadTimeStart = task.getDownloadTimeStart();
        int downloadVideoDuration = task.getDownloadVideoDuration();
        CameraDauDownloadCompleteEvent event = new CameraDauDownloadCompleteEvent();
        event.setRequestId(downloadRequestId);
        event.setSuccess(success);
        event.setMsg(task.getErrorMsg());
        event.setFile(downloadFile);
        event.setMode(downloadMode);
        event.setTimeStart(downloadTimeStart);
        event.setDuration(downloadVideoDuration);
        event.setFileFormat(HIKConstants.VIDEO_FORMAT);
        DsxpEventUtils.emit(node(), HIKNVREventIds.DOWNLOAD_COMPLETE, event, true);
    }

    @Override
    public HIKCameraDauState state() {
        return state;
    }
}
