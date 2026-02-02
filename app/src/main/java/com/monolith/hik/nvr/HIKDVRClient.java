package com.monolith.hik.nvr;

import static com.monolith.hik.jna.HCNetSDKByJNA.NET_DVR_SET_TIMECFG;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_TIME;
import com.hikvision.netsdk.NET_DVR_VOD_PARA;
import com.hikvision.netsdk.PlaybackControlCommand;
import com.monolith.dsxp.util.ExceptionUtils;
import com.monolith.dsxp.util.ListUtils;
import com.monolith.hik.HIKException;
import com.monolith.hik.jna.HCNetSDKByJNA;
import com.monolith.hik.jna.HCNetSDKJNAInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Date: 2025/2/24 11:20
 * Author: Levent
 * 海康DVR客户端
 */
public class HIKDVRClient {
    private static final Logger log = LoggerFactory.getLogger(HIKDVRClient.class);
    public static final byte STREAM_MAIN = 0;
    public static final byte STREAM_SUB = 1;
    public static final byte STREAM_THIRD = 2;
    public static final int MODE_BMP = 0;
    public static final int MODE_JPEG = 1;
    private static HIKDVRClient INSTANCE = null;

    public static HIKDVRClient getInstance(HIKDVROptions options) throws HIKException {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (HIKDVRClient.class) {
            if (INSTANCE != null) {
                return INSTANCE;
            }
            INSTANCE = new HIKDVRClient(options);
            INSTANCE.init();
        }
        return INSTANCE;
    }

    private final HIKDVROptions options;
    private final List<Integer> ipCamChannels = ListUtils.newArrayList();
    private HCNetSDKByJNA sdk;
    private HCNetSDK netSDK;
    private int uid = -1;

    private HIKDVRClient(HIKDVROptions options) {
        this.options = options;
    }

    public void init() throws HIKException {
        try {
            sdk = HCNetSDKJNAInstance.getInstance();
            if (sdk.NET_DVR_Init()) {
                String logDir = options.getLogDir();
                if (logDir != null) {
                    sdk.NET_DVR_SetLogToFile(3, logDir, false);
                }
            } else {
                throw new HIKException("SDK init failed!");
            }
            netSDK = HCNetSDK.getInstance();
        } catch (Exception e) {
            throw new HIKException(ExceptionUtils.getMessage(e), e);
        }
    }

    public void destroy() {
        sdk.NET_DVR_Cleanup();
    }

