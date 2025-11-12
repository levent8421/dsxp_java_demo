package com.monolith.dsxpdemo.run;

import com.monolith.dsxp.define.DsxpConnectionDefinition;
import com.monolith.dsxp.define.DsxpDauDefinition;
import com.monolith.dsxp.define.DsxpDeviceDefinition;
import com.monolith.dsxp.define.DsxpDriverGroupDefinition;
import com.monolith.dsxp.driver.DsxpDeviceWorker;
import com.monolith.dsxp.driver.state.DeviceWorkerState;
import com.monolith.dsxp.driver.stub.StubDsxpConnectionWorker;
import com.monolith.dsxp.driver.stub.StubDsxpDauWorker;
import com.monolith.dsxp.driver.stub.StubDsxpDeviceWorker;
import com.monolith.dsxp.tree.DsxpConnectionNode;
import com.monolith.dsxp.tree.DsxpDauNode;
import com.monolith.dsxp.tree.DsxpDeviceNode;
import com.monolith.dsxp.tree.DsxpDeviceTree;
import com.monolith.dsxp.tree.DsxpDriverGroupNode;
import com.monolith.dsxp.util.PacketCounter;
import com.monolith.dsxpdemo.dsxp.DeviceManager;
import com.monolith.dsxpdemo.util.ThreadUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Create By YANYiZHI
 * Create Time: 2025/11/12 9:34
 * Description:
 * DeviceHealthStateRunner
 *
 * @author YANYiZHI
 */
public class DeviceHealthStateRunner implements Runnable{
    private static final int TICK_INTERVAL = 2 * 1000;
    private volatile boolean running = false;
    private ThreadPoolExecutor thread = ThreadUtils.createPool(1, "DeviceHealthStateRunner");

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                DsxpDeviceTree dsxpDeviceTree = DeviceManager.INSTANCE.getDeviceTree();
                if (dsxpDeviceTree == null) {
                    continue;
                }
                Map<DsxpDriverGroupDefinition, DsxpDriverGroupNode> roots = dsxpDeviceTree.getRoots();
                List<HealthConnModel> list = new ArrayList<>();
                for (DsxpDriverGroupNode groupNode : roots.values()) {
                    Map<DsxpConnectionDefinition, DsxpConnectionNode> connNodes = groupNode.getConnNodes();
                    for (Map.Entry<DsxpConnectionDefinition, DsxpConnectionNode> connectionNodeEntry : connNodes.entrySet()) {
                        if (connectionNodeEntry.getValue().getConnectionWorker() instanceof StubDsxpConnectionWorker) {
                            continue;
                        }
                        String identifier = connectionNodeEntry.getValue().getConnDef().identifier();
                        HealthConnModel healthConnModel = new HealthConnModel();
                        List<HealthDeviceModel> deviceList = new ArrayList<>();
                        healthConnModel.setConn(identifier);
                        healthConnModel.setDeviceModels(deviceList);
                        list.add(healthConnModel);
                        Map<DsxpDauDefinition, DsxpDauNode> dauNodes = connectionNodeEntry.getValue().getDauNodes();
                        for (Map.Entry<DsxpDauDefinition, DsxpDauNode> dauNodeEntry : dauNodes.entrySet()) {
                            if (dauNodeEntry.getValue().getDauWorker() instanceof StubDsxpDauWorker) {
                                continue;
                            }
                            Map<DsxpDeviceDefinition, DsxpDeviceNode> deviceNodes = dauNodeEntry.getValue().getDeviceNodes();
                            recursiveTraversalDevice(deviceNodes, healthConnModel, identifier);
                        }
                    }
                }
                //这个list 就是设备的健康度 可以搞个回调给页面展示 也可以在到达某个阈值时发出警报给上位机
                list.sort(Comparator.comparing(HealthConnModel::getConn));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ThreadUtils.sleepMs(TICK_INTERVAL);
            } catch (Exception e) {
                stop();
            }
        }
        running = false;
    }

    public void stop() {
        running = false;
        if (thread != null){
            thread.shutdown();
        }
        thread = null;
    }

    private void recursiveTraversalDevice(Map<DsxpDeviceDefinition, DsxpDeviceNode> deviceNodes, HealthConnModel healthConnModel, String connIdentifier) {
        for (Map.Entry<DsxpDeviceDefinition, DsxpDeviceNode> deviceNodeEntry : deviceNodes.entrySet()) {
            HealthDeviceModel healthDeviceModel = new HealthDeviceModel();
            healthConnModel.getDeviceModels().add(healthDeviceModel);
            DsxpDeviceDefinition deviceDef = deviceNodeEntry.getValue().getDeviceDef();
            DsxpDeviceWorker deviceWorker = deviceNodeEntry.getValue().getDeviceWorker();

            healthDeviceModel.setDeviceAddress(deviceDef.identifier().replace(connIdentifier, ""));
            healthDeviceModel.setDeviceType(deviceDef.getDeviceType());
            DeviceWorkerState state = deviceWorker.state();

            if (deviceWorker instanceof StubDsxpDeviceWorker || state == null) {
                healthDeviceModel.setDeviceTransmission("--/--");
                healthDeviceModel.setDeviceHealthRate("--");
                healthDeviceModel.setOnlineState("offline");
                healthDeviceModel.setErrors("--");
                continue;
            }

            boolean online = state.isOnline();
            PacketCounter packetCounter = state.getPacketCounter();
            if (packetCounter == null) {
                continue;
            }
            long errors = packetCounter.getErrors();
            long success = packetCounter.getSuccess();

            healthDeviceModel.setDeviceTransmission(success + "/" + (success + errors));
            healthDeviceModel.setDeviceHealthRate((success + errors) == 0 ? ("0" + "%") : ((success * 100) / (success + errors)) + "%");
            healthDeviceModel.setOnlineState(online ? "online" : "offline");
            healthDeviceModel.setErrors(String.valueOf((errors)));
            Map<DsxpDeviceDefinition, DsxpDeviceNode> childrenDeviceNodes = deviceNodeEntry.getValue().getChildrenDeviceNodes();
            if (!childrenDeviceNodes.isEmpty()) {
                recursiveTraversalDevice(childrenDeviceNodes, healthConnModel, connIdentifier);
            }
        }
    }
}
