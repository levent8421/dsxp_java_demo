package com.monolith.dsxpdemo.util;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.monolith.dsxpdemo.R;

import java.util.List;

public class AlertUtils {
    public static AlertDialog alert(Context ctx, String title, String msg, DialogInterface.OnClickListener listener) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.scroll_text, null, false);
        TextView text = view.findViewById(R.id.text);
        text.setText(msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title)
                .setView(view)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog showItemChoose(Context ctx, String title, List<String> item, DialogInterface.OnClickListener listener) {
        String[] itemArr = item.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title)
                .setItems(itemArr, listener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }
}
