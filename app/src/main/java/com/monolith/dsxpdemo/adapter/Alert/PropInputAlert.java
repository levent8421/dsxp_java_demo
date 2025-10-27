package com.monolith.dsxpdemo.adapter.Alert;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.monolith.dsxpdemo.R;
import com.monolith.dsxpdemo.util.EditTextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Create By YANYiZHI
 * Create Time: 2025/10/27 11:25
 * Class Name: PropInputAlert
 * Description:
 * 库位属性配置
 */
public class PropInputAlert extends AlertDialog {
    private final String binCode;
    private final OnCloseListener listener;
    private EditText etSkuName;
    private EditText etSkuNo;
    private EditText etSkuApw;
    private TextView btOk;
    private TextView btCancel;

    public PropInputAlert(@NonNull Context context, String binCode, OnCloseListener listener) {
        super(context);
        this.binCode = binCode;
        this.listener = listener;
    }

    @Override
    public void show() {
        View rootView = View.inflate(this.getContext(), R.layout.alert_prop_set, null);
        setView(rootView);
        TextView txBinCode = rootView.findViewById(R.id.tx_bin_code);
        txBinCode.setText(binCode);
        etSkuName = rootView.findViewById(R.id.et_sku_name);
        etSkuNo = rootView.findViewById(R.id.et_sku_no);
        etSkuApw = rootView.findViewById(R.id.et_sku_apw);
        btOk = rootView.findViewById(R.id.bt_ok);
        btCancel = rootView.findViewById(R.id.bt_cancel);
        initButton();
        super.show();
        setCanceledOnTouchOutside(false);
    }

    private void initButton() {
        btOk.setOnClickListener(v -> {
            String skuName = EditTextUtils.getString(etSkuName, "测试物料名");
            String skuNo = EditTextUtils.getString(etSkuNo, "test001");
            String skuApw = EditTextUtils.getString(etSkuApw, "0.1");
            Map<String, String> map = new HashMap<>();
            // 以下key参考dsxp设备资源管理规范配置参数表自行新增删除
            map.put("sku_name", skuName);
            map.put("sku_no", skuNo);
            map.put("sku_apw", skuApw);
            listener.onSubmit(binCode, map);
            this.dismiss();
        });
        btCancel.setOnClickListener(v -> {
            listener.onDismiss();
            this.dismiss();
        });
    }

    public interface OnCloseListener {
        void onDismiss();

        void onSubmit(String binCode, Map<String, String> map);
    }
}
