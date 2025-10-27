package com.monolith.dsxpdemo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.monolith.dsxp.util.ExceptionUtils;
import com.monolith.dsxp.util.ListUtils;
import com.monolith.dsxp.warehouse.WarehouseManager;
import com.monolith.dsxp.warehouse.component.ShelfBin;
import com.monolith.dsxp.warehouse.component.WarehouseComponent;
import com.monolith.dsxp.warehouse.utils.WarehouseComponentUtils;
import com.monolith.dsxp.warehouse.worksheet.WorksheetEngine;
import com.monolith.dsxpdemo.dsxp.DeviceManager;
import com.monolith.dsxpdemo.dto.WorksheetEditInfo;
import com.monolith.dsxpdemo.util.AlertUtils;
import com.monolith.dsxpdemo.util.ToastUtils;

import java.util.List;

/**
 * Date: 2025/10/27 15:13
 * Author: Levent
 * 工单演示界面
 */
public class WorksheetActivity extends AppCompatActivity {
    private final WorksheetEditInfo editInfo = new WorksheetEditInfo();
    private final List<ShelfBin> bins = ListUtils.newArrayList();
    private WorksheetEngine worksheetEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_worksheet);
        buildWorksheetEngine();
        initView();
    }

    private void buildWorksheetEngine() {
        // 实际使用时需要将WorksheetEngine保存在全局，尽量避免多次创建，该对象是一个比较重的对象，构建需要花费较多资源
        // 在构建WorksheetEngine时需要确保WarehouseManager已经就绪
        try {
            worksheetEngine = DeviceManager.INSTANCE.buildWorksheetEngine();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(this, ExceptionUtils.getMessage(e));
        }
    }

    private void initView() {
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
        for (ShelfBin bin : bins) {
            binCodes.add(bin.code().asString());
        }
        AlertUtils.showItemChoose(this, "选择库位", binCodes, (dialogInterface, i) -> {
            ShelfBin shelfBin = bins.get(i);
            editInfo.getBins().add(shelfBin);
        });
    }

    private void loadAllBins() {
        bins.clear();
        WarehouseManager warehouseManager = DeviceManager.INSTANCE.getWarehouseManager();
        List<WarehouseComponent> components = WarehouseComponentUtils.getAllChildren(warehouseManager.getWarehouse());
        for (WarehouseComponent component : components) {
            if (component instanceof ShelfBin) {
                bins.add((ShelfBin) component);
            }
        }
    }

    private void chooseDir() {

    }

    private void chooseType() {

    }

    private void chooseAssistMode() {

    }

    private void startWorksheet() {

    }

    private void finishWorksheet() {

    }
}