package com.monolith.dsxpdemo.dto;

import com.monolith.dsxp.warehouse.component.WarehouseComponent;

public class WarehouseComponentListItem {
    private final WarehouseComponent component;
    private int type;
    private String title;
    private String weight;
    private String inventory;
    private boolean online;

    public WarehouseComponentListItem(WarehouseComponent component) {
        this.component = component;
    }

    public WarehouseComponent getComponent() {
        return component;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
