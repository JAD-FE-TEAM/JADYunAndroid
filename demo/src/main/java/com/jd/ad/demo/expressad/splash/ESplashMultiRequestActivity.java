package com.jd.ad.demo.expressad.splash;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.Logger;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.splash.JADSplash;
import com.jd.ad.sdk.splash.JADSplashListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板开屏
 */
public class ESplashMultiRequestActivity extends BaseActivity {
    private static final String AD_TAG = "Splash";

    /**
     * 开屏广告容器
     */
    private ViewGroup mAdContainer;
    /**
     * 广告 ID 输入控件
     */
    private EditText mPlacementEt;
    /**
     * 请求数量 输入控件
     */
    private EditText mNumEt;

    private Button mAdLoadBtn;
    private Button mAdShowBtn;
    private JADSplash mCurrentSplash;

    /**
     * 广告列表
     */
    private List<JADSplash> mAdList = new ArrayList<>();
    /**
     * 广告的视图集合
     */
    private HashMap<JADSplash, View> mAdMaps = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_splash_multi_request_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 延伸显示区域到刘海
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        ScreenUtils.screenAdapt(this);
        initView();
    }

    private void initView() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_feed_multi_request_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAdContainer = findViewById(R.id.ad_container);
        mAdLoadBtn = findViewById(R.id.load_ad_btn);
        mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
        mAdShowBtn = findViewById(R.id.show_ad_btn);
        mPlacementEt = (EditText) findViewById(R.id.et_code);
        mNumEt = (EditText) findViewById(R.id.et_num);
        final String slotID = TextUtils.isEmpty(mPlacementEt.getText()) ? "2525" :
                mPlacementEt.getText().toString();


        mAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ThreadChooseUtils.isMainThread(ESplashMultiRequestActivity.this)) {
                    multiLoad(slotID);
                } else {
                    DemoExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            multiLoad(slotID);
                        }
                    });
                }

            }
        });
        mAdShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextAd();
            }
        });
    }

    private void collectAds(JADSplash splash, View adView) {
        Logger.LogD("【splash】 collectAds：【" + splash.hashCode() + ", " + adView.hashCode() + "】");
        mAdMaps.put(splash, adView);
    }

    private void showNextAd() {
        mAdContainer.setVisibility(View.VISIBLE);
        Iterator<Map.Entry<JADSplash, View>> entries = mAdMaps.entrySet().iterator();
        if (entries.hasNext()) {
            Map.Entry<JADSplash, View> entry = entries.next();
            mCurrentSplash = entry.getKey();
            View adView = entry.getValue();
            Logger.LogD("【splash】 show current ad ：【" + (mCurrentSplash != null ?
                    mCurrentSplash.hashCode() : "-1") + ", " + (adView != null ? adView.hashCode() :
                    "-1") + "】");
            if (adView != null) {
                mAdContainer.addView(adView);
            }
            entries.remove();
        } else {
            removeSplashAd();
        }
    }


    private void multiLoad(String slotID) {
        int reqNum = 10;
        try {
            if (mNumEt != null && mNumEt.getText() != null && !TextUtils.isEmpty(mNumEt.getText().toString())) {
                String requestNum = mNumEt.getText().toString();
                reqNum = Integer.parseInt(requestNum);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        mAdMaps.clear();
        mAdList.clear();
        for (int i = 0; i < reqNum; i++) {
            loadAdAndShow(slotID);
        }
    }

    /**
     * 加载广告
     */
    private void loadAdAndShow(String slotID) {
        //准备广告位 宽高  单位 dp
        int widthDp = ScreenUtils.getScreenWidthDip(this);
        //100dp demo 布局文件中 底部 logo 图片高度,开发者根据自身实际情况设置
        int heightDp = ScreenUtils.getRealScreenHeight(this) - 100;
        // 加载广告的容忍时间
        float tolerateTime = 3.5f;
        //跳过按钮的起始时间
        int skitTime = 5;

        //Step1: 创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink，广告超时时间(可选)
        JADSlot slot = new JADSlot.Builder()
                //广告位ID
                .setSlotID(slotID)
                //期望个性化模板广告view的size,单位dp，注意这里要保证传入尺寸符合申请的模版要求的比例
                .setSize(widthDp, heightDp)
                //广告加载容忍时间。如果设定的时间内任然没有加载到广告，则判断加载失败
                .setTolerateTime(tolerateTime)
                //倒计时时间 单位：秒
                .setSkipTime(skitTime)
                //设置模版开屏广告点击类型,
                .setSplashClickAreaType(JADSlot.ClickStyle.SERVER)
                .build();//3，

        //Step2: 创建JadSplash，参数包括广告位参数和回调接口
        JADSplash splash = new JADSplash(this, slot);
        Logger.LogD("【splash】 init hashCode: " + splash.hashCode());
        //Step3: 加载广告
        splash.loadAd(new JADSplashListener() {
            @Override
            public void onLoadSuccess() {
                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
                // 获取竞价价格
                if (splash.getJADExtra() != null) {
                    int price = splash.getJADExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }
            }

            @Override
            public void onLoadFailure(int code, String error) {
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_failed, AD_TAG));
            }

            @Override
            public void onRenderSuccess(View adView) {
                showToast(getString(R.string.ad_render_success, AD_TAG));
                logD(getString(R.string.ad_render_success, AD_TAG));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
                collectAds(splash, adView);
            }

            @Override
            public void onRenderFailure(int code, String error) {
                showToast(getString(R.string.ad_render_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_render_failed, AD_TAG, code, error));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_failed, AD_TAG));
            }

            @Override
            public void onExposure() {
                showToast(getString(R.string.ad_exposure, AD_TAG));
                logD(getString(R.string.ad_exposure, AD_TAG));
            }

            @Override
            public void onClick() {
                showToast(getString(R.string.ad_click, AD_TAG));
                logD(getString(R.string.ad_click, AD_TAG));
            }

            @Override
            public void onClose() {
                showToast(getString(R.string.ad_dismiss, AD_TAG));
                logD(getString(R.string.ad_dismiss, AD_TAG));
                if (mCurrentSplash != null) {
                    mCurrentSplash.destroy();
                }
                showNextAd();
            }
        });
        mAdList.add(splash);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Step5: 在页面销毁时销毁广告资源
        for (JADSplash splash : mAdList) {
            if (splash != null) {
                splash.destroy();
            }
        }
        mAdList.clear();
        mAdMaps.clear();
    }

    private void removeSplashAd() {
        mAdContainer.setVisibility(View.GONE);
        mAdContainer.removeAllViews();
    }

}