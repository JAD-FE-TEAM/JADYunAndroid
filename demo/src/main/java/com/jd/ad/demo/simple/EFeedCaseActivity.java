package com.jd.ad.demo.simple;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.feed.JADFeed;
import com.jd.ad.sdk.feed.JADFeedListener;

public class EFeedCaseActivity extends BaseActivity {
    private static final String AD_TAG = "Feed";

    private ViewGroup mAdContainer; //开发者提供的信息流广告容器，用于广告渲染成功后，把广告视图添加到此容器

    private JADFeed mJADFeed;

    private float mExpressViewWidthDp = 360;
    private float mExpressViewHeightDp = 236;

    private String mCodeID = "8126";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_simple_e_feed_ad_show_activity);
        initViews();
    }

    private void initViews() {
        mAdContainer = findViewById(R.id.ad_container);
        if (ThreadChooseUtils.isMainThread(EFeedCaseActivity.this)) {
            loadAdAndShow();
        } else {
            DemoExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    loadAdAndShow();
                }
            });
        }

    }

    private void loadAdAndShow() {
        //Step1: 创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink
        /**
         * 注意：
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、单位必须为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(mCodeID) //广告位ID 必须正确 否则无广告返回
                .setSize(mExpressViewWidthDp, mExpressViewHeightDp) //单位必须为dp 必须正确 否则无广告返回
                .setCloseButtonHidden(false) //是否关闭 关闭 按钮
                .build();
        //Step2: 创建 JADFeed，参数包括广告位参数和回调接口
        mJADFeed = new JADFeed(this, slot);
        //Step3: 加载 JADFeed
        mJADFeed.loadAd(new JADFeedListener() {

            @Override
            public void onLoadSuccess() {
                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                // 获取竞价价格
                if (mJADFeed != null) {
                    int price = mJADFeed.getExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }
            }

            @Override
            public void onLoadFailure(int code, @NonNull String error) {
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
                new DemoDialog(EFeedCaseActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {
                        finish();
                    }
                });
            }

            @Override
            public void onRenderSuccess(@NonNull View adView) {
                showToast(getString(R.string.ad_render_success, AD_TAG));
                logD(getString(R.string.ad_render_success, AD_TAG));
                //Step4: 在render成功之后调用, 将返回广告视图adView添加到自己广告容器adContainer视图中
                if (adView != null && !isFinishing()) {
                    mAdContainer.removeAllViews();
                    mAdContainer.addView(adView);
                }
            }

            @Override
            public void onRenderFailure(int code, @NonNull String error) {
                showToast(getString(R.string.ad_render_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_render_failed, AD_TAG, code, error));
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
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Step5: 在页面销毁时close广告，来销毁其中使用到的资源
        if (mJADFeed != null) {
            mJADFeed.destroy();
            mJADFeed = null;
        }
    }
}
