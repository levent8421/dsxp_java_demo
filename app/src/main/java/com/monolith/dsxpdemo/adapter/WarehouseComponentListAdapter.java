package com.monolith.dsxpdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.monolith.dsxp.warehouse.WarehouseManager;
import com.monolith.dsxp.warehouse.component.Shelf;
import com.monolith.dsxp.warehouse.component.ShelfBin;
import com.monolith.dsxp.warehouse.component.ShelfLayer;
import com.monolith.dsxp.warehouse.component.Warehouse;
import com.monolith.dsxp.warehouse.component.WarehouseComponent;
import com.monolith.dsxp.warehouse.utils.ComponentCodes;
import com.monolith.dsxpdemo.R;
import com.monolith.dsxpdemo.adapter.Alert.PropInputAlert;
import com.monolith.dsxpdemo.dsxp.DeviceManager;
import com.monolith.dsxpdemo.dto.WarehouseComponentListItem;

import java.util.List;
import java.util.Map;

public class WarehouseComponentListAdapter extends RecyclerView.Adapter {
    public static final int WAREHOUSE = 0x01;
    public static final int SHELF = 0x02;
    public static final int LAYER = 0x03;
    public static final int BIN = 0x04;
    private final Context context;

    static class TitleViewHolder extends RecyclerView.ViewHolder {
        final TextView title;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(android.R.id.text1);
        }
    }

    static class BinViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout lyBinItem;
        final TextView title;
        final TextView weight;
        final TextView inventory;
        final TextView online;

        public BinViewHolder(@NonNull View itemView) {
            super(itemView);
            this.lyBinItem = itemView.findViewById(R.id.ly_bin_item);
            this.title = itemView.findViewById(R.id.tv_bin_code);
            this.weight = itemView.findViewById(R.id.tv_origin);
            this.inventory = itemView.findViewById(R.id.tv_pcs);
            this.online = itemView.findViewById(R.id.tv_state);
        }
    }

    private final List<WarehouseComponentListItem> components;

    public WarehouseComponentListAdapter(Context context, List<WarehouseComponentListItem> components) {
        this.context = context;
        this.components = components;
    }

    @Override
    public int getItemViewType(int position) {
        WarehouseComponentListItem listItem = components.get(position);
        WarehouseComponent component = listItem.getComponent();
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
        WarehouseComponentListItem listItem = components.get(position);
        WarehouseComponent component = listItem.getComponent();
        String code = component.code().asString();
        if (holder instanceof TitleViewHolder) {
            TitleViewHolder viewHolder = (TitleViewHolder) holder;
            viewHolder.title.setText(code);
        }
        if (holder instanceof BinViewHolder) {
            BinViewHolder binViewHolder = (BinViewHolder) holder;
            binViewHolder.title.setText(code);
            binViewHolder.weight.setText(listItem.getWeight());
            binViewHolder.inventory.setText(listItem.getInventory());
            binViewHolder.online.setText(listItem.isOnline() ? "Online" : "Offline");
            binViewHolder.online.setTextColor(listItem.isOnline() ? ContextCompat.getColor(context, R.color.teal_200) : ContextCompat.getColor(context, R.color.red));
            binViewHolder.lyBinItem.setOnClickListener(v -> {
                PropInputAlert propInputAlert = new PropInputAlert(context, code, new PropInputAlert.OnCloseListener() {
                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onSubmit(String binCode, Map<String, String> map) {
                        updateWarehouseProp(binCode, map);
                    }
                });
                propInputAlert.show();
            });
        }
    }

    private void updateWarehouseProp(String binCode, Map<String, String> map) {
        WarehouseManager warehouseManager = DeviceManager.INSTANCE.getWarehouseManager();
        WarehouseComponent component = warehouseManager.findComponent(ComponentCodes.parseCode(binCode));
        component.setProps(map);
    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    public WarehouseComponentListItem getItem(int i) {
        return components.get(i);
    }
}
