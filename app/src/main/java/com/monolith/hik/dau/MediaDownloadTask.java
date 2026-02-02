package com.monolith.hik.dau;

import com.monolith.dsxp.util.StateMachine;

import java.io.File;
import java.util.Date;

public class MediaDownloadTask {
    public static final int STATE_DOWNLOAD_REQUESTED = 0x01;
    public static final int STATE_DOWNLOADING = 0x02;
    public static final int STATE_DOWNLOAD_DONE = 0x03;
    public static final int STATE_DOWNLOAD_ERROR = 0x04;
    public static final int STATE_COMPLETED = 0x05;
    public static final int MODE_IMAGE = 0x00;
    public static final int MODE_VIDEO = 0x01;
    private final StateMachine workState;
    private String downloadRequestId;
    private int downloadId;
    private int downloadMode;
    private Date downloadTimeStart;
    private int downloadVideoDuration;
    private File downloadFile;
    private String errorMsg;

    public MediaDownloadTask(HIKCameraDauWorker worker, String requestId, int downloadMode, Date downloadTimeStart, int downloadVideoDuration, File downloadFile) {
        this.downloadRequestId = requestId;
        this.downloadMode = downloadMode;
        this.downloadTimeStart = downloadTimeStart;
        this.downloadVideoDuration = downloadVideoDuration;
        this.downloadFile = downloadFile;
        this.workState = new StateMachine(worker.node().identifier());
        setupStateMachine();
    }

    private void setupStateMachine() {
        workState.defineState(STATE_DOWNLOAD_REQUESTED, "DOWNLOAD_REQUESTED");
        workState.defineState(STATE_DOWNLOADING, "DOWNLOADING");
        workState.defineState(STATE_DOWNLOAD_DONE, "DOWNLOAD_DONE");
        workState.defineState(STATE_DOWNLOAD_ERROR, "DOWNLOAD_ERROR");
        workState.setState(STATE_DOWNLOAD_REQUESTED, -1);
    }

    public StateMachine getWorkState() {
        return workState;
    }

    public String getDownloadRequestId() {
        return downloadRequestId;
    }

    public void setDownloadRequestId(String downloadRequestId) {
        this.downloadRequestId = downloadRequestId;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public int getDownloadMode() {
        return downloadMode;
    }

    public void setDownloadMode(int downloadMode) {
        this.downloadMode = downloadMode;
    }

    public Date getDownloadTimeStart() {
        return downloadTimeStart;
    }

    public void setDownloadTimeStart(Date downloadTimeStart) {
        this.downloadTimeStart = downloadTimeStart;
    }

    public int getDownloadVideoDuration() {
        return downloadVideoDuration;
    }

    public void setDownloadVideoDuration(int downloadVideoDuration) {
        this.downloadVideoDuration = downloadVideoDuration;
    }

    public File getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(File downloadFile) {
        this.downloadFile = downloadFile;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
