package com.monolith.dsxpdemo.dto;

import java.math.BigDecimal;

/**
 * Create By YANYiZHI
 * Create Time: 2025/12/24 12:42
 * Description:
 * WorksheetItemDTO
 *
 * @author YANYiZHI
 */
public class WorksheetItemDTO {
    private String binCode;
    private BigDecimal planQty;
    private BigDecimal completeQty;

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public BigDecimal getPlanQty() {
        return planQty;
    }

    public void setPlanQty(BigDecimal planQty) {
        this.planQty = planQty;
    }

    public BigDecimal getCompleteQty() {
        return completeQty;
    }

    public void setCompleteQty(BigDecimal completeQty) {
        this.completeQty = completeQty;
    }
}
