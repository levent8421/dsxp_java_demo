package com.monolith.dsxpdemo.dto;

import com.monolith.dsxp.util.ListUtils;
import com.monolith.dsxp.warehouse.component.ShelfBin;

import java.util.List;

public class WorksheetEditInfo {
    private int type;
    private int dir;
    private int assistMode;
    private final List<ShelfBin> bins = ListUtils.newArrayList();

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public int getAssistMode() {
        return assistMode;
    }

    public void setAssistMode(int assistMode) {
        this.assistMode = assistMode;
    }

    public List<ShelfBin> getBins() {
        return bins;
    }
}
