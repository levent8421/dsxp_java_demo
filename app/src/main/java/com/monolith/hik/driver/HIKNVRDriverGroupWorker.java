package com.monolith.hik.driver;

import com.monolith.dsxp.DsxpException;
import com.monolith.dsxp.driver.DsxpConnectionWorker;
import com.monolith.dsxp.driver.DsxpDauWorker;
import com.monolith.dsxp.driver.DsxpDeviceWorker;
import com.monolith.dsxp.driver.SimpleDsxpDriverGroupWorker;
import com.monolith.dsxp.driver.state.DriverGroupState;
import com.monolith.dsxp.tree.DsxpConnectionNode;
import com.monolith.dsxp.tree.DsxpDauNode;
import com.monolith.dsxp.tree.DsxpDeviceNode;
import com.monolith.dsxp.tree.DsxpDriverGroupNode;
import com.monolith.dsxp.util.DsxpEventUtils;
import com.monolith.dsxp.util.StringUtils;
import com.monolith.hik.HIKNVREventIds;
import com.monolith.hik.connection.HIKNVRConnectionWorker;
import com.monolith.hik.dau.HIKCameraDauWorker;
import com.monolith.hik.device.HIKCameraDeviceWorker;
import com.monolith.hik.nvr.HIKDVRClient;
import com.monolith.hik.nvr.HIKDVROptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 2025/6/27 17:29
 * Author: Levent
 * HIK NVR驱动
 */
public class HIKNVRDriverGroupWorker extends SimpleDsxpDriverGroupWorker {
    private static final Logger log = LoggerFactory.getLogger(HIKNVRDriverGroupWorker.class);
    private final HIKNVRDriverGroupConf conf;
    public static final String NAME = "hik_nvr";
    private HIKDVRClient client;

    public HIKNVRDriverGroupWorker(DsxpDriverGroupNode node) {
        super(node);
        conf = new HIKNVRDriverGroupConf();
    }

    @Override
    public DriverGroupState state() {
        return null;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void onBuildComplete() throws Exception {
        super.onBuildComplete();
    }

    @Override
    public DsxpConnectionWorker buildConnection(DsxpConnectionNode node) throws Exception {
        String subProtocol = node.getConnDef().getSubProtocol();
        HIKNVRConnectionWorker worker;
        if (StringUtils.equalsIgnoreCase(subProtocol, HIKNVRConnectionWorker.NAME)) {
            worker = new HIKNVRConnectionWorker(node);
        } else {
            throw new DsxpException("Can not resolve connection subProtocol:" + subProtocol);
        }
        return worker;
    }

    @Override
    public DsxpDauWorker buildDau(DsxpDauNode node) throws Exception {
        String dauType = node.getDauDef().getDauType();
        DsxpDauWorker worker;
        if (StringUtils.equalsIgnoreCase(dauType, HIKCameraDauWorker.NAME)) {
            worker = new HIKCameraDauWorker(node);
        } else {
            throw new DsxpException("Can not resolve:" + dauType);
        }
        return worker;
    }

    @Override
    public DsxpDeviceWorker buildDevice(DsxpDeviceNode node) throws Exception {
        String deviceType = node.getDeviceDef().getDeviceType();
        DsxpDeviceWorker worker;
        if (StringUtils.equalsIgnoreCase(deviceType, HIKCameraDeviceWorker.NAME)) {
            worker = new HIKCameraDeviceWorker(node);
        } else {
            throw new DsxpException("Can not resolve:" + deviceType);
        }
        return worker;
    }

    @Override
    public void convertProps() {
        conf.loadFromNode();
    }

    public HIKDVRClient obtainDVRClient() {
        if (client != null) {
            return client;
        }
        synchronized (this) {
            if (client != null) {
                return client;
            }
            client = initDVRClient();
            return client;
        }
    }

    private HIKDVRClient initDVRClient() {
        HIKDVROptions options = new HIKDVROptions();
        String logDir = conf.getLogDir();
        options.setLogDir(logDir);
        DsxpDriverGroupNode node = node();
        try {
            HIKDVRClient instance = HIKDVRClient.getInstance(options);
            DsxpEventUtils.emit(node, HIKNVREventIds.SDK_INITIALIZED, instance, false);
            return instance;
        } catch (Exception e) {
            log.error("Error on init SDK!", e);
            DsxpEventUtils.emit(node, HIKNVREventIds.SDK_INIT_FAILED, e, false);
            return null;
        }
    }
}
