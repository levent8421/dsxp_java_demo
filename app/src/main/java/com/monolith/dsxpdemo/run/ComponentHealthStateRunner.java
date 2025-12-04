package com.monolith.dsxpdemo.run;

import com.monolith.dsxp.define.DsxpNodeDefinition;
import com.monolith.dsxp.driver.DsxpDeviceWorker;
import com.monolith.dsxp.driver.state.DeviceWorkerState;
import com.monolith.dsxp.tree.DsxpDeviceTreeNode;
import com.monolith.dsxp.util.PacketCounter;
import com.monolith.dsxp.warehouse.WarehouseManager;
import com.monolith.dsxp.warehouse.component.ShelfBin;
import com.monolith.dsxp.warehouse.component.WarehouseComponent;
import com.monolith.dsxp.warehouse.worker.WarehouseDau;
import com.monolith.dsxpdemo.dsxp.DeviceManager;
import com.monolith.dsxpdemo.util.ThreadUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Create By YANYiZHI
 * Create Time: 2025/11/12 9:34
 * Description:
 * ComponentHealthStateRunner
 *
 * @author YANYiZHI
 */
public class ComponentHealthStateRunner implements Runnable {
    private static final int TICK_INTERVAL = 2 * 1000;
    private volatile boolean running = false;
    private ThreadPoolExecutor thread = ThreadUtils.createPool(1, "ComponentHealthStateRunner");

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                WarehouseManager warehouseManager = DeviceManager.INSTANCE.getWarehouseManager();
                List<WarehouseComponent> allComponents = warehouseManager.getAllComponents();
                //这边一直在获取设备的健康度 生产环境中可以搞个回调给页面展示 也可以在到达某个阈值时发出警报给上位机
                for (WarehouseComponent component : allComponents) {
                    if (component instanceof ShelfBin) {
                        boolean online = component.getState().getHardwareState().isInvDauOnline() &&
                                component.getState().getHardwareState().isInteractionDauOnline() &&
                                component.getState().getHardwareState().isIdentificationDauOnline() &&
                                component.getState().getHardwareState().isAccessControlDauOnline();
                        System.out.println("库位：" + component.code().asString() + "是否在线：" + online);
                        List<WarehouseDau> daus = component.getHardwareBinding().getDaus();
                        for (WarehouseDau dau : daus) {
                            DsxpDeviceTreeNode node = dau.node();
                            Map<? extends DsxpNodeDefinition, ? extends DsxpDeviceTreeNode> children = node.getChildren();
                            recursiveTraversalDevice(children);
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

    private void recursiveTraversalDevice(Map<? extends DsxpNodeDefinition, ? extends DsxpDeviceTreeNode> children) {
        for (DsxpDeviceTreeNode deviceTreeNode : children.values()) {
            DsxpDeviceWorker worker = (DsxpDeviceWorker) deviceTreeNode.getWorker();
            DeviceWorkerState state = worker.state();
            boolean deviceOnline = state.isOnline();
            System.out.println("设备：" + deviceTreeNode.identifier() + "是否在线：" + deviceOnline);
            PacketCounter packetCounter = state.getPacketCounter();
            if (packetCounter == null) {
                continue;
            }
            //设备离线 error 增加
            long errors = packetCounter.getErrors();
            //设备正常 success 增加
            long success = packetCounter.getSuccess();
            //健康度就是：success / (success + errors) 的百分比
            System.out.println("设备: " + deviceTreeNode.identifier() + "发送错误包数：" + errors + " 成功包数：" + success);
            Map<? extends DsxpNodeDefinition, ? extends DsxpDeviceTreeNode> nodeChildren = deviceTreeNode.getChildren();
            if (!nodeChildren.isEmpty()) {
                recursiveTraversalDevice(nodeChildren);
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
