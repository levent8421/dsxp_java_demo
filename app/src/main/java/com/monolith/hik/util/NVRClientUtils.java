package com.monolith.hik.util;

import com.monolith.dsxp.tree.DsxpDeviceTreeNode;
import com.monolith.dsxp.tree.DsxpDriverGroupNode;
import com.monolith.dsxp.util.DeviceTreeUtils;
import com.monolith.hik.driver.HIKNVRDriverGroupWorker;
import com.monolith.hik.nvr.HIKDVRClient;

/**
 * Date: 2025/6/30 15:03
 * Author: Levent
 * Client Utils
 */
public class NVRClientUtils {
    public static HIKDVRClient getClient(DsxpDeviceTreeNode node) {
        DsxpDriverGroupNode driverGroupNode = DeviceTreeUtils.findParentNode(node, DsxpDriverGroupNode.class);
        HIKNVRDriverGroupWorker driverGroupWorker = (HIKNVRDriverGroupWorker) driverGroupNode.getWorker();
        return driverGroupWorker.obtainDVRClient();
    }
}
