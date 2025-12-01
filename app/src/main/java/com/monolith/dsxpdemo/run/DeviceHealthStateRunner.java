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

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Create By YANYiZHI
 * Create Time: 2025/12/01 10:16
 * Description:
 * DeviceHealthStateRunner
 *
 * @author YANYiZHI
 */
public class DeviceHealthStateRunner implements Runnable {
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
                for (DsxpDriverGroupNode groupNode : roots.values()) {
                    Map<DsxpConnectionDefinition, DsxpConnectionNode> connNodes = groupNode.getConnNodes();
                    for (Map.Entry<DsxpConnectionDefinition, DsxpConnectionNode> connectionNodeEntry : connNodes.entrySet()) {
                        if (connectionNodeEntry.getValue().getConnectionWorker() instanceof StubDsxpConnectionWorker) {
                            continue;
                        }
                        Map<DsxpDauDefinition, DsxpDauNode> dauNodes = connectionNodeEntry.getValue().getDauNodes();
                        for (Map.Entry<DsxpDauDefinition, DsxpDauNode> dauNodeEntry : dauNodes.entrySet()) {
                            if (dauNodeEntry.getValue().getDauWorker() instanceof StubDsxpDauWorker) {
                                continue;
                            }
                            Map<DsxpDeviceDefinition, DsxpDeviceNode> deviceNodes = dauNodeEntry.getValue().getDeviceNodes();
                            recursiveTraversalDevice(deviceNodes);
                        }
                    }
                }
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


    private void recursiveTraversalDevice(Map<DsxpDeviceDefinition, DsxpDeviceNode> deviceNodes) {
        for (Map.Entry<DsxpDeviceDefinition, DsxpDeviceNode> deviceNodeEntry : deviceNodes.entrySet()) {
            DsxpDeviceDefinition deviceDef = deviceNodeEntry.getValue().getDeviceDef();
            DsxpDeviceWorker deviceWorker = deviceNodeEntry.getValue().getDeviceWorker();
            DeviceWorkerState state = deviceWorker.state();

            if (deviceWorker instanceof StubDsxpDeviceWorker || state == null) {
                System.out.println("设备：" + deviceDef.identifier() + " 初始化异常");
                continue;
            }

            boolean online = state.isOnline();
            System.out.println("设备：" + deviceDef.identifier() + "是否在线：" + online);
            PacketCounter packetCounter = state.getPacketCounter();
            if (packetCounter == null) {
                continue;
            }
            //设备离线 error 增加
            long errors = packetCounter.getErrors();
            //设备正常 success 增加
            long success = packetCounter.getSuccess();
            //健康度就是：success / (success + errors) 的百分比
            System.out.println("设备: " + deviceDef.identifier() + "发送错误包数：" + errors + " 成功包数：" + success);

            Map<DsxpDeviceDefinition, DsxpDeviceNode> childrenDeviceNodes = deviceNodeEntry.getValue().getChildrenDeviceNodes();
            if (!childrenDeviceNodes.isEmpty()) {
                recursiveTraversalDevice(childrenDeviceNodes);
            }
        }
    }

    public void stop() {
        running = false;
        if (thread != null) {
            thread.shutdown();
        }
        thread = null;
    }
}
