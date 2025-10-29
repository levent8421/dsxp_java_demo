package com.monolith.dsxpdemo.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.monolith.dsxp.util.StringUtils;

public class ViewCheck {
    public static void paramTextCheck(TextView textView, String value) {
        if (StringUtils.isNotEmpty(value)) {
            textView.setText(value);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    public static void paramTextCheck(TextView textView, int color, String value) {
        if (StringUtils.isNotEmpty(value)) {
            textView.setText(value);
            textView.setTextColor(color);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    public static void paramImageCheck(ImageView image, Drawable drawable) {
        if (drawable != null) {
            image.setImageDrawable(drawable);
        }
    }

    public static void paramGoneVisibilityCheck(View view, boolean value) {
        if (value) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public static void paramInVisibilityCheck(View view, boolean value) {
        if (value) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }
}
