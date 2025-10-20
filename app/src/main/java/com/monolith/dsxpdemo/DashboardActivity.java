package com.monolith.dsxpdemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monolith.dsxp.event.DsxpEventContext;
import com.monolith.dsxp.event.DsxpEventIds;
import com.monolith.dsxp.tree.DsxpDeviceTree;
import com.monolith.dsxp.util.DeviceTreeUtils;
import com.monolith.dsxp.warehouse.WarehouseManager;
import com.monolith.dsxp.warehouse.component.WarehouseComponent;
import com.monolith.dsxp.warehouse.event.WarehouseEventIds;
import com.monolith.dsxp.warehouse.utils.WarehouseComponentUtils;
import com.monolith.dsxpdemo.adapter.WarehouseComponentListAdapter;
import com.monolith.dsxpdemo.dsxp.DeviceManager;
import com.monolith.dsxpdemo.util.AlertUtils;

import java.util.List;

/**
 * Date: 2025/10/20 17:35
 * Author: Levent
 * 看板页面
 */
public class DashboardActivity extends AppCompatActivity {
    private RecyclerView rvComponents;

    private WarehouseComponentListAdapter componentListAdapter;

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
        eventContext.registerHandler(DsxpEventIds.DAU_CONNECTION_STATE, (dsxpDeviceTreeNode, dsxpEvent) -> {
            Boolean online = (Boolean) dsxpEvent.getData();
            onComponentStateUpdate(online);
        });
        // 注册库存变化事件
        eventContext.registerHandler(WarehouseEventIds.INVENTORY_UPDATE, (dsxpDeviceTreeNode, dsxpEvent) -> {
            System.out.println(dsxpEvent.getData());
        });
    }

    private void showDeviceTree() {
        DsxpDeviceTree deviceTree = DeviceManager.INSTANCE.getDeviceTree();
        try {
            AlertUtils.alert(this, "Device Tree", DeviceTreeUtils.dumpAsPrintable(deviceTree), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWarehouse() {
        WarehouseManager warehouseManager = DeviceManager.INSTANCE.getWarehouseManager();
        String warehouseInfo = WarehouseComponentUtils.dumpAsPrintable(warehouseManager.getWarehouse());
        AlertUtils.alert(this, "Warehouse Info", warehouseInfo, null);
    }

    private void buildDriver() {
        DeviceManager deviceManager = DeviceManager.INSTANCE;
        deviceManager.buildDrivers();
        showWarehouseComponent();
    }

    private void showWarehouseComponent() {
        WarehouseManager warehouseManager = DeviceManager.INSTANCE.getWarehouseManager();
        List<WarehouseComponent> components = WarehouseComponentUtils.getAllChildren(warehouseManager.getWarehouse());
        componentListAdapter = new WarehouseComponentListAdapter(components);
        rvComponents.setAdapter(componentListAdapter);
        componentListAdapter.notifyDataSetChanged();
    }

    private void startDriver() {
        DeviceManager deviceManager = DeviceManager.INSTANCE;
        deviceManager.start();
    }

    private void stopDriver() {
        DeviceManager deviceManager = DeviceManager.INSTANCE;
        deviceManager.stop();
    }

    private void onComponentStateUpdate(Boolean online) {
        System.out.println(online);
    }
}