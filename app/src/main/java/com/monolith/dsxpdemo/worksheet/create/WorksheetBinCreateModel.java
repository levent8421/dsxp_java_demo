package com.monolith.dsxpdemo.worksheet.create;

/**
 * Create By YANYiZHI
 * Create Time: 2025/10/28 10:55
 * Description:
 * WorksheetBinModel
 *
 * @author YANYiZHI
 */
public class WorksheetBinCreateModel {
    private String binCode;
    private String skuNo;
    private String skuName;
    private String qtyPlanned;
    private String qtyCompleted;
    private String flowDir;

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public String getSkuNo() {
        return skuNo;
    }

    public void setSkuNo(String skuNo) {
        this.skuNo = skuNo;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getQtyPlanned() {
        return qtyPlanned;
    }

    public void setQtyPlanned(String qtyPlanned) {
        this.qtyPlanned = qtyPlanned;
    }

    public String getQtyCompleted() {
        return qtyCompleted;
    }

    public void setQtyCompleted(String qtyCompleted) {
        this.qtyCompleted = qtyCompleted;
    }

    public String getFlowDir() {
        return flowDir;
    }

    public void setFlowDir(String flowDir) {
        this.flowDir = flowDir;
    }
}
