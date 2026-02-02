package com.monolith.hik.device;

import com.monolith.dsxp.DsxpException;
import com.monolith.dsxp.driver.state.DeviceWorkerState;
import com.monolith.dsxp.tree.DsxpDeviceNode;
import com.monolith.dsxp.util.NumberUtils;

/**
 * Date: 2025/6/30 10:54
 * Author: Levent
 * 相机设备
 */
public class HIKCameraDeviceWorker extends AbstractHIKDeviceWorker {
    public static final String NAME = "camera";
    private final HIKCameraDeviceState state;
    private final DsxpDeviceNode deviceNode;

    public HIKCameraDeviceWorker(DsxpDeviceNode node) {
        super(node);
        this.deviceNode = node;
        this.state = new HIKCameraDeviceState();
    }

    @Override
    public void update() throws DsxpException {

    }

    public int getChannelId() {
        String deviceId = deviceNode.getDeviceDef().getDeviceId();
        return NumberUtils.parseInt(deviceId, 0);
    }

    @Override
    public DeviceWorkerState state() {
        return state;
    }
}
