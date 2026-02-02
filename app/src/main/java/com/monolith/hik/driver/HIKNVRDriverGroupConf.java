package com.monolith.hik.driver;

import com.monolith.hik.util.StorageUtils;

/**
 * Date: 2025/6/30 11:16
 * Author: Levent
 * 驱动配置
 */
public class HIKNVRDriverGroupConf {
    private String logDir = StorageUtils.getStorageDir("/log/hik").getAbsolutePath();

    public void loadFromNode() {

    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }
}
