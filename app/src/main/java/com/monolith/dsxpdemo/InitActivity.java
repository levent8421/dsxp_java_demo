package com.monolith.dsxpdemo;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.monolith.dsxp.tree.DsxpDeviceTree;
import com.monolith.dsxp.util.DeviceTreeUtils;
import com.monolith.dsxp.util.ExceptionUtils;
import com.monolith.dsxpdemo.dsxp.DeviceManager;
import com.monolith.dsxpdemo.util.ActivityUtils;
import com.monolith.dsxpdemo.util.AlertUtils;
import com.monolith.dsxpdemo.util.IOUtils;
import com.monolith.dsxpdemo.util.ToastUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Date: 2025/10/20 14:14
 * Author: Levent
 * 系统初始化
 */
public class InitActivity extends AppCompatActivity {
    private EditText etResource;
    private TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        initView();
        loadDsxpResource();
    }

    private void initView() {
        applyPermission();
        etResource = findViewById(R.id.et_resource);
        tvMsg = findViewById(R.id.tv_msg);
        findViewById(R.id.btn_parse).setOnClickListener(v -> parseResource());
    }


    private void applyPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                this.startActivity(intent);
            }
        } else {
            checkPermissions();
        }
    }

    private void checkPermissions() {
        requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                },
                0x3171FA);
    }

    private void loadDsxpResource() {
        try (InputStream resourceStream = getResources().getAssets().open(DemoConstants.DEFAULT_RESOURCE_ASSETS_FILE_NAME)) {
            String resource = IOUtils.readAsString(resourceStream);
            etResource.setText(resource);
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.show(this, ExceptionUtils.getMessage(e));
        }
    }

    private void parseResource() {
        String resource = etResource.getText().toString();
        DeviceManager deviceManager = DeviceManager.INSTANCE;
        deviceManager.parseDsxpResource(resource);
        DsxpDeviceTree deviceTree = deviceManager.getDeviceTree();
        int nodes = deviceTree.getNodeSize();
        tvMsg.setText("MSG:Build Success,nodes=" + nodes);
        showDeviceTreeDialog(deviceTree);
    }

    private void showDeviceTreeDialog(DsxpDeviceTree deviceTree) {
        try {
            String deviceTreeInfo = DeviceTreeUtils.dumpAsPrintable(deviceTree);
            AlertUtils.alert(this, "Device Tree", deviceTreeInfo, (d, w) -> {
                ActivityUtils.to(this, DashboardActivity.class);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}