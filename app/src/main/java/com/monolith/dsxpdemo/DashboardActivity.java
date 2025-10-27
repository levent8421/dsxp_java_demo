package com.monolith.dsxpdemo;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monolith.dsxp.driver.DsxpWorker;
import com.monolith.dsxp.event.DsxpEventContext;
import com.monolith.dsxp.event.DsxpEventIds;
import com.monolith.dsxp.event.dto.DauConnectionEventData;
import com.monolith.dsxp.tree.DsxpDeviceTree;
import com.monolith.dsxp.tree.DsxpDeviceTreeNode;
import com.monolith.dsxp.util.DeviceTreeUtils;
import com.monolith.dsxp.util.ListUtils;
import com.monolith.dsxp.warehouse.WarehouseManager;
import com.monolith.dsxp.warehouse.component.WarehouseComponent;
import com.monolith.dsxp.warehouse.event.InventoryUpdateEvent;
import com.monolith.dsxp.warehouse.event.WarehouseEventIds;
import com.monolith.dsxp.warehouse.utils.ComponentCode;
import com.monolith.dsxp.warehouse.utils.WarehouseComponentUtils;
import com.monolith.dsxp.warehouse.worker.WarehouseDau;
import com.monolith.dsxpdemo.adapter.WarehouseComponentListAdapter;
import com.monolith.dsxpdemo.dsxp.DeviceManager;
import com.monolith.dsxpdemo.dto.WarehouseComponentListItem;
import com.monolith.dsxpdemo.util.AlertUtils;

import java.math.BigDecimal;
import java.util.List;
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
        // 注册库存变化事件
        eventContext.registerHandler(WarehouseEventIds.INVENTORY_UPDATE, (node, event) -> {
            InventoryUpdateEvent data = (InventoryUpdateEvent) event.getData();
            onComponentInventoryUpdate(data);
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
        AlertUtils.alert(this, "Warehouse Info", warehouseInfo, null);
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
    }

    private void stopDriver() {
        DeviceManager deviceManager = DeviceManager.INSTANCE;
        deviceManager.stop();
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