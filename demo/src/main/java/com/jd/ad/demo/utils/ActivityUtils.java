package com.jd.ad.demo.utils;

import android.app.Activity;
import android.os.Build;

public class ActivityUtils {
    public static boolean isActivityAvailable(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return false;
        }
        boolean flag = false;
        if (Build.VERSION.SDK_INT >= 17) {
            if (!activity.isDestroyed()) {
                flag = true;
            }
        } else {
            if (!activity.isFinishing()) {
                flag = true;
            }
        }
        return flag;
    }
}
