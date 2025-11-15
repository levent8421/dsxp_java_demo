package com.monolith.dsxpdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monolith.dsxp.util.DecimalUtils;
import com.monolith.dsxp.util.ExceptionUtils;
import com.monolith.dsxp.util.ListUtils;
import com.monolith.dsxp.util.StringUtils;
import com.monolith.dsxp.warehouse.WarehouseManager;
import com.monolith.dsxp.warehouse.component.ShelfBin;
import com.monolith.dsxp.warehouse.component.WarehouseComponent;
import com.monolith.dsxp.warehouse.component.conf.WarehouseSku;
import com.monolith.dsxp.warehouse.utils.ComponentCodes;
import com.monolith.dsxp.warehouse.utils.ComponentConfUtils;
import com.monolith.dsxp.warehouse.utils.WarehouseComponentUtils;
import com.monolith.dsxp.warehouse.utils.WorksheetUtils;
import com.monolith.dsxp.warehouse.worksheet.Worksheet;
import com.monolith.dsxp.warehouse.worksheet.WorksheetConstants;
import com.monolith.dsxp.warehouse.worksheet.WorksheetEngine;
import com.monolith.dsxp.warehouse.worksheet.WorksheetItem;
import com.monolith.dsxpdemo.constant.WorksheetExpandConstants;
import com.monolith.dsxpdemo.dsxp.DeviceManager;
import com.monolith.dsxpdemo.dto.WorksheetEditInfo;
import com.monolith.dsxpdemo.util.AlertUtils;
import com.monolith.dsxpdemo.util.ToastUtils;
import com.monolith.dsxpdemo.worksheet.WorksheetLifeCycleHandler;
import com.monolith.dsxpdemo.worksheet.create.WorksheetBinCreateModel;
import com.monolith.dsxpdemo.worksheet.create.WorksheetBinListAdapter;
import com.monolith.dsxpdemo.worksheet.run.WorksheetRunningAdapter;
import com.monolith.dsxpdemo.worksheet.run.WorksheetRunningModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2025/10/27 15:13
 * Author: Levent
 * 工单演示界面
 */
public class WorksheetActivity extends AppCompatActivity implements WorksheetLifeCycleHandler.RunningListener {
    private final WorksheetEditInfo editInfo = new WorksheetEditInfo();
    private final Map<String, WorksheetBinCreateModel> createModelMap = new HashMap<>();
    private final List<ShelfBin> bins = ListUtils.newArrayList();
    private final Map<String, WorksheetRunningModel> runningModelMap = new HashMap<>();
    /**
     * 工单类型 计划/临时
     */
    private final List<String> worksheetTypes = List.of(WorksheetExpandConstants.WORKSHEET_TYPE_PLAN_STR, WorksheetExpandConstants.WORKSHEET_TYPE_TMP_STR);
    /**
     * 流向 取货/补货 (计划类型)
     */
    private final List<String> planFlowDirs = List.of(WorksheetConstants.PICKUP_STR, WorksheetConstants.PUT_AWAY_STR);
    /**
     * 流向 取货/补货/自由(临时类型)
     */
    private final List<String> freeFlowDirs = List.of(WorksheetConstants.PICKUP_STR, WorksheetConstants.PUT_AWAY_STR, WorksheetConstants.FREE_STR);
    /**
     * 协同模式 指示/专注/严格
     */
    private final List<String> assistModes = List.of(WorksheetConstants.ASSIST_MODE_INDICATE_STR, WorksheetConstants.ASSIST_MODE_FOCUS_STR, WorksheetConstants.ASSIST_MODE_STRICT_STR);
    private WorksheetEngine worksheetEngine;
    private WorksheetBinListAdapter binListAdapter;
    private WorksheetRunningAdapter runningAdapter;
    private TextView txType;
    private TextView txFlowDir;
    private TextView txAssist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet);
        buildWorksheetEngine();
        initView();
    }

    private void buildWorksheetEngine() {
        // 实际使用时需要将WorksheetEngine保存在全局，尽量避免多次创建，该对象是一个比较重的对象，构建需要花费较多资源
        // 在构建WorksheetEngine时需要确保WarehouseManager已经就绪
        try {
            worksheetEngine = DeviceManager.INSTANCE.buildWorksheetEngine();
            worksheetEngine.setWorksheetListener(new WorksheetLifeCycleHandler(this, worksheetEngine.getWarehouseManager()));
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(this, ExceptionUtils.getMessage(e));
        }
    }

    private void initView() {
        RecyclerView rvBins = findViewById(R.id.rv_bins);
        RecyclerView rvRunning = findViewById(R.id.rv_running);
        txType = findViewById(R.id.tx_type);
        txFlowDir = findViewById(R.id.tx_flow_dir);
        txAssist = findViewById(R.id.tx_assist);
        rvBins.setLayoutManager(new LinearLayoutManager(this));
        binListAdapter = new WorksheetBinListAdapter(this, editInfo);
        rvBins.setAdapter(binListAdapter);

        rvRunning.setLayoutManager(new LinearLayoutManager(this));
        runningAdapter = new WorksheetRunningAdapter(this);
        rvRunning.setAdapter(runningAdapter);
        findViewById(R.id.btn_bins).setOnClickListener(v -> chooseBins());
        findViewById(R.id.btn_dir).setOnClickListener(v -> chooseDir());
        findViewById(R.id.btn_type).setOnClickListener(v -> chooseType());
        findViewById(R.id.btn_assist).setOnClickListener(v -> chooseAssistMode());
        findViewById(R.id.btn_start).setOnClickListener(v -> startWorksheet());
        findViewById(R.id.btn_finish).setOnClickListener(v -> finishWorksheet());
    }

    private void chooseBins() {
        loadAllBins();
        List<String> binCodes = ListUtils.newArrayList();
        List<ShelfBin> binCaches = ListUtils.newArrayList();
        for (ShelfBin bin : bins) {
            if (!createModelMap.containsKey(bin.code().asString())) {
                binCodes.add(bin.code().asString());
                binCaches.add(bin);
            }
        }
        AlertUtils.showItemChoose(this, "选择库位", binCodes, (dialogInterface, i) -> {
            WorksheetBinCreateModel worksheetBinCreateModel = new WorksheetBinCreateModel();
            ShelfBin shelfBin = binCaches.get(i);
            WarehouseSku skuConf = ComponentConfUtils.getSkuConf(shelfBin);
            worksheetBinCreateModel.setBinCode(shelfBin.code().asString());
            worksheetBinCreateModel.setSkuNo(skuConf.getNo());
            worksheetBinCreateModel.setSkuName(skuConf.getName());
            worksheetBinCreateModel.setQtyPlanned("6");
            worksheetBinCreateModel.setQtyCompleted("0");
            worksheetBinCreateModel.setFlowDir(editInfo.getDir());
            createModelMap.put(binCodes.get(i), worksheetBinCreateModel);
            binListAdapter.updateList(new ArrayList<>(createModelMap.values()));
        });
    }

    private void loadAllBins() {
        bins.clear();
        WarehouseManager warehouseManager = DeviceManager.INSTANCE.getWarehouseManager();
        List<WarehouseComponent> components = WarehouseComponentUtils.getAllChildren(warehouseManager.getWarehouse());
        for (WarehouseComponent component : components) {
            if (component instanceof ShelfBin) {
                //必须是绑定了物料的库位才能够作为工单的执行项
                WarehouseSku skuConf = ComponentConfUtils.getSkuConf(component);
                if (skuConf.getNo() == null || skuConf.getApw() == null) {
                    continue;
                }
                bins.add((ShelfBin) component);
            }
        }
    }

    private void chooseDir() {
        String type = editInfo.getType();
        List<String> flowDir = Collections.emptyList();
        if (StringUtils.equals(type, WorksheetExpandConstants.WORKSHEET_TYPE_PLAN_STR)) {
            flowDir = planFlowDirs;
        }
        if (StringUtils.equals(type, WorksheetExpandConstants.WORKSHEET_TYPE_TMP_STR)) {
            flowDir = freeFlowDirs;
        }
        List<String> finalFlowDir = flowDir;
        AlertUtils.showItemChoose(this, "选择流向", flowDir, (dialogInterface, i) -> {
            editInfo.setDir(finalFlowDir.get(i));
            txFlowDir.setVisibility(View.VISIBLE);
            txFlowDir.setText(getString(R.string.flow_dir, finalFlowDir.get(i)));
        });
    }

    private void chooseType() {
        AlertUtils.showItemChoose(this, "选择工单类型", worksheetTypes, (dialogInterface, i) -> {
            editInfo.setType(worksheetTypes.get(i));
            editInfo.setDir(null);
            txType.setVisibility(View.VISIBLE);
            txType.setText(getString(R.string.worksheet_type, worksheetTypes.get(i)));
            txFlowDir.setVisibility(View.GONE);
            createModelMap.clear();
            binListAdapter.clear();
        });
    }

    private void chooseAssistMode() {
        AlertUtils.showItemChoose(this, "选择协调模式", assistModes, (dialogInterface, i) -> {
            editInfo.setAssistMode(assistModes.get(i));
            txAssist.setVisibility(View.VISIBLE);
            txAssist.setText(getString(R.string.assist_mode, assistModes.get(i)));
        });
    }

    /**
     * 实际业务中要做一下一次只能开启一个工单的判定
     */
    private void startWorksheet() {
        if (editInfo.getType() == null || editInfo.getDir() == null || editInfo.getAssistMode() == null) {
            return;
        }
        if (StringUtils.equals(editInfo.getType(), WorksheetExpandConstants.WORKSHEET_TYPE_PLAN_STR)) {
            startPlanWorksheet();
        }
        if (StringUtils.equals(editInfo.getType(), WorksheetExpandConstants.WORKSHEET_TYPE_TMP_STR)) {
            startTmpWorksheet();
        }
    }

    /**
     * 开启计划工单
     */
    private void startPlanWorksheet() {
        Worksheet worksheet = new Worksheet("worksheetId");
        //这边流向仅作为业务参考 不具备实际逻辑意义
        worksheet.setFlowDir(WorksheetConstants.getFlowDirCode(editInfo.getDir()));
        worksheet.setTitle("计划工单演示");
        worksheet.setAssistMode(WorksheetConstants.getAssistModeCode(editInfo.getAssistMode()));
        for (WorksheetBinCreateModel binModel : createModelMap.values()) {
            WorksheetItem worksheetItem = WorksheetUtils.createWorksheetItem(ComponentCodes.parseCode(binModel.getBinCode()), WorksheetExpandConstants.getWorksheetTypeCode(editInfo.getType()));
            worksheet.getItems().add(worksheetItem);
            worksheetItem.setSkuNo(binModel.getSkuNo());
            //下面两个数值有正负的区别  正：补货  负：取货
            worksheetItem.setPlanQty(DecimalUtils.parse(binModel.getQtyPlanned()));
            worksheetItem.setCompleteQty(DecimalUtils.parse(binModel.getQtyCompleted()));
        }
        try {
            worksheetEngine.startWorksheet(worksheet);
            startRunningUi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启临时工单
     */
    private void startTmpWorksheet() {
        Worksheet worksheet = new Worksheet("worksheetId");
        worksheet.setFlowDir(WorksheetConstants.getFlowDirCode(editInfo.getDir()));
        worksheet.setTitle("临时工单演示");
        worksheet.setAssistMode(WorksheetConstants.getAssistModeCode(editInfo.getAssistMode()));
        for (WorksheetBinCreateModel binModel : createModelMap.values()) {
            WorksheetItem worksheetItem = WorksheetUtils.createWorksheetItem(ComponentCodes.parseCode(binModel.getBinCode()), WorksheetExpandConstants.getWorksheetTypeCode(editInfo.getType()));
            worksheet.getItems().add(worksheetItem);
            worksheetItem.setSkuNo(binModel.getSkuNo());
        }
        try {
            worksheetEngine.startWorksheet(worksheet);
            startRunningUi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finishWorksheet() {
        try {
            worksheetEngine.stopWorksheet("worksheetId");
            createModelMap.clear();
            runningModelMap.clear();
            clearUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearUI() {
        binListAdapter.clear();
        runningAdapter.clear();
    }

    private void startRunningUi() {
        Collection<WorksheetBinCreateModel> values = createModelMap.values();

        for (WorksheetBinCreateModel value : values) {
            WorksheetRunningModel runningModel = new WorksheetRunningModel();
            runningModelMap.put(value.getBinCode(), runningModel);
            runningModel.setBinCode(value.getBinCode());
            runningModel.setSkuName(value.getSkuName());
            runningModel.setQtyPlanned(DecimalUtils.parse(value.getQtyPlanned()));
            runningModel.setQtyCompleted(DecimalUtils.parse(value.getQtyCompleted()));
            runningModel.setQtyDelta(BigDecimal.ZERO);
        }

        runningAdapter.updateList(runningModelMap.values());
    }

    @Override
    public void onUpdate(WorksheetRunningModel runningModel) {
        runningModelMap.put(runningModel.getBinCode(), runningModel);
        runningAdapter.updateList(runningModelMap.values());
    }
}