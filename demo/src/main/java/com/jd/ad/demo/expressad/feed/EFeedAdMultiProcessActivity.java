package com.jd.ad.demo.expressad.feed;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.feed.JADFeed;
import com.jd.ad.sdk.feed.JADFeedListener;

public class EFeedAdMultiProcessActivity extends BaseActivity {

    //默认广告位id
//    private static final String AD_ID = "2528";
    private static final String AD_ID = "8126";
    private static final String AD_TAG = "Feed";

    /**
     * 开发者提供的信息流广告容器，用于广告渲染成功后，把广告视图添加到此容器
     */
    private ViewGroup mAdContainer;

    /**
     * 设置高度的seekBar
     */
    private SeekBar mSeekWidthBar;

    /**
     * 设置高度的seekBar
     */
    private SeekBar mSeekHeightBar;

    /**
     * 用于显示当前 SeekBar宽度进度情况
     */
    private TextView mSeekWidthBarTv;

    /**
     * 用于显示当前 SeekBar高度进度情况
     */
    private TextView mSeekHeightBarTv;

    /**
     * 广告ID输入控件
     */
    private EditText mPlacementEt; //广告 ID 输入控件

    /**
     * 加载广告按钮
     */
    private Button mAdLoadBtn;

    /**
     * 广告尺寸：宽
     */
    private float mExpressViewWidthDp;

    /**
     * 广告尺寸：高
     */
    private float mExpressViewHeightDp;

    private JADFeed mJADFeed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_feed_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initViews();
    }

    /**
     * 初始化页面视图
     */
    private void initViews() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_feed_single);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPlacementEt = findViewById(R.id.et_code);
        mPlacementEt.setText(AD_ID);

        mAdContainer = findViewById(R.id.ad_container);

        mSeekWidthBar = findViewById(R.id.seek_width_bar);
        mSeekWidthBarTv = findViewById(R.id.seek_width_bar_progress);

        mSeekHeightBar = findViewById(R.id.seek_height_bar);
        mSeekHeightBarTv = findViewById(R.id.seek_height_bar_progress);

        mSeekWidthBar.setProgress(100);
        mSeekWidthBar.setMax(100);

        initScaleView(0); //默认选择 1280 * 720 的广告图片

        resetDes(mSeekWidthBar.getProgress(), mSeekWidthBar, mSeekWidthBarTv, true);
        resetDes(mSeekHeightBar.getProgress(), mSeekHeightBar, mSeekHeightBarTv, false);

        mSeekWidthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resetDes(progress, seekBar, mSeekWidthBarTv, true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekHeightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resetDes(progress, seekBar, mSeekHeightBarTv, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mAdLoadBtn = findViewById(R.id.load_ad_btn);
        mAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAdAndShow();
            }
        });
    }

    /**
     * 根据屏幕宽度计算高度
     *
     * @param scale
     * @return
     */
    public float[] getProgressWH(float scale) {
        float screenWidthDp = ScreenUtils.getScreenWidthDip(this);

        int proH = (int) (screenWidthDp / scale);
        float proW = proH * scale;
        float[] proWH = new float[]{proW, proH};
        return proWH;
    }

    /**
     * 刷新页面
     */
    private void resetDes(int progress, SeekBar seekBar, TextView desTv, boolean isWidth) {
        String format = "宽度/总宽度 = %ddp / %ddp = %.2f";
        int totalSize = ScreenUtils.getScreenWidthDip(this);
        if (!isWidth) {
            format = "高度/总高度 = %ddp / %ddp = %.2f";
            totalSize = ScreenUtils.getScreenHeightDip(this);
        }

        float ratio = progress * 1.0f / seekBar.getMax();
        int selectSize = (int) (ratio * totalSize);

        @SuppressLint("DefaultLocale")
        String value = String.format(format, selectSize, totalSize, ratio);
        desTv.setText(value);
        createScale();
    }

    /**
     * 根据比例初始化广告尺寸
     */
    private void createScale() {
        mExpressViewWidthDp = (int) (mSeekWidthBar.getProgress() * 1.0f / mSeekWidthBar.getMax()
                * ScreenUtils.getScreenWidthDip(this));
        mExpressViewHeightDp = (int) (mSeekHeightBar.getProgress() * 1.0f / mSeekHeightBar.getMax()
                * ScreenUtils.getScreenHeightDip(this));
    }

    /**
     * 信息流强制定位宽高区间，利用宽高比计算会产生区间错乱
     */
    private void initScaleView(int choosePic) {
        mSeekWidthBar.setProgress(100);
        int screenWidthDp = ScreenUtils.getScreenWidthDip(this);
        if (choosePic == 1) {
            int validHeightDp = (int) (screenWidthDp * 1.0f / 3.0);
            int initHeightProgress1 = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress1);
        } else {
            int validHeightDp = (int) (screenWidthDp * 1.0f / 1.5);
            int initHeightProgress = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress);
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
                .setSlotID(AD_ID) //广告位ID 必须正确 否则无广告返回
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
                new DemoDialog(EFeedAdMultiProcessActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
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