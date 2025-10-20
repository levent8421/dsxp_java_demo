package com.monolith.dsxpdemo.util;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.monolith.dsxpdemo.R;

public class AlertUtils {
    public static AlertDialog alert(Context context, String title, String msg, DialogInterface.OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_text, null, false);
        TextView text = view.findViewById(R.id.text);
        text.setText(msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setView(view)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }
}
