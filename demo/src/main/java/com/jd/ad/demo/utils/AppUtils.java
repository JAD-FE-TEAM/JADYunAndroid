package com.jd.ad.demo.utils;

import android.app.ActivityManager;
import android.content.Context;

public class AppUtils {
    /**
     * 获取当前进程名
     *
     * @param context 上下文
     * @return
     */
    public static String getCurProcessName(Context context) {
        // 获取此进程的标识符
        try {
            int pid = android.os.Process.myPid();
            // 获取活动管理器
            ActivityManager activityManager = (ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE);
            // 从应用程序进程列表找到当前进程，是：返回当前进程名
            for (ActivityManager.RunningAppProcessInfo appProcess :
                    activityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