    public void login(String ip, int port, String user, String password) throws HIKException {
        HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO loginInfo = new HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO();
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] ipBytes = ip.getBytes(utf8);
        byte[] userBytes = user.getBytes(utf8);
        byte[] passwordBytes = password.getBytes(utf8);
        System.arraycopy(ipBytes, 0, loginInfo.sDeviceAddress, 0, ipBytes.length);
        System.arraycopy(userBytes, 0, loginInfo.sUserName, 0, userBytes.length);
        System.arraycopy(passwordBytes, 0, loginInfo.sPassword, 0, passwordBytes.length);
        loginInfo.wPort = (short) port;
        loginInfo.write();
        HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40 deviceInfo = new HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40();
        int userId = sdk.NET_DVR_Login_V40(loginInfo.getPointer(), deviceInfo.getPointer());
        if (userId < 0) {
            error("Login failed,Result UID=" + userId);
            return;
        }
        deviceInfo.read();
        initChannels(deviceInfo);
        this.uid = userId;
        log.info("HIK_DVR Login Success:{}, uid={}", ip, uid);
    }

    private void initChannels(HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40 deviceInfo) {
        ipCamChannels.clear();
        byte startId = deviceInfo.struDeviceV30.byStartDChan;
        int channelNum = (deviceInfo.struDeviceV30.byHighDChanNum << 8) | deviceInfo.struDeviceV30.byIPChanNum;
        for (int i = 0; i < channelNum; i++) {
            ipCamChannels.add(startId + i);
        }
    }

    public void logout() {
        sdk.NET_DVR_Logout(uid);
    }

    public int download(int channel, Date timeStart, Date timeEnd, String filename) throws HIKException {
        NET_DVR_TIME sdkTimeStart = asSdkTime(timeStart);
        NET_DVR_TIME sdkTimeEnd = asSdkTime(timeEnd);
        int downloadId = netSDK.NET_DVR_GetFileByTime(uid, channel, sdkTimeStart, sdkTimeEnd, filename);
        if (downloadId < 0) {
            error("Invalid downloadID:" + downloadId);
            return -1;
        }
        TransType transType = new TransType();
        transType.type = 2;
        transType.write();

        boolean setTranceTypeSuccess = sdk.NET_DVR_PlayBackControl_V40(
                downloadId, PlaybackControlCommand.NET_DVR_SET_TRANS_TYPE, transType.getPointer(), transType.size(), null, null);
        log.info("setTranceTypeSuccess={}", setTranceTypeSuccess);
        boolean playbackSuccess = netSDK.NET_DVR_PlayBackControl_V40(downloadId, PlaybackControlCommand.NET_DVR_PLAYSTART, null, 0, null);
        if (!playbackSuccess) {
            log.error("Playback start failed!downloadId={}", downloadId);
            stopDownload(downloadId);
            error("Download(Playback start failed)");
            return -1;
        }
        return downloadId;
    }

    public int getDownloadProgress(int downloadId) {
        return netSDK.NET_DVR_GetDownloadPos(downloadId);
    }

    public void stopDownload(int downloadId) throws HIKException {
        if (!netSDK.NET_DVR_StopGetFile(downloadId)) {
            error("Error on stop download!");
        }
    }

    public List<Integer> getChannels() {
        return ipCamChannels;
    }

    public void captureImage(int channel, byte streamType, Date time, String filename) throws HIKException {
        NET_DVR_VOD_PARA playbackParam = new NET_DVR_VOD_PARA();
        NET_DVR_TIME sdkTime = asSdkTime(time);
        playbackParam.struBeginTime = sdkTime;
        playbackParam.struEndTime = sdkTime;
        playbackParam.byStreamType = streamType;
        playbackParam.byAudioFile = 0;
        playbackParam.struIDInfo.dwChannel = channel;
        int playbackId = netSDK.NET_DVR_PlayBackByTime_V40(uid, playbackParam);
        if (playbackId < 0) {
            error("Invalid playback res=" + playbackId);
            return;
        }
        try {
            boolean setModeSuccess = sdk.NET_DVR_SetCapturePictureMode(MODE_JPEG);
            log.debug("setModeSuccess={}", setModeSuccess);
            boolean captureSuccess = sdk.NET_DVR_PlayBackCaptureFile(playbackId, filename);
            if (!captureSuccess) {
                error("Capture by playback id " + playbackId + " failed!");
            }
        } finally {
            boolean stopPlaybackSuccess = netSDK.NET_DVR_StopPlayBack(playbackId);
            if (!stopPlaybackSuccess) {
                error("Stop playback failed!id=" + playbackId);
            }
        }
    }

    public void setTimeCorrect() throws HIKException {
        if (uid < 0) {
            return;
        }
        NET_DVR_TIME nowTime = asSdkTime(new Date());

        if (!netSDK.NET_DVR_SetDVRConfig(uid, NET_DVR_SET_TIMECFG, -1, nowTime)) {
            error("time correct err! ");
        }
    }

    private NET_DVR_TIME asSdkTime(Date time) {
        NET_DVR_TIME sdkTime = new NET_DVR_TIME();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        sdkTime.dwYear = calendar.get(Calendar.YEAR);
        sdkTime.dwMonth = calendar.get(Calendar.MONTH) + 1;
        sdkTime.dwDay = calendar.get(Calendar.DAY_OF_MONTH);
        sdkTime.dwHour = calendar.get(Calendar.HOUR_OF_DAY);
        sdkTime.dwMinute = calendar.get(Calendar.MINUTE);
        sdkTime.dwSecond = calendar.get(Calendar.SECOND);
        return sdkTime;
    }

    private void error(String msg) throws HIKException {
        int errorCode = sdk.NET_DVR_GetLastError();
        throw new HIKException(msg + ",errorCode=" + errorCode);
    }
}
