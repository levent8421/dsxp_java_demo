package com.monolith.dsxpdemo.worksheet.run;

import com.monolith.dsxp.util.DecimalUtils;

import java.math.BigDecimal;

/**
 * Create By YANYiZHI
 * Create Time: 2025/10/28 16:13
 * Description:
 * WorksheetRunningModel
 *
 * @author YANYiZHI
 */
public class WorksheetRunningModel {
    private String binCode;
    private String skuNo;
    private String skuName;
    private BigDecimal qtyPlanned;
    private BigDecimal qtyCompleted;
    private BigDecimal qtyDelta;
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

    public BigDecimal getQtyPlanned() {
        return qtyPlanned;
    }

    public void setQtyPlanned(BigDecimal qtyPlanned) {
        this.qtyPlanned = qtyPlanned;
    }

    public BigDecimal getQtyCompleted() {
        return qtyCompleted;
    }

    public void setQtyCompleted(BigDecimal qtyCompleted) {
        this.qtyCompleted = qtyCompleted;
    }

    public BigDecimal getQtyDelta() {
        return qtyDelta;
    }

    public void setQtyDelta(BigDecimal qtyDelta) {
        this.qtyDelta = qtyDelta;
    }

    public String getFlowDir() {
        return flowDir;
    }

    public void setFlowDir(String flowDir) {
        this.flowDir = flowDir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorksheetRunningModel that = (WorksheetRunningModel) o;
        return DecimalUtils.valueEquals(qtyPlanned, that.qtyPlanned) &&
                DecimalUtils.valueEquals(qtyCompleted, that.qtyCompleted) &&
                DecimalUtils.valueEquals(qtyDelta, that.qtyDelta);
    }
}
