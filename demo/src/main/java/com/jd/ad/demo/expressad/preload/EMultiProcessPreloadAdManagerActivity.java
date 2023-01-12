package com.jd.ad.demo.expressad.preload;

import android.os.Bundle;
import android.widget.Button;

import com.jd.ad.demo.R;

/**
 * 预加载广告管理页面
 */
public class EMultiProcessPreloadAdManagerActivity extends EPreloadAdManagerActivity {
    private static final String AD_TAG = "预加载";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeadBarTitle(R.string.e_preload_ad_multi_process_title);
        setETSplashHint("10844");
        setETFeedHint("10847");

        setSplashSlotId("10844");
        setFeedSlotId("10847");
        //预加载广告按钮
        Button mPreloadBtn = findViewById(R.id.preload_ad_btn);
        mPreloadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));

    }


}
