package com.monolith.dsxpdemo.worksheet.create;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monolith.dsxp.util.StringUtils;
import com.monolith.dsxp.warehouse.worksheet.WorksheetConstants;
import com.monolith.dsxpdemo.R;
import com.monolith.dsxpdemo.dto.WorksheetEditInfo;
import com.monolith.dsxpdemo.util.ViewCheck;

import java.util.List;

/**
 * Create By YANYiZHI
 * Create Time: 2025/10/27 11:25
 * Class Name: WorksheetBinListAdapter
 * Description:
 * 工单库位适配器
 */
public class WorksheetBinListAdapter extends RecyclerView.Adapter<ContactViewHolder> {
    private List<WorksheetBinCreateModel> list;
    private final WorksheetEditInfo editInfo;
    private final Context context;

    public WorksheetBinListAdapter(Context context, WorksheetEditInfo editInfo) {
        this.editInfo = editInfo;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_worksheet_bin_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        WorksheetBinCreateModel binCreateModel = list.get(position);
        ViewCheck.paramTextCheck(holder.binCode, binCreateModel.getBinCode());
        if (StringUtils.equals(editInfo.getType(), WorksheetConstants.WORKSHEET_TYPE_PLAN_STR)) {
            //计划工单才有计划数量 实际使用中针对不同的类型应该创建不同的adapter
            ViewCheck.paramTextCheck(holder.quantity, context.getString(R.string.plan_qty, binCreateModel.getQtyPlanned()));
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<WorksheetBinCreateModel> updateList) {
        if (list == null) {
            list = updateList;
        } else {
            list.clear();
            list.addAll(updateList);
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        if (list != null) {
            list.clear();
            notifyDataSetChanged();
        }
    }
}
