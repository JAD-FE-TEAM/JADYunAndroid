package com.kepler.jd.sdk;

import android.app.Activity;
import android.os.Bundle;

/**
 * 小把手配置页面，需要媒体在应用清单文件中注册并配置应用的的scheme协议
 */
public class KeplerBackActivity extends Activity {
    public KeplerBackActivity() {
    }

    public void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.finish();
    }
}
