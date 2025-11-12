package com.monolith.dsxpdemo.run;

/**
 * Create By YANYiZHI
 * Create Time: 2025/11/12 9:42
 * Description:
 * 设备层 健康度 包装
 *
 * @author YANYiZHI
 */
public class HealthDeviceModel {
    //设备物理地址
    private String deviceAddress;

    //设备类型
    private String deviceType;

    //发送的包的数量
    private String deviceTransmission;

    //成功百分比
    private String deviceHealthRate;

    //在线情况
    private String onlineState;

    //失败次数
    private String errors;

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceTransmission() {
        return deviceTransmission;
    }

    public void setDeviceTransmission(String deviceTransmission) {
        this.deviceTransmission = deviceTransmission;
    }

    public String getDeviceHealthRate() {
        return deviceHealthRate;
    }

    public void setDeviceHealthRate(String deviceHealthRate) {
        this.deviceHealthRate = deviceHealthRate;
    }

    public String getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(String onlineState) {
        this.onlineState = onlineState;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
