package com.monolith.dsxpdemo.run;

import java.util.List;

/**
 * Create By YANYiZHI
 * Create Time: 2025/11/12 9:42
 * Description:
 * 连接层 健康度 包装
 *
 * @author YANYiZHI
 */
public class HealthConnModel {
    private String conn;
    private List<HealthDeviceModel> deviceModels;

    public String getConn() {
        return conn;
    }

    public void setConn(String conn) {
        this.conn = conn;
    }

    public List<HealthDeviceModel> getDeviceModels() {
        return deviceModels;
    }

    public void setDeviceModels(List<HealthDeviceModel> deviceModels) {
        this.deviceModels = deviceModels;
    }
}
