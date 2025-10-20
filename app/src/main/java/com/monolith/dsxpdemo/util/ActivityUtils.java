package com.monolith.dsxpdemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ActivityUtils {
    public static void to(Context context, Class<? extends Activity> activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }
}
