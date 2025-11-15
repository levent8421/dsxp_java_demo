package com.monolith.dsxpdemo.constant;

import com.monolith.dsxp.warehouse.worksheet.WorksheetConstants;

/**
 * Create By YANYiZHI
 * Create Time: 2025/11/11 10:36
 * Description:
 * 驱动不再关心的常量移到这边了
 *
 * @author YANYiZHI
 */
public class WorksheetExpandConstants extends WorksheetConstants {
    public static final int WORKSHEET_TYPE_PLAN = 0x01;
    public static final int WORKSHEET_TYPE_TMP = 0x02;

    public static final String WORKSHEET_TYPE_PLAN_STR = "plan";
    public static final String WORKSHEET_TYPE_TMP_STR = "tmp";

    public static String getWorksheetTypeName(int type) {
        switch (type) {
            case WORKSHEET_TYPE_PLAN:
                return WORKSHEET_TYPE_PLAN_STR;
            case WORKSHEET_TYPE_TMP:
                return WORKSHEET_TYPE_TMP_STR;
            default:
                return "Unknown(" + type + ")";
        }
    }

    public static int getWorksheetTypeCode(String name) {
        switch (name) {
            case WORKSHEET_TYPE_PLAN_STR:
                return WORKSHEET_TYPE_PLAN;
            case WORKSHEET_TYPE_TMP_STR:
                return WORKSHEET_TYPE_TMP;
            default:
                return 0x00;
        }
    }
}
