package com.monolith.hik.connection;

import com.monolith.dsxp.DsxpException;
import com.monolith.dsxp.driver.DsxpConnectionTask;
import com.monolith.dsxp.driver.DsxpConnectionWorker;
import com.monolith.dsxp.driver.conf.DsxpConnectionPropConf;
import com.monolith.dsxp.driver.simple.conn.task.DauCallerTask;
import com.monolith.dsxp.driver.state.ConnectionState;
import com.monolith.dsxp.tree.DsxpConnectionNode;
import com.monolith.dsxp.tree.DsxpDeviceTreeNode;
import com.monolith.dsxp.util.ExceptionUtils;
import com.monolith.hik.HIKException;
import com.monolith.hik.nvr.HIKDVRClient;
import com.monolith.hik.util.NVRClientUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 2025/6/30 10:40
 * Author: Levent
 * Connection for HIK_NVR
 */
public class HIKNVRConnectionWorker implements DsxpConnectionWorker {
    private static final Logger log = LoggerFactory.getLogger(HIKNVRConnectionWorker.class);
    public static final String NAME = "hik_nvr";
    private final HIKNVRConnectionState state = new HIKNVRConnectionState();
    private final DsxpConnectionNode node;
    private final DauCallerTask dauCallerTask;
    private final HIKNVRConnectionConf conf;

    public HIKNVRConnectionWorker(DsxpConnectionNode node) {
        this.node = node;
        this.dauCallerTask = new DauCallerTask(node);
        this.conf = new HIKNVRConnectionConf(node.getConnDef());
    }

    @Override
    public DsxpConnectionTask createConnectionTask() {
        return dauCallerTask;
    }

    @Override
    public ConnectionState state() {
        return state;
    }

    @Override
    public DsxpConnectionPropConf getConnPropConf() {
        return null;
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

    public HIKDVRClient requireSDKClient() throws DsxpException {
        HIKDVRClient client = NVRClientUtils.getClient(node);
        if (client == null) {
            throw new DsxpException(node + ":SDK init failed!");
        }
        return client;
    }

    public void login() throws HIKException {
        try {
            HIKDVRClient client = requireSDKClient();
            client.login(conf.getHost(), conf.getPort(), conf.getUsername(), conf.getPassword());
        } catch (Exception e) {
            logout();
            throw new HIKException(ExceptionUtils.getMessage(e), e);
        }
    }

    public void logout() {
        HIKDVRClient client = NVRClientUtils.getClient(node);
        if (client == null) {
            return;
        }
        try {
            client.logout();
        } catch (Exception e) {
            log.error("{}:Logout failed:{}", node, ExceptionUtils.getMessage(e), e);
        }
    }
}
