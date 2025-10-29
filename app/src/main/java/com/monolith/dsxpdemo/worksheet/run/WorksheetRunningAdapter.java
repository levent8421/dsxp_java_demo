package com.monolith.dsxpdemo.worksheet.run;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import com.monolith.dsxpdemo.R;
import com.monolith.dsxpdemo.util.ViewCheck;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Create By YANYiZHI
 * Create Time: 2025/10/28 15:48
 * Description:
 * 工单执行适配器
 *
 * @author YANYiZHI
 */
public class WorksheetRunningAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private final Context context;
    private final AsyncListDiffer<WorksheetRunningModel> differ = new AsyncListDiffer<>(this, new DiffCallback());
    private final Handler handler = new Handler();

    public WorksheetRunningAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_worksheet_running_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        WorksheetRunningModel runningModel = differ.getCurrentList().get(position);
        ViewCheck.paramTextCheck(holder.txBinCode, runningModel.getBinCode());
        ViewCheck.paramTextCheck(holder.txSkuName, runningModel.getSkuName());
        ViewCheck.paramTextCheck(holder.txSkuNo, runningModel.getSkuNo());
        ViewCheck.paramTextCheck(holder.txPlanQty, context.getString(R.string.plan_qty, String.valueOf(runningModel.getQtyPlanned())));
        ViewCheck.paramTextCheck(holder.txCompleteQty, context.getString(R.string.complete_qty, String.valueOf(runningModel.getQtyCompleted())));
        ViewCheck.paramTextCheck(holder.txDeltaQty, context.getString(R.string.delta_qty, String.valueOf(runningModel.getQtyDelta())));
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public void updateList(Collection<WorksheetRunningModel> values) {
        handler.post(() -> differ.submitList(new ArrayList<>(values)));
    }

    public void clear() {
        handler.post(() -> differ.submitList(new ArrayList<>()));
    }
}
