package com.monolith.dsxpdemo.dto;

import com.monolith.dsxp.util.ListUtils;
import com.monolith.dsxp.warehouse.component.ShelfBin;

import java.util.List;

public class WorksheetEditInfo {
    private String type;
    private String dir;
    private String assistMode;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getAssistMode() {
        return assistMode;
    }

    public void setAssistMode(String assistMode) {
        this.assistMode = assistMode;
    }
}
