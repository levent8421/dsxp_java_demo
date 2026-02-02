package com.monolith.dsxpdemo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.monolith.dsxp.util.ExceptionUtils;
import com.monolith.dsxpdemo.util.DateTimeInputUtils;
import com.monolith.dsxpdemo.util.EditTextUtils;
import com.monolith.dsxpdemo.util.ThreadUtils;
import com.monolith.hik.HIKException;
import com.monolith.hik.nvr.HIKDVRClient;
import com.monolith.hik.nvr.HIKDVROptions;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Create By YANYiZHI
 * Create Time: 2026/01/30 15:48
 * Description:
 * HIKNvrToolsActivity
 *
 * @author YANYiZHI
 */
public class HIKNvrToolsActivity extends AppCompatActivity {
    public static final String SDK_LOG_DIR = "/sdcard/demo/log/hik";

    private HIKDVRClient client;
    private ThreadPoolExecutor asyncTask;
    private Handler mainLooper;
    private TextView textLog;
    private EditText etIp;
    private EditText etPort;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etVideoDownloadSec;
    private TextView tvDatetime;
    private Date videoTimeEnd;
    private ProgressBar pbDownload;
    private VideoView vvVideo;
    private Spinner spChannels;
    private String downloadFullPath;
    private int downloadId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hik);
        initView();
    }

    private void initView() {
        this.textLog = findViewById(R.id.tv_log);
        this.etIp = findViewById(R.id.et_ip);
        this.etPort = findViewById(R.id.et_port);
        this.etUsername = findViewById(R.id.et_username);
        this.etPassword = findViewById(R.id.et_password);
        this.etVideoDownloadSec = findViewById(R.id.et_download_sec);
        this.vvVideo = findViewById(R.id.vv_video);
        this.pbDownload = findViewById(R.id.pb_download);
        this.spChannels = findViewById(R.id.sp_channels);

        findViewById(R.id.btn_init).setOnClickListener(v -> this.initSDK());
        findViewById(R.id.btn_login).setOnClickListener(v -> this.login());
        findViewById(R.id.btn_logout).setOnClickListener(v -> this.logout());
        findViewById(R.id.btn_download).setOnClickListener(v -> this.download());
        findViewById(R.id.btn_play).setOnClickListener(v -> this.playVideo());
        findViewById(R.id.btn_capture).setOnClickListener(v -> this.capture());
        findViewById(R.id.btn_time_correct).setOnClickListener(v -> this.timeCorrect());
        vvVideo.setOnClickListener(v -> this.playVideo());
        this.tvDatetime = findViewById(R.id.tv_datetime);
        this.tvDatetime.setOnClickListener(v -> this.pickDatetime());
        File crashFile = new File(SDK_LOG_DIR);
        if (!crashFile.exists()) {
            boolean mkdir = crashFile.mkdirs();
            System.out.println(mkdir);
        }
        asyncTask = ThreadUtils.createPool(1, "AsyncTask");
        mainLooper = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        asyncTask.shutdownNow();
    }

    private void initSDK() {

        asyncTask.execute(() -> {
            try {
                HIKDVROptions options = new HIKDVROptions();
                options.setLogDir(SDK_LOG_DIR);
                client = HIKDVRClient.getInstance(options);
                log("Init success!");
            } catch (Exception e) {
                e.printStackTrace();
                log("Init failed:" + ExceptionUtils.getMessage(e));
            }
        });
    }

    private void login() {
        if (client == null) {
            return;
        }
        String ip = EditTextUtils.getString(etIp, "192.168.1.94");
        int port = EditTextUtils.getNumber(etPort, 8000);
        String username = EditTextUtils.getString(etUsername, "admin");
        String password = EditTextUtils.getString(etPassword, "qwerasdzx!");
        asyncTask.execute(() -> {
            try {
                client.login(ip, port, username, password);
                log("Login Success:ip=" + ip + ",port=" + port + ",username=" + username + ",password=" + password);
                showChannelSpinner();
            } catch (Exception e) {
                log("Login Failed:ip=" + ip + ",port=" + port + ",username=" + username + ",password=" + password + "\nError=" + ExceptionUtils.getMessage(e));
                e.printStackTrace();
            }
        });
    }

    private void showChannelSpinner() {
        List<Integer> channels = client.getChannels();
        Integer[] channelArr = new Integer[channels.size()];
        for (int i = 0; i < channelArr.length; i++) {
            channelArr[i] = channels.get(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, channelArr);
        mainLooper.post(() -> spChannels.setAdapter(adapter));
    }

    private void logout() {
        if (client == null) {
            return;
        }
        asyncTask.execute(() -> {
            try {
                client.logout();
                log("Logout success!");
            } catch (Exception e) {
                e.printStackTrace();
                log("Logout failed:" + ExceptionUtils.getMessage(e));
            }
        });
    }

    private int getSelectedChannel() {
        long selectedItemId = spChannels.getSelectedItemId();
        List<Integer> channels = client.getChannels();
        return channels.get((int) selectedItemId);
    }

    private Date getSelectedTime() {
        if (videoTimeEnd == null) {
            videoTimeEnd = new Date();
        }
        return videoTimeEnd;
    }

    private String getTmpFile(String filename) {
        return "/sdcard/demo/" + filename;
    }

    private void download() {
        if (client == null) {
            return;
        }
        Date timeEnd = getSelectedTime();
        int sec = EditTextUtils.getNumber(etVideoDownloadSec, 5);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeEnd);
        calendar.add(Calendar.SECOND, -sec);
        Date videoTimeStart = calendar.getTime();
        downloadFullPath = getTmpFile("test" + ".mp4");
        int channel = getSelectedChannel();
        asyncTask.execute(() -> {
            try {
                String start = videoTimeStart.toString();
                String end = timeEnd.toString();
                log("Download start:" + start + " to " + end + "\nfile=" + downloadFullPath);
                this.downloadId = client.download(channel, videoTimeStart, videoTimeEnd, downloadFullPath);
                log("Download end,downloadId=" + downloadId);
            } catch (Exception e) {
                this.downloadId = -1;
                e.printStackTrace();
                log("Download failed:" + ExceptionUtils.getMessage(e));
            }
        });
        asyncTask.execute(() -> {
            int progress;
            do {
                if (downloadId < 0) {
                    return;
                }
                progress = client.getDownloadProgress(downloadId);
                updateDownloadProgress(progress);
                try {
                    ThreadUtils.sleepMs(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log("Get download progress error:" + ExceptionUtils.getMessage(e));
                }
            } while (progress < 100);
            playVideo();
        });
    }

    private void timeCorrect() {
        if (client == null) {
            return;
        }
        //使用后ntp会被禁用
        try {
            client.setTimeCorrect();
            log("set TimeCorrect success");
        } catch (Exception e) {
            log("set TimeCorrect err:" + ExceptionUtils.getMessage(e));
        }
    }

    private void updateDownloadProgress(int progress) {
        mainLooper.post(() -> pbDownload.setProgress(progress));
    }

    private void playVideo() {
        log("Play:" + downloadFullPath);
        mainLooper.post(() -> {
            vvVideo.setVideoPath(downloadFullPath);
            vvVideo.start();
        });
    }

    private void capture() {
        if (client == null) {
            return;
        }
        int channel = getSelectedChannel();
        Date selectedTime = getSelectedTime();
        String filename = getTmpFile(selectedTime.toString() + ".jpg");
        asyncTask.execute(() -> {
            try {
                client.captureImage(channel, HIKDVRClient.STREAM_MAIN, selectedTime, filename);
                log("Capture success,file=" + filename);
            } catch (HIKException e) {
                log("Capture failed:" + ExceptionUtils.getMessage(e));
                e.printStackTrace();
            }
        });
    }


    private void pickDatetime() {
        DateTimeInputUtils.showDatetimePickerDialog(this, new DateTimeInputUtils.DatetimePickerCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOK(Date date) {
                videoTimeEnd = date;
                tvDatetime.setText(date.toString());
            }
        });
    }

    private void log(String text) {
        CharSequence logChars = textLog.getText();
        String logStr;
        if (logChars == null) {
            logStr = text;
        } else {
            logStr = text + "\n" + logChars;
        }
        mainLooper.post(() -> textLog.setText(logStr));
    }
}
