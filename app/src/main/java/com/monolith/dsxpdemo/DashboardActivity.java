package com.monolith.dsxpdemo;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monolith.dsxp.define.DsxpConnectionDefinition;
import com.monolith.dsxp.define.DsxpDriverGroupDefinition;
import com.monolith.dsxp.driver.DsxpBroadcastDeviceWorker;
import com.monolith.dsxp.driver.DsxpConnectionWorker;
import com.monolith.dsxp.driver.DsxpWorker;
import com.monolith.dsxp.event.DsxpEventContext;
import com.monolith.dsxp.event.DsxpEventIds;
import com.monolith.dsxp.event.dto.DauConnectionEventData;
import com.monolith.dsxp.jtrfid.RfidEvents;
import com.monolith.dsxp.jtrfid.worker.dto.HFDauData;
import com.monolith.dsxp.tree.DsxpConnectionNode;
import com.monolith.dsxp.tree.DsxpDeviceTree;
import com.monolith.dsxp.tree.DsxpDeviceTreeNode;
import com.monolith.dsxp.tree.DsxpDriverGroupNode;
import com.monolith.dsxp.util.DeviceTreeUtils;
import com.monolith.dsxp.util.ListUtils;
import com.monolith.dsxp.warehouse.WarehouseManager;
import com.monolith.dsxp.warehouse.component.Shelf;
import com.monolith.dsxp.warehouse.component.ShelfBin;
import com.monolith.dsxp.warehouse.component.ShelfLayer;
import com.monolith.dsxp.warehouse.component.WarehouseComponent;
import com.monolith.dsxp.warehouse.component.conf.WarehouseSku;
import com.monolith.dsxp.warehouse.event.AccessControlStateEvent;
import com.monolith.dsxp.warehouse.event.InventoryUpdateEvent;
import com.monolith.dsxp.warehouse.event.WarehouseEventIds;
import com.monolith.dsxp.warehouse.utils.ComponentCode;
import com.monolith.dsxp.warehouse.utils.ComponentCodes;
import com.monolith.dsxp.warehouse.utils.ComponentConfUtils;
import com.monolith.dsxp.warehouse.utils.WarehouseComponentUtils;
import com.monolith.dsxp.warehouse.worker.AccessControlDau;
import com.monolith.dsxp.warehouse.worker.DauContainer;
import com.monolith.dsxp.warehouse.worker.WarehouseDau;
import com.monolith.dsxpdemo.adapter.WarehouseComponentListAdapter;
import com.monolith.dsxpdemo.dsxp.DeviceManager;
import com.monolith.dsxpdemo.dto.WarehouseComponentListItem;
import com.monolith.dsxpdemo.run.DeviceHealthStateRunner;
import com.monolith.dsxpdemo.util.ActivityUtils;
import com.monolith.dsxpdemo.util.AlertUtils;
import com.monolith.mit.dsp.MitDspEvents;
import com.monolith.mit.dsp.worker.dau.io.DspLockerDauWorker;
import com.monolith.mit.dsp.worker.dau.wt.event.TraceWeightUpdateEventData;
import com.monolith.mit.dsp.worker.dau.wt.event.WeightInventoryUpdateEventData;
import com.monolith.mit.dsp.worker.device.broadcast.DspBroadcastDeviceWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Date: 2025/10/20 17:35
 * Author: Levent
 * 看板页面
 */
public class DashboardActivity extends AppCompatActivity {
    private RecyclerView rvComponents;

