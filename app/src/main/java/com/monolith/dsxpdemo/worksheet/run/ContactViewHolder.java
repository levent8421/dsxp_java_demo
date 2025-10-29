package com.monolith.dsxpdemo.worksheet.run;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monolith.dsxpdemo.R;

/**
 * Create By YANYiZHI
 * Create Time: 2025/10/28 15:48
 * Description:
 * ContactViewHolder
 *
 * @author YANYiZHI
 */
public class ContactViewHolder extends RecyclerView.ViewHolder {
    public TextView txBinCode;
    public TextView txSkuName;
    public TextView txSkuNo;
    public TextView txPlanQty;
    public TextView txCompleteQty;
    public TextView txDeltaQty;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        txBinCode = itemView.findViewById(R.id.tx_bin_code);
        txSkuName = itemView.findViewById(R.id.tx_sku_name);
        txSkuNo = itemView.findViewById(R.id.tx_sku_no);
        txPlanQty = itemView.findViewById(R.id.tx_plan_qty);
        txCompleteQty = itemView.findViewById(R.id.tx_complete_qty);
        txDeltaQty = itemView.findViewById(R.id.tx_delta_qty);
    }
}
