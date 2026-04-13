package com.monolith.dsxpdemo.util;

import com.monolith.dsxp.warehouse.WarehouseManager;
import com.monolith.dsxp.warehouse.component.WarehouseComponent;
import com.monolith.dsxp.warehouse.utils.ComponentCodes;
import com.monolith.dsxp.warehouse.utils.WarehouseComponentUtils;
import com.monolith.dsxpdemo.dsxp.DeviceManager;
import com.monolith.dsxpdemo.dto.WorksheetItemDTO;
import com.monolith.mit.dsp.worker.dau.wt.SimpleWtDauWorker;

import java.util.Collection;
import java.util.List;

/**
 * Create By YANYiZHI
 * Create Time: 2025/12/24 12:41
 * Description:
 * WorksheetUtils
 *
 * @author YANYiZHI
 */
public class WorksheetControlUtils {

    private static List<WorksheetItemDTO> cacheList;

    public static void startWorksheet(List<WorksheetItemDTO> list) {
        cacheList = list;
        WarehouseManager manager = DeviceManager.INSTANCE.getWarehouseManager();
        for (WorksheetItemDTO itemDTO : list) {
            WarehouseComponent component = manager.findComponent(ComponentCodes.parseCode(itemDTO.getBinCode()));
            Collection<SimpleWtDauWorker> daus = WarehouseComponentUtils.findDauByType(component, SimpleWtDauWorker.class);
            for (SimpleWtDauWorker dau : daus) {
                try {
                    dau.startWTWorksheet(itemDTO.getPlanQty(), itemDTO.getCompleteQty(), false);
                } catch (Exception e) {
                    System.out.println("open worksheet item err");
                }
            }
        }
    }

    public static void stopWorksheet() {
        WarehouseManager manager = DeviceManager.INSTANCE.getWarehouseManager();
        if (cacheList != null) {
            for (WorksheetItemDTO itemDTO : cacheList) {
                WarehouseComponent component = manager.findComponent(ComponentCodes.parseCode(itemDTO.getBinCode()));
                Collection<SimpleWtDauWorker> daus = WarehouseComponentUtils.findDauByType(component, SimpleWtDauWorker.class);
                for (SimpleWtDauWorker dau : daus) {
                    try {
                        dau.stopWTWorksheet();
                    } catch (Exception e) {
                        System.out.println("stop worksheet item err");
                    }
                }
            }
        }
        cacheList = null;
    }
}
