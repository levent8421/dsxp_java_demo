package com.monolith.hik.dau;

import com.monolith.dsxp.util.DatetimeUtils;
import com.monolith.dsxp.util.ExceptionUtils;
import com.monolith.dsxp.util.StateMachine;
import com.monolith.hik.nvr.HIKDVRClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

/**
 * Date: 2025/6/30 15:54
 * Author: Levent
 * Download Task
 */
public class VideoDownloadTask implements HIKNVRTask {
    private static final Logger log = LoggerFactory.getLogger(VideoDownloadTask.class);
    public static final int DOWNLOAD_TIMEOUT = 30 * 1000;
    private int channel;
    private Date startTime;
    private Date endTime;
    private File file;
    private final HIKCameraDauWorker dauWorker;
    private final MediaDownloadTask task;

    public VideoDownloadTask(HIKCameraDauWorker dauWorker, MediaDownloadTask task) {
        this.dauWorker = dauWorker;
        this.task = task;
    }

    @Override
    public void nvrTaskRun(HIKDVRClient client) throws Exception {
        log.info("[{}] download scope start:{} end:{}", file.getAbsolutePath(), DatetimeUtils.format(startTime), DatetimeUtils.format(endTime));
        int downloadId = client.download(channel, startTime, endTime, file.getAbsolutePath());
        log.info("{}:Download ID={}", dauWorker, downloadId);
        StateMachine workState = task.getWorkState();
        workState.setState(MediaDownloadTask.STATE_DOWNLOADING, DOWNLOAD_TIMEOUT);
        task.setDownloadId(downloadId);
    }

    @Override
    public void onTaskError(Exception err) {
        StateMachine workState = task.getWorkState();
        workState.setState(MediaDownloadTask.STATE_DOWNLOAD_ERROR, -1);
        task.setErrorMsg(ExceptionUtils.getMessage(err));
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
