package com.jd.ad.demo.expressad.preload;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.feed.JADFeed;
import com.jd.ad.sdk.splash.JADSplash;

/**
 * 预加载广告管理页面
 */
public class EPreloadAdManagerActivity extends BaseActivity {
    private static final String AD_TAG = "预加载";
    /**
     * 广告位配置容器
     */
    private LinearLayout mPreloadAdConfigContainer;
    private EditText mETSplashSlotID, mETFeedSlotID, mETBannerSlotID, mETInterstitialSlotID;
    private EditText mETSplashWidth, mETFeedWidth, mETBannerWidth, mETInterstitialWidth;
    private EditText mETSplashHeight, mETFeedHeight, mETBannerHeight, mETInterstitialHeight;
    private EditText mETSplashNum, mETFeedNum, mETBannerNum, mETInterstitialNum;

    private String mSplashSlotId = "2525";
    private String mFeedSlotId = "8126";

    private HeadBarLayout mHeadBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_preload_manager_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 延伸显示区域到刘海
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        ScreenUtils.screenAdapt(this);
        initView();
    }

    protected void setSplashSlotId(String slotId) {
        mSplashSlotId = slotId;
    }

    private String getSplashSlotId() {
        return mSplashSlotId;
    }

    protected void setFeedSlotId(String slotId) {
        mFeedSlotId = slotId;
    }

    private String getFeedSlotId() {
        return mFeedSlotId;
    }


    private void initView() {
        mHeadBarLayout = findViewById(R.id.head_bar);
        mHeadBarLayout.setTitle(R.string.e_preload_ad_title);
        mHeadBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPreloadAdConfigContainer = findViewById(R.id.layout_preload_setting_container);
        addPreloadSplashSetting();
        addPreloadFeedSetting();
//        addPreloadBannerSetting();
//        addPreloadInterstitialSetting();

        //预加载广告按钮
        Button mPreloadBtn = findViewById(R.id.preload_ad_btn);
        mPreloadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
        mPreloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preloadAd();
            }
        });

    }

    protected void setHeadBarTitle(int title) {
        mHeadBarLayout.setTitle(R.string.e_preload_ad_multi_process_title);
    }

    private void addPreloadSplashSetting() {
        View view = getLayoutInflater().inflate(R.layout.demo_e_preload_splash_manager_setting_layout, null);
        mPreloadAdConfigContainer.addView(view);
        mETSplashSlotID = view.findViewById(R.id.et_code);
        mETSplashWidth = view.findViewById(R.id.et_size_w);
        mETSplashHeight = view.findViewById(R.id.et_size_h);
        mETSplashNum = view.findViewById(R.id.et_num);

    }

    protected void setETSplashHint(String slotID) {
        if (mETSplashSlotID != null) {
            mETSplashSlotID.setHint(slotID);
        }
    }

    protected void setETFeedHint(String slotID) {
        if (mETSplashSlotID != null) {
            mETFeedSlotID.setHint(slotID);
        }
    }

    private void addPreloadFeedSetting() {
        View view = getLayoutInflater().inflate(R.layout.demo_e_preload_feed_manager_setting_layout, null);
        mPreloadAdConfigContainer.addView(view);
        mETFeedSlotID = view.findViewById(R.id.et_code);
        mETFeedWidth = view.findViewById(R.id.et_size_w);
        mETFeedHeight = view.findViewById(R.id.et_size_h);
        mETFeedNum = view.findViewById(R.id.et_num);
    }

    private void addPreloadBannerSetting() {
        View view = getLayoutInflater().inflate(R.layout.demo_e_preload_banner_manager_setting_layout, null);
        mPreloadAdConfigContainer.addView(view);
        mETBannerSlotID = view.findViewById(R.id.et_code);
        mETBannerWidth = view.findViewById(R.id.et_size_w);
        mETBannerHeight = view.findViewById(R.id.et_size_h);
        mETBannerNum = view.findViewById(R.id.et_num);
    }

    private void addPreloadInterstitialSetting() {
        View view = getLayoutInflater().inflate(R.layout.demo_e_preload_interstitial_manager_setting_layout, null);
        mPreloadAdConfigContainer.addView(view);
        mETInterstitialSlotID = view.findViewById(R.id.et_code);
        mETInterstitialWidth = view.findViewById(R.id.et_size_w);
        mETInterstitialHeight = view.findViewById(R.id.et_size_h);
        mETInterstitialNum = view.findViewById(R.id.et_num);
    }

    private JADSlot generateJADSlot(String slotID, int widthDp, int heightDp) {
        //Step1: 创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink，广告超时时间(可选)
        JADSlot slot = new JADSlot.Builder()
                //广告位ID
                .setSlotID(slotID)
                //期望个性化模板广告view的size,单位dp，注意这里要保证传入尺寸符合申请的模版要求的比例
                .setSize(widthDp, heightDp)
                .build();
        return slot;
    }

    /**
     * 批量预加载广告
     */
    private void preloadAd() {
        String splashSlotID = TextUtils.isEmpty(mETSplashSlotID.getText()) ? getSplashSlotId() :
                mETSplashSlotID.getText().toString();
        int splashWidth = Integer.parseInt(TextUtils.isEmpty(mETSplashWidth.getText()) ? "360" :
                mETSplashWidth.getText().toString());
        int splashHeight = Integer.parseInt(TextUtils.isEmpty(mETSplashHeight.getText()) ? "640" :
                mETSplashHeight.getText().toString());
        int splashNum = TextUtils.isEmpty(mETSplashNum.getText()) ? 1 :
                Integer.parseInt(mETSplashNum.getText().toString());
        for (int i = 0; i < splashNum; i++) {
            JADSlot slot = generateJADSlot(splashSlotID, splashWidth, splashHeight);
            JADSplash splash = new JADSplash(this, slot);
            splash.preloadAd();
        }

        String feedSlotID = TextUtils.isEmpty(mETFeedSlotID.getText()) ? getFeedSlotId() :
                mETFeedSlotID.getText().toString();
        int feedWidth = Integer.parseInt(TextUtils.isEmpty(mETFeedWidth.getText()) ? "320" :
                mETFeedWidth.getText().toString());
        int feedHeight = Integer.parseInt(TextUtils.isEmpty(mETFeedHeight.getText()) ? "180" :
                mETFeedHeight.getText().toString());
        int feedNum = TextUtils.isEmpty(mETFeedNum.getText()) ? 1 :
                Integer.parseInt(mETFeedNum.getText().toString());
        for (int i = 0; i < feedNum; i++) {
            JADSlot slot = generateJADSlot(feedSlotID, feedWidth, feedHeight);
            JADFeed feed = new JADFeed(this, slot);
            feed.preloadAd();
        }


//        String bannerSlotID = TextUtils.isEmpty(mETBannerSlotID.getText()) ? "2532" :
//                mETBannerSlotID.getText().toString();
//        int bannerWidth = Integer.parseInt(TextUtils.isEmpty(mETBannerWidth.getText()) ? "360" :
//                mETBannerWidth.getText().toString());
//        int bannerHeight = Integer.parseInt(TextUtils.isEmpty(mETBannerHeight.getText()) ? "180" :
//                mETBannerHeight.getText().toString());
//        int bannerNum = TextUtils.isEmpty(mETBannerNum.getText()) ? 1 :
//                Integer.parseInt(mETBannerNum.getText().toString());
//        for (int i = 0; i < bannerNum; i++) {
//            JADSlot slot = generateJADSlot(bannerSlotID, bannerWidth, bannerHeight);
//            JADBanner banner = new JADBanner(this, slot);
//            banner.preloadAd();
//        }
//
//
//        String interstitialSlotID = TextUtils.isEmpty(mETInterstitialSlotID.getText()) ? "2534" :
//                mETInterstitialSlotID.getText().toString();
//        int interstitialWidth = Integer.parseInt(TextUtils.isEmpty(mETInterstitialWidth.getText()) ? "400" :
//                mETInterstitialWidth.getText().toString());
//        int interstitialHeight = Integer.parseInt(TextUtils.isEmpty(mETInterstitialHeight.getText()) ? "600" :
//                mETInterstitialHeight.getText().toString());
//        int interstitialNum = TextUtils.isEmpty(mETInterstitialNum.getText()) ? 1 :
//                Integer.parseInt(mETInterstitialNum.getText().toString());
//        for (int i = 0; i < interstitialNum; i++) {
//            JADSlot slot = generateJADSlot(interstitialSlotID, interstitialWidth, interstitialHeight);
//            JADInterstitial interstitial = new JADInterstitial(this, slot);
//            interstitial.preloadAd();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
