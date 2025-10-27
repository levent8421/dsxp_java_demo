package com.monolith.dsxpdemo.util;

import android.text.Editable;
import android.widget.EditText;

import com.monolith.dsxp.util.StringUtils;


/**
 * Date: 2025/6/26 16:47
 * Author: Levent
 * EDIT Text工具
 */
public class EditTextUtils {
    public static String getString(EditText text, String defVal) {
        if (text == null) {
            return defVal;
        }
        Editable cs = text.getText();
        if (cs == null) {
            return defVal;
        }
        String str = cs.toString().trim();
        if (StringUtils.isBlank(str)) {
            return defVal;
        }
        return str;
    }

    public static int getNumber(EditText text, int defVal) {
        String str = getString(text, null);
        if (str == null) {
            return defVal;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defVal;
        }
    }

    public static double getDouble(EditText text, double defVal) {
        String str = getString(text, null);
        if (str == null) {
            return defVal;
        }
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return defVal;
        }
    }
}
