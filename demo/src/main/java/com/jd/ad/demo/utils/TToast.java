package com.jd.ad.demo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public final class TToast {
    private static Toast sToast;

    public static void show(Context context, String msg) {
        show(context, msg, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String msg, int duration) {
//        Toast toast = getToast(context);
//        if (toast != null) {
//            toast.setDuration(duration);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.setText(String.valueOf(msg));
//            toast.show();
//        } else {
//            Logger.LogD("toast msg: " + msg);
//        }
    }

    @SuppressLint("ShowToast")
    private static Toast getToast(Context context) {
        if (context == null) {
            return sToast;
        }
        sToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        return sToast;
    }

    public static void reset() {
        sToast = null;
    }

}
