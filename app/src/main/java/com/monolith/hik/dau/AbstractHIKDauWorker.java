package com.monolith.hik.dau;

import com.monolith.dsxp.driver.DsxpWorker;
import com.monolith.dsxp.driver.simple.dau.LifecycleDauWorker;
import com.monolith.dsxp.tree.DsxpConnectionNode;
import com.monolith.dsxp.tree.DsxpDauNode;
import com.monolith.dsxp.tree.DsxpDeviceTreeNode;
import com.monolith.dsxp.util.DeviceTreeUtils;
import com.monolith.hik.connection.HIKNVRConnectionWorker;
import com.monolith.hik.nvr.HIKDVRClient;

/**
 * Date: 2025/6/30 10:49
 * Author: Levent
 * 海康采集点基类
 */
public abstract class AbstractHIKDauWorker implements LifecycleDauWorker {
    private final DsxpDauNode node;

    protected AbstractHIKDauWorker(DsxpDauNode node) {
        this.node = node;
    }

    @Override
    public void onCycleStart() throws Exception {

    }

    @Override
    public void onCycleEnd() throws Exception {

    }

    @Override
    public void onCreate() throws Exception {

    }

    @Override
    public void convertProps() {

    }

    @Override
    public void onDestroy() throws Exception {

    }

    @Override
    public DsxpDeviceTreeNode node() {
        return node;
    }

    public HIKNVRConnectionWorker getNVR() {
        DsxpConnectionNode connectionNode = DeviceTreeUtils.findParentNode(node, DsxpConnectionNode.class);
        DsxpWorker worker = connectionNode.getWorker();
        return (HIKNVRConnectionWorker) worker;
    }

    public void runNVRTask(HIKNVRTask task) {
        HIKNVRConnectionWorker nvr = getNVR();
        try {
            nvr.login();
            HIKDVRClient client = nvr.requireSDKClient();
            task.nvrTaskRun(client);
        } catch (Exception e) {
            task.onTaskError(e);
        } finally {
            nvr.logout();
        }
    }
}
