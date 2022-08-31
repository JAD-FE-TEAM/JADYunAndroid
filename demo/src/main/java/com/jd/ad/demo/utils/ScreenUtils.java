package com.jd.ad.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.math.BigDecimal;

public class ScreenUtils {
    public static final double SPLASH_MIN = 0.49d;
    public static final double SPLASH_MIDDLE = 0.61d;
    public static final double SPLASH_MAX = 0.75d;

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidthDip(Context context) {
        return px2dip(context, ScreenUtils.getScreenWidth(context));
    }

    public static int getScreenHeightDip(Context context) {
        return px2dip(context, ScreenUtils.getScreenHeight(context));

    }

    public static int getRealScreenHeight(Context context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealMetrics(outMetrics);
        } else {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }

        return px2dip(context, outMetrics.heightPixels);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int dip2px(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static double getAspectRatio(@NonNull View view) {
        int height = view.getMeasuredHeight();
        int width = view.getMeasuredWidth();
        return div(width, height);
    }

    public static double getScreenAspectRatio(Context context) {
        int height = getScreenHeight(context);
        int width = getScreenWidth(context);
        return div(width, height);
    }

    public static double div(int width, int height) {
        if (width == 0 || height == 0) {
            return 0d;
        }
        BigDecimal b1 = new BigDecimal(String.valueOf(height));
        BigDecimal b2 = new BigDecimal(String.valueOf(width));
        return b2.divide(b1, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double div(float width, float height) {
        if (width == 0 || height == 0) {
            return 0d;
        }
        BigDecimal b1 = new BigDecimal(String.valueOf(height));
        BigDecimal b2 = new BigDecimal(String.valueOf(width));
        return b2.divide(b1, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static void screenAdapt(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // 旧版本适配
                View v = activity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 新版本适配
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
                decorView.setSystemUiVisibility(uiOptions);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}