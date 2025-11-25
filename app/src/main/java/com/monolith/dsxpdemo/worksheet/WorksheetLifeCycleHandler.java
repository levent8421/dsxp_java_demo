package com.monolith.dsxpdemo.worksheet;

import androidx.annotation.NonNull;

import com.monolith.dsxp.util.DecimalUtils;
import com.monolith.dsxp.warehouse.WarehouseManager;
import com.monolith.dsxp.warehouse.component.ShelfBin;
import com.monolith.dsxp.warehouse.component.WarehouseComponent;
import com.monolith.dsxp.warehouse.component.conf.WarehouseSku;
import com.monolith.dsxp.warehouse.worksheet.WorksheetItem;
import com.monolith.dsxp.warehouse.worksheet.WorksheetListener;
import com.monolith.dsxp.warehouse.worksheet.task.WorksheetItemPerformTask;
import com.monolith.dsxp.warehouse.worksheet.task.WorksheetItemPerformTaskState;
import com.monolith.dsxp.warehouse.worksheet.task.WorksheetPerformError;
import com.monolith.dsxp.warehouse.worksheet.task.WorksheetPerformTask;
import com.monolith.dsxpdemo.worksheet.run.WorksheetRunningModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create By YANYiZHI
 * Create Time: 2025/10/28 14:23
 * Description:
 * 工单执行生命周期
 *
 * @author YANYiZHI
 */
public class WorksheetLifeCycleHandler implements WorksheetListener {

    private final RunningListener listener;
    private final WarehouseManager warehouseManager;
    private final Map<String, WarehouseSku> warehouseSkuMap = new HashMap<>();

    public WorksheetLifeCycleHandler(RunningListener listener, WarehouseManager warehouseManager) {
        this.listener = listener;
        this.warehouseManager = warehouseManager;
    }

    @Override
    public void onWorksheetStart(WorksheetPerformTask worksheetPerformTask) {
        // TODO 工单开启成功回调业务
        List<WarehouseComponent> allComponents = warehouseManager.getAllComponents();
        for (WarehouseComponent component : allComponents) {
            if (component instanceof ShelfBin) {
                WarehouseSku skuConf = component.getConfContainer().getSku();
                warehouseSkuMap.put(component.code().asString(), skuConf);
            }
        }
    }

    @Override
    public void onWorksheetFinish(WorksheetPerformTask worksheetPerformTask) {
        // TODO 工单关闭成功回调业务
    }

    @Override
    public void onWorksheetItemError(WorksheetItemPerformTask itemPerformTask, WorksheetPerformError error) {
        // TODO 工单项执行错误业务
    }

    @Override
    public void onWorksheetError(WorksheetPerformTask performTask, WorksheetPerformError error) {
        // TODO 工单执行错误业务
    }

    @Override
    public void onWorksheetItemUpdate(WorksheetItemPerformTask itemPerformTask) {
        //变化的工单id
        String worksheetId = itemPerformTask.getWorksheetTask().getWorksheet().getId();
        //变化的工单项
        WorksheetItem taskWorksheetItem = itemPerformTask.getWorksheetItem();
        String binCode = taskWorksheetItem.getComponentCode().asString();
        WorksheetItemPerformTaskState state = itemPerformTask.getState();
        WarehouseSku warehouseSku = warehouseSkuMap.get(binCode);

        WorksheetRunningModel runningModel = getWorksheetRunningModel(binCode, state, warehouseSku);
        listener.onUpdate(runningModel);
        //物料是否匹配异常：WorksheetConstants.getSKUMatchStateName(state.getSkuMatchState())
        //库存数量异常：WorksheetConstants.getInventoryStateName(state.getInventoryState())
        //非法操作异常：WorksheetConstants.getBinMatchName(state.getBinMatchState())
        //硬件异常：itemPerformTask.getErrors().isEmpty()
    }

    @NonNull
    private static WorksheetRunningModel getWorksheetRunningModel(String binCode, WorksheetItemPerformTaskState state, WarehouseSku warehouseSku) {
        WorksheetRunningModel runningModel = new WorksheetRunningModel();
        runningModel.setBinCode(binCode);
        runningModel.setQtyPlanned(DecimalUtils.orDefault(state.getPlanQty(), DecimalUtils.ZERO));
        runningModel.setQtyCompleted(DecimalUtils.orDefault(state.getCompleteQty(), DecimalUtils.ZERO));
        runningModel.setQtyDelta(DecimalUtils.orDefault(state.getInventoryDeltaQty(), DecimalUtils.ZERO));
        if (warehouseSku != null) {
            runningModel.setSkuName(warehouseSku.getName());
            runningModel.setSkuNo(warehouseSku.getNo());
        }
        return runningModel;
    }

    @Override
    public void onWorksheetItemFinish(WorksheetItemPerformTask itemPerformTask) {
        // TODO 工单项领取/放回完毕业务
        //工单项提交原因：int finishReason = itemPerformTask.getState().getFinishReason(); 锁提交、按键提交、门禁类、闸机
    }

    /**
     * 实际开发中应该使用耦合更低的方式更新UI
     */
    public interface RunningListener {
        void onUpdate(WorksheetRunningModel runningModel);
    }
}
