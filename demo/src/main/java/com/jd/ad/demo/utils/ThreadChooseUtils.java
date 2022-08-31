package com.jd.ad.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public final class ThreadChooseUtils {

    public static boolean isMainThread(Context context) {
        boolean isMain = true;
        String threadType = get(context);
        if (!TextUtils.isEmpty(threadType)) {
            isMain = !"child".equals(threadType);
        }
        Logger.LogD("初始化和请求广告是否在主线程：" + isMain);
        return isMain;
    }

    public static void save(Context context, String type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("jad_demo",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("threadType", type);
        editor.commit();
    }

    public static String get(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("jad_demo",
                Context.MODE_PRIVATE);
        String type = sharedPreferences.getString("threadType", "");
        return type;
    }

    public void delete(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("jad_demo",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("threadType");
        editor.commit();
    }

    public void clear(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("jad_demo",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


}
