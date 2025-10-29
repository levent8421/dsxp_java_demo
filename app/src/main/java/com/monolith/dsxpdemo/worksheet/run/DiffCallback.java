package com.monolith.dsxpdemo.worksheet.run;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class DiffCallback extends DiffUtil.ItemCallback<WorksheetRunningModel>{
    @Override
    public boolean areItemsTheSame(@NonNull WorksheetRunningModel oldModel, @NonNull WorksheetRunningModel newModel) {
        return Objects.equals(oldModel.getBinCode(),newModel.getBinCode());
    }

    @Override
    public boolean areContentsTheSame(@NonNull WorksheetRunningModel oldModel, @NonNull WorksheetRunningModel newModel) {
        return oldModel.equals(newModel);
    }
}
