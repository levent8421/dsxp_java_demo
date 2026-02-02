package com.monolith.dsxpdemo.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;


import com.monolith.dsxpdemo.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Date: 2025/6/27 11:02
 * Author: Levent
 * 时间日期输入
 */
public class DateTimeInputUtils {
    public interface DatetimePickerCallback {
        void onCancel();

        void onOK(Date date);
    }

    public static void showDatePickerDialog(Context context, DatetimePickerCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.date_picker_dialog, null, false);

        DatePicker datePicker = view.findViewById(R.id.dp_date);
        builder.setView(view);
        AlertDialog dialog = builder.show();
        view.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int dayOfMonth = datePicker.getDayOfMonth();
            Date date = buildDate(year, month, dayOfMonth, 0, 0, 0, 0);
            callback.onOK(date);
            dialog.dismiss();
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            callback.onCancel();
            dialog.dismiss();
        });
    }

    public static void showDatetimePickerDialog(Context context, DatetimePickerCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.datetime_picker_dialog, null, false);

        DatePicker datePicker = view.findViewById(R.id.dp_date);
        TimePicker timePicker = view.findViewById(R.id.tp_time);
        builder.setView(view);
        AlertDialog dialog = builder.show();
        view.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int dayOfMonth = datePicker.getDayOfMonth();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            Date date = buildDate(year, month, dayOfMonth, hour, minute, 0, 0);
            callback.onOK(date);
            dialog.dismiss();
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            callback.onCancel();
            dialog.dismiss();
        });
    }

    private static Date buildDate(int year, int month, int dayOfMonth, int hour, int min, int sec, int ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, ms);
        return calendar.getTime();
    }
}
