package com.monolith.dsxpdemo.dsxp;

import com.monolith.dsxp.driver.registry.DsxpDriverGroupRegistry;
import com.monolith.dsxp.driver.registry.GlobalDsxpDriverGroupRegistry;
import com.monolith.dsxp.event.DefaultDsxpEventContext;
import com.monolith.dsxp.event.DsxpEventContext;
import com.monolith.dsxp.jtrfid.RfidDriverConstants;
import com.monolith.dsxp.jtrfid.worker.JtReaderDriverGroupBuilder;
import com.monolith.dsxp.tree.DsxpDeviceTree;
import com.monolith.dsxp.tree.ds3p.Ds3pDeviceTree;
import com.monolith.dsxp.util.StringUtils;
import com.monolith.dsxp.warehouse.SimpleWarehouseManager;
import com.monolith.dsxp.warehouse.WarehouseManager;
import com.monolith.dsxp.warehouse.worksheet.WorksheetEngine;
import com.monolith.dsxp.warehouse.worksheet.WorksheetException;
import com.monolith.dsxp.warehouse.worksheet.impl.SimpleWorksheetEngine;
import com.monolith.hardware_serial_port.SerialPortFactoryImpl;
import com.monolith.mit.dsp.worker.DspDriverGroupBuilder;
import com.monolith.mit.dsp.worker.driver.DspDriverGroupWorker;

/**
 * Date: 2025/10/20 15:42
 * Author: Levent
 * 设备管理
 */
public class DeviceManager {
    public static DeviceManager INSTANCE = new DeviceManager();
    private final DsxpDeviceTree deviceTree;
    private final WarehouseManager warehouseManager;

    private DeviceManager() {
        // 驱动 -> 连接 -> 采集点 -> 设备
        deviceTree = buildDeviceTree();
        warehouseManager = new SimpleWarehouseManager();
    }

    private DsxpDeviceTree buildDeviceTree() {
        DsxpEventContext eventContext = new DefaultDsxpEventContext(2);
        return new Ds3pDeviceTree(eventContext, new SimpleDsxpStorage());
    }

    public void parseDsxpResource(String resource) {
        String[] lines = resource.split("\n");
        for (String url : lines) {
            if (StringUtils.isBlank(url)) {
                continue;
            }
            try {
                deviceTree.define(StringUtils.trim(url));
            } catch (Exception e) {
                // 传入的URL不合法
                e.printStackTrace();
            }
        }
    }

    public DsxpDeviceTree getDeviceTree() {
        return deviceTree;
    }

    public WarehouseManager getWarehouseManager() {
        return warehouseManager;
    }

    public void buildDrivers() {
        // 构建一个驱动注册中心（用于存储已安装的驱动）
        DsxpDriverGroupRegistry driverGroupRegistry = buildDriverGroupRegistry();
        // 从设备树初始化仓库模块
        try {
            warehouseManager.setupDeviceTree(deviceTree, driverGroupRegistry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DsxpDriverGroupRegistry buildDriverGroupRegistry() {
        //根据不同平台实现具体串口驱动
        SerialPortFactoryImpl serialPortFactory = new SerialPortFactoryImpl();
        DspDriverGroupBuilder dspDriverGroup = new DspDriverGroupBuilder(serialPortFactory);
        JtReaderDriverGroupBuilder jtReaderDriverGroup = new JtReaderDriverGroupBuilder(serialPortFactory);
        DsxpDriverGroupRegistry driverGroupRegistry = GlobalDsxpDriverGroupRegistry.INSTANCE;
        // 设置默认驱动（未指定驱动组时适用的驱动）
        driverGroupRegistry.setDefaultDriverGroupBuilder(dspDriverGroup);
        try {
            // 分别安装需要的驱动
            driverGroupRegistry.installDriver(DspDriverGroupWorker.NAME, dspDriverGroup);
            driverGroupRegistry.installDriver(RfidDriverConstants.DriverName.JT_HF_READER, jtReaderDriverGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return driverGroupRegistry;
    }

    public void start() {
        deviceTree.start();
    }

    public void stop() {
        deviceTree.stop();
    }

    public WorksheetEngine buildWorksheetEngine() throws WorksheetException {
        WorksheetEngine engine = new SimpleWorksheetEngine();
        engine.setup(warehouseManager);
        return engine;
    }
}
