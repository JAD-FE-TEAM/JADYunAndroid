package com.jd.ad.demo.utils;

import android.util.Log;

public class Logger {
    public static final String TAG = "DemoLog";
    public static boolean ENABLE_LOG = true;

    public static void LogD(String msg) {
        if (ENABLE_LOG) {
            Log.d(TAG, msg);
        }
    }

    public static void LogE(String msg) {
        if (ENABLE_LOG) {
            Log.e(TAG, msg);
        }
    }
}
