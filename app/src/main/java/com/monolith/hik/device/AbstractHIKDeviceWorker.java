package com.monolith.hik.device;

import com.monolith.dsxp.driver.DsxpDeviceWorker;
import com.monolith.dsxp.tree.DsxpDeviceNode;
import com.monolith.dsxp.tree.DsxpDeviceTreeNode;

/**
 * Date: 2025/6/30 10:54
 * Author: Levent
 * HIK设备基类
 */
public abstract class AbstractHIKDeviceWorker implements DsxpDeviceWorker {
    private final DsxpDeviceNode node;

    public AbstractHIKDeviceWorker(DsxpDeviceNode node) {
        this.node = node;
    }

    @Override
    public void onCreate() throws Exception {

    }

    @Override
    public void onBuildComplete() throws Exception {

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
}