    private WarehouseComponentListAdapter componentListAdapter;
    private final Handler handler = new Handler();
    private final DeviceHealthStateRunner healthStateRunner = new DeviceHealthStateRunner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initView();
        initEvent();
    }

    private void initView() {
        findViewById(R.id.btn_show_tree).setOnClickListener(v -> showDeviceTree());
        findViewById(R.id.btn_show_warehouse).setOnClickListener(v -> showWarehouse());
        findViewById(R.id.btn_build_driver).setOnClickListener(v -> buildDriver());
        findViewById(R.id.btn_start).setOnClickListener(v -> startDriver());
        findViewById(R.id.btn_stop).setOnClickListener(v -> stopDriver());
        //findViewById(R.id.btn_do_zero).setOnClickListener(v -> doZeroAll());
        findViewById(R.id.btn_open_lock).setOnClickListener(v -> openLock());
        findViewById(R.id.btn_close_lock).setOnClickListener(v -> closeLock());
        findViewById(R.id.btn_worksheet).setOnClickListener(v -> toWorksheet());
        this.rvComponents = findViewById(R.id.rvComponents);
        this.rvComponents.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initEvent() {
        DsxpDeviceTree deviceTree = DeviceManager.INSTANCE.getDeviceTree();
        DsxpEventContext eventContext = deviceTree.getEventContext();
        // 注册连接状态变化事件
        eventContext.registerHandler(DsxpEventIds.DAU_CONNECTION_STATE, (node, event) -> {
            DauConnectionEventData data = (DauConnectionEventData) event.getData();
            onComponentStateUpdate(node, data.isOnline());
        });
        // 注册标准库存变化事件(库存变化就看这个就行了)
        eventContext.registerHandler(WarehouseEventIds.INVENTORY_UPDATE, (node, event) -> {
            InventoryUpdateEvent data = (InventoryUpdateEvent) event.getData();
            System.out.println(data.getCode().asString() + "：标准库存变化 ==》" + data.getInvDelta());
        });
        // 注册重力库位库存更新事件
        eventContext.registerHandler(MitDspEvents.WT_INVENTORY, (node, event) -> {
            WeightInventoryUpdateEventData data = (WeightInventoryUpdateEventData) event.getData();
            DsxpWorker worker = node.getWorker();
            if (!(worker instanceof WarehouseDau)) {
                return;
            }
            WarehouseDau dau = (WarehouseDau) worker;
            WarehouseComponent component = dau.getComponent();
            ComponentCode code = component.code();
            System.out.println(code + "：重力库位库存更新 ==》" + "跟踪库存变化：" + data.getInventoryDelta() + "    测量库存变化：" + data.getMeasuredInventoryDelta());
        });
        // 重量跟踪事件
        eventContext.registerHandler(MitDspEvents.WT_TRACE_WEIGHT_UPDATE, ((node, event) -> {
            TraceWeightUpdateEventData data = (TraceWeightUpdateEventData) event.getData();
            System.out.println("库位：" + node.getDef().identifier() + "变化" + data.getWeightDelta());
        }));
        //刷卡事件（1s一次上报 自行过滤）
        eventContext.registerHandler(RfidEvents.RFID_CARD_PRESS, (node, event) -> {
            HFDauData eventValue = (HFDauData) event.getData();
            System.out.println(eventValue.getEpc());
        });
        /**
         * LOCKED = 0x01;
         * UNLOCKED = 0x03;
         */
        eventContext.registerHandler(WarehouseEventIds.ACCESS_CONTROL_STATE, (node, event) -> {
            AccessControlStateEvent controlStateEvent = (AccessControlStateEvent) event.getData();
            System.out.println(controlStateEvent.getCode().asString() + "状态变更 ：===》" + controlStateEvent.getStateCode());
        });
    }

    private void showDeviceTree() {
        // 查看设备树结构
        DsxpDeviceTree deviceTree = DeviceManager.INSTANCE.getDeviceTree();
        try {
            AlertUtils.alert(this, "Device Tree", DeviceTreeUtils.dumpAsPrintable(deviceTree), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWarehouse() {
        // 查看仓库结构
        WarehouseManager warehouseManager = DeviceManager.INSTANCE.getWarehouseManager();
        String warehouseInfo = WarehouseComponentUtils.dumpAsPrintable(warehouseManager.getWarehouse());

        //获取货架/层/库位信息
        List<WarehouseComponent> allComponents = warehouseManager.getAllComponents();
        List<Shelf> shelfList = new ArrayList<>();
        List<ShelfLayer> shelfLayerList = new ArrayList<>();
        List<ShelfBin> shelfBinList = new ArrayList<>();
        for (WarehouseComponent component : allComponents) {
            if (component instanceof Shelf) {
                //货架
                Shelf shelf = (Shelf) component;
                shelfList.add(shelf);
            }
            if (component instanceof ShelfLayer) {
                //层
                ShelfLayer shelfLayer = (ShelfLayer) component;
                shelfLayerList.add(shelfLayer);
            }
            if (component instanceof ShelfBin) {
                //库位
                ShelfBin shelfBin = (ShelfBin) component;
                shelfBinList.add(shelfBin);
                //获取库位绑定的sku信息
                WarehouseSku skuConf = ComponentConfUtils.getSkuConf(component);
                if (skuConf != null) {
                    //sku
                }
            }
        }

        AlertUtils.alert(this, "Warehouse Info", warehouseInfo, null);
    }

    private void toWorksheet() {
        ActivityUtils.to(this, WorksheetActivity.class);
    }

    private void buildDriver() {
        // 构建驱动
        DeviceManager deviceManager = DeviceManager.INSTANCE;
        deviceManager.buildDrivers();
        refreshBinList();
    }

    private void refreshBinList() {
        // 从驱动中获取各个库位、层、货架的状态
        WarehouseManager warehouseManager = DeviceManager.INSTANCE.getWarehouseManager();
        List<WarehouseComponent> components = WarehouseComponentUtils.getAllChildren(warehouseManager.getWarehouse());
        List<WarehouseComponentListItem> items = ListUtils.newArrayList();
        for (WarehouseComponent component : components) {
            WarehouseComponentListItem item = new WarehouseComponentListItem(component);
            items.add(item);
        }
        componentListAdapter = new WarehouseComponentListAdapter(this, items);
        rvComponents.setAdapter(componentListAdapter);
    }

    private void startDriver() {
        DeviceManager deviceManager = DeviceManager.INSTANCE;
        deviceManager.start();
        /**
         * 这边我偷懒直接在驱动启动后就执行 实际项目中 页面需要的时候再启动 页面不需要的时候关掉 减少资源开销 上位机主动获取的时候主动调一下run中方法返回参数即可
         */
        healthStateRunner.run();
    }

    private void stopDriver() {
        DeviceManager deviceManager = DeviceManager.INSTANCE;
        deviceManager.stop();
    }

    private void doZeroAll() {
        DeviceManager deviceManager = DeviceManager.INSTANCE;
        DsxpDeviceTree deviceTree = deviceManager.getDeviceTree();
        Map<DsxpDriverGroupDefinition, DsxpDriverGroupNode> roots = deviceTree.getRoots();
        for (Map.Entry<DsxpDriverGroupDefinition, DsxpDriverGroupNode> groupNodeEntry : roots.entrySet()) {
            Map<DsxpConnectionDefinition, DsxpConnectionNode> connNodes = groupNodeEntry.getValue().getConnNodes();
            for (Map.Entry<DsxpConnectionDefinition, DsxpConnectionNode> connectionNodeEntry : connNodes.entrySet()) {
                final DsxpConnectionNode connectionNode = connectionNodeEntry.getValue();
                final DsxpConnectionWorker connectionWorker = connectionNode.getConnectionWorker();
                DsxpBroadcastDeviceWorker broadcastDevice = connectionWorker.getBroadcastDevice();
                if (broadcastDevice instanceof DspBroadcastDeviceWorker) {
                    DspBroadcastDeviceWorker broadcastDeviceWorker = (DspBroadcastDeviceWorker) broadcastDevice;
                    try {
                        broadcastDeviceWorker.broadcastDoZero();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void openLock() {
        //这边传入想开的锁的code
        WarehouseComponent component = DeviceManager.INSTANCE.getWarehouseManager().findComponent(ComponentCodes.parseCode("L1-1-1"));
        DauContainer<AccessControlDau> accessControlDaus = component.getHardwareBinding().getAccessControlDaus();
        if (accessControlDaus.isEmpty()) {
            System.out.println("no this lock");
            return;
        }
        //打开这个code下所有锁
        for (AccessControlDau value : accessControlDaus.values()) {
            if (value instanceof DspLockerDauWorker) {
                DspLockerDauWorker lockerDauWorker = (DspLockerDauWorker) value;
                try {
                    lockerDauWorker.unlock();
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    private void closeLock() {
        //这边传入想关的锁的code
        WarehouseComponent component = DeviceManager.INSTANCE.getWarehouseManager().findComponent(ComponentCodes.parseCode("L1-1-1"));
        DauContainer<AccessControlDau> accessControlDaus = component.getHardwareBinding().getAccessControlDaus();
        if (accessControlDaus.isEmpty()) {
            System.out.println("no this lock");
            return;
        }
        //关闭这个code下所有锁
        for (AccessControlDau value : accessControlDaus.values()) {
            if (value instanceof DspLockerDauWorker) {
                DspLockerDauWorker lockerDauWorker = (DspLockerDauWorker) value;
                try {
                    lockerDauWorker.lock();
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    private void onComponentStateUpdate(DsxpDeviceTreeNode node, Boolean online) {
        DsxpWorker worker = node.getWorker();
        if (!(worker instanceof WarehouseDau)) {
            return;
        }
        WarehouseDau dau = (WarehouseDau) worker;
        WarehouseComponent component = dau.getComponent();
        ComponentCode code = component.code();
        int position = getPosition(code);
        WarehouseComponentListItem item = componentListAdapter.getItem(position);
        item.setOnline(online);
        handler.post(() -> componentListAdapter.notifyItemChanged(position));
    }

    private void onComponentInventoryUpdate(InventoryUpdateEvent event) {
        ComponentCode code = event.getCode();
        int position = getPosition(code);
        WarehouseComponentListItem item = componentListAdapter.getItem(position);
        BigDecimal inv = event.getInvEnd();
        Object sensorData = event.getSensorDataEnd();
        item.setInventory(inv.toString() + " PCS");
        item.setWeight(sensorData.toString() + " Kg");
        handler.post(() -> componentListAdapter.notifyItemChanged(position));
    }

    private int getPosition(ComponentCode code) {
        for (int i = 0; i < componentListAdapter.getItemCount(); i++) {
            WarehouseComponentListItem item = componentListAdapter.getItem(i);
            WarehouseComponent component = item.getComponent();
            if (Objects.equals(component.code(), code)) {
                return i;
            }
        }
        return -1;
    }
}