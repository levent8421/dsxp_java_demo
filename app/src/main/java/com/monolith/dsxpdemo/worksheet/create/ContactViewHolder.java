package com.monolith.dsxpdemo.worksheet.create;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monolith.dsxpdemo.R;

/**
 * Create By YANYiZHI
 * Create Time: 2025/10/27 11:25
 * Class Name: ContactViewHolder
 * Description:
 */
public class ContactViewHolder extends RecyclerView.ViewHolder {
    public TextView binCode;
    public TextView quantity;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        binCode = itemView.findViewById(R.id.tx_bin_code);
        quantity = itemView.findViewById(R.id.tx_quantity);
    }
}
