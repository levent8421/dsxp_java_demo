package com.monolith.dsxpdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.monolith.dsxp.warehouse.component.Shelf;
import com.monolith.dsxp.warehouse.component.ShelfBin;
import com.monolith.dsxp.warehouse.component.ShelfLayer;
import com.monolith.dsxp.warehouse.component.Warehouse;
import com.monolith.dsxp.warehouse.component.WarehouseComponent;
import com.monolith.dsxpdemo.R;

import java.util.List;

public class WarehouseComponentListAdapter extends RecyclerView.Adapter {
    public static final int WAREHOUSE = 0x01;
    public static final int SHELF = 0x02;
    public static final int LAYER = 0x03;
    public static final int BIN = 0x04;

    static class TitleViewHolder extends RecyclerView.ViewHolder {
        final TextView title;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(android.R.id.text1);
        }
    }

    static class BinViewHolder extends RecyclerView.ViewHolder {
        final TextView title;

        public BinViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.tv_bin_code);
        }
    }

    private final List<WarehouseComponent> components;

    public WarehouseComponentListAdapter(List<WarehouseComponent> components) {
        this.components = components;
    }

    @Override
    public int getItemViewType(int position) {
        WarehouseComponent component = components.get(position);
        if (component instanceof Warehouse) {
            return WAREHOUSE;
        }
        if (component instanceof Shelf) {
            return SHELF;
        }
        if (component instanceof ShelfLayer) {
            return LAYER;
        }
        if (component instanceof ShelfBin) {
            return BIN;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        switch (viewType) {
            case WAREHOUSE:
            case SHELF:
            case LAYER:
                itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                return new TitleViewHolder(itemView);
            case BIN:
                itemView = inflater.inflate(R.layout.bin_item, parent, false);
                return new BinViewHolder(itemView);
            default:
                itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                return new TitleViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WarehouseComponent component = components.get(position);
        String code = component.code().asString();
        if (holder instanceof TitleViewHolder) {
            TitleViewHolder viewHolder = (TitleViewHolder) holder;
            viewHolder.title.setText(code);
        }
        if (holder instanceof BinViewHolder) {
            BinViewHolder binViewHolder = (BinViewHolder) holder;
            binViewHolder.title.setText(code);
        }
    }

    @Override
    public int getItemCount() {
        return components.size();
    }
}
