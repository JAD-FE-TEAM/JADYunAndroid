package com.jd.ad.demo.tool;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.bl.initsdk.JADYunSdk;

public class TInfrastructureActivity extends BaseActivity {
    private LinearLayout mInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools_infrastructure_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initView();
    }

    private void initView() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.tools_infrastructure_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mInfoList = findViewById(R.id.item_container);
        LayoutInflater inflater = getLayoutInflater();

        String sdkVersion = "SDK版本号：" + JADYunSdk.getSDKVersion();
        bindText(inflater, sdkVersion);
        String app = "测试应用：" + getApplication().getPackageName();
        bindText(inflater, app);
        String appId = "AppID：" + JADYunSdk.getAppId();
        bindText(inflater, appId);
        String osVersion = "操作系统版本：" + Build.VERSION.RELEASE;
        bindText(inflater, osVersion);
        String manufacturer = "设备制造商：" + Build.MANUFACTURER;
        bindText(inflater, manufacturer);
//        String imei = "imei：" + JADInfoManager.getInstance().getImei();
//        bindText(inflater, imei);
//        String oaid = "oaid：" + JADInfoManager.getInstance().getOaid();
//        bindText(inflater, oaid);
//        String sdkInit = "SDK 初始化：";
//        bindText(inflater, sdkInit);
//        String obfuscation = "代码混淆：";
//        bindText(inflater, obfuscation);
    }

    private void bindText(LayoutInflater inflater, String text) {
        TextView titleTv = (TextView) inflater.inflate(R.layout.demo_ad_type_list_head_item, null);
        titleTv.setText(text);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.leftMargin = ScreenUtils.dip2px(this, 20);
        mInfoList.addView(titleTv, params);
    }
}
