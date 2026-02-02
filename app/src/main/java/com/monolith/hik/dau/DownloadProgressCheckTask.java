package com.monolith.hik.dau;

import com.monolith.dsxp.util.ExceptionUtils;
import com.monolith.dsxp.util.StateMachine;
import com.monolith.hik.nvr.HIKDVRClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 2025/7/3 15:08
 * Author: Levent
 * 下载进度检查
 */
public class DownloadProgressCheckTask implements HIKNVRTask {
    private static final Logger log = LoggerFactory.getLogger(DownloadProgressCheckTask.class);
    private final MediaDownloadTask task;
    private final HIKCameraDauWorker worker;

    public DownloadProgressCheckTask(HIKCameraDauWorker worker, MediaDownloadTask task) {
        this.task = task;
        this.worker = worker;
    }

    @Override
    public void nvrTaskRun(HIKDVRClient client) throws Exception {
        int downloadId = task.getDownloadId();
        int progress = client.getDownloadProgress(downloadId);
        log.info("{}:Download[{}] progress={}", worker, downloadId, progress);
        if (progress >= 100) {
            downloadComplete(MediaDownloadTask.STATE_DOWNLOAD_DONE, "Progress=" + progress);
        }
    }

    @Override
    public void onTaskError(Exception err) {
        downloadComplete(MediaDownloadTask.STATE_DOWNLOAD_ERROR, ExceptionUtils.getMessage(err));
    }

    private void downloadComplete(int stateCode, String msg) {
        task.setErrorMsg(msg);
        StateMachine workState = task.getWorkState();
        workState.setState(stateCode, -1);
    }
}
