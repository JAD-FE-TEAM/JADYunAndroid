package com.jd.ad.demo.nativead.feed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.ActivityUtils;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ImageLoader;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.bl.initsdk.JADYunSdk;
import com.jd.ad.sdk.dl.addata.JADMaterialData;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.logger.Logger;
import com.jd.ad.sdk.nativead.JADNative;
import com.jd.ad.sdk.nativead.JADNativeInteractionListener;
import com.jd.ad.sdk.nativead.JADNativeLoadListener;
import com.jd.ad.sdk.nativead.JADNativeWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * 信息流媒体渲染
 */
public class NFeedAdActivity extends BaseActivity {
    private static final String TAG = "NFeedAdActivity";
    //    private static final String AD_ID = "829118588";
    private static final String AD_ID = "1377798457";
    private static final String AD_TAG = "Feed";
    /**
     * 广告容器
     */
    private ViewGroup mAdContainer;
    /**
     * 设置广告宽度的进度条
     */
    private SeekBar mSeekWidthBar;
    /**
     * 显示广告宽度
     */
    private TextView mSeedWidthBarTv;
    /**
     * 设置广告高度的进度条
     */
    private SeekBar mSeekHeightBar;
    /**
     * 显示广告高度
     */
    private TextView mSeedHeightBarTv;
    /**
     * 输入广告位ID
     */
    private EditText mPlacementEt;
    /**
     * 加载按钮
     */
    private Button mAdLoadBtn;

    /**
     * 广告对象
     */
    private JADNative mJADNative;
    /**
     * 摇一摇动画组件
     */
    private View mShakeAnimationView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_n_feed_activity);
        initViews();
    }

    /**
     * 初始化页面
     */
    @SuppressLint("SetTextI18n")
    private void initViews() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.n_feed_title);
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
        mSeedWidthBarTv = findViewById(R.id.seek_width_bar_progress);

        mSeekHeightBar = findViewById(R.id.seek_height_bar);
        mSeedHeightBarTv = findViewById(R.id.seek_height_bar_progress);

        mSeekWidthBar.setProgress(100);
        mSeekWidthBar.setMax(100);

        //宽高比区间：[1.36-1.64] 或 [1.64 - 1.92]
        int screenWidthDp = ScreenUtils.getScreenWidthDip(this);
        int validHeightDp = (int) (screenWidthDp * 1.0f / 1.5); // 1.5 为初始化的宽高比，符合 宽高比区间：[1.36-1.64] 或 [1.64 - 1.92]
        int initHeightProgress = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);

        mSeekHeightBar.setProgress(initHeightProgress);
        mSeekHeightBar.setMax(100);

        resetDes(mSeekWidthBar.getProgress(), mSeekWidthBar, mSeedWidthBarTv, true);
        resetDes(mSeekHeightBar.getProgress(), mSeekHeightBar, mSeedHeightBarTv, false);

        mSeekWidthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resetDes(progress, seekBar, mSeedWidthBarTv, true);
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
                resetDes(progress, seekBar, mSeedHeightBarTv, false);
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
                int expressViewWidth = (int) (mSeekWidthBar.getProgress() * 1.0f / mSeekWidthBar.getMax()
                        * ScreenUtils.getScreenWidthDip(NFeedAdActivity.this));

                int expressViewHeight = (int) (mSeekHeightBar.getProgress() * 1.0f / mSeekHeightBar.getMax()
                        * ScreenUtils.getScreenHeightDip(NFeedAdActivity.this));

                mAdLoadBtn.setEnabled(false);
                mAdLoadBtn.setText("广告加载中...");
                mAdContainer.removeAllViews();

                String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "";
                if (ThreadChooseUtils.isMainThread(NFeedAdActivity.this)) {
                    loadAndShowAd(codeID, expressViewWidth, expressViewHeight);
                } else {
                    DemoExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadAndShowAd(codeID, expressViewWidth, expressViewHeight);
                        }
                    });
                }
            }
        });
    }

    /**
     * 请求广告
     */
    private void loadAndShowAd(String codeID, float expressViewWidth, float expressViewHeight) {

        /*
         * Step1:创建媒体渲染广告参数，包括广告位id、图片宽高、是否支持 deepLink
         * 注意:
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、宽高大小单位为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(codeID)
                .setImageSize(expressViewWidth, expressViewHeight)
                .setAdType(JADSlot.AdType.FEED)
                .build();
        //Step2:加载媒体渲染相关广告数据，监听加载回调
        mJADNative = new JADNative(slot);
        mJADNative.loadAd(new JADNativeLoadListener() {
            @Override
            public void onLoadSuccess() {
                logI(getString(R.string.ad_load_success, AD_TAG));
                if (!ActivityUtils.isActivityAvailable(NFeedAdActivity.this)) {
                    return;
                }
                // 获取竞价价格
                if (mJADNative != null) {
                    int price = mJADNative.getJADExtra().getPrice();
                    logI(getString(R.string.ad_data_price, AD_TAG, price));
                }

                if (mJADNative != null
                        && mJADNative.getDataList() != null
                        && !mJADNative.getDataList().isEmpty()
                        && mJADNative.getDataList().get(0) != null) {


                    mAdLoadBtn.setEnabled(true);
                    mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
                    //Step3:媒体创建媒体渲染视图
                    inflateAdView(expressViewWidth, expressViewHeight);
                } else {
                    onLoadFailure(-1, "load ad is empty");
                }
            }

            @Override
            public void onLoadFailure(int code, String error) {
                logI(getString(R.string.ad_load_failed, AD_TAG, code, error));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_failed, AD_TAG));
            }
        });
    }

    /**
     * 渲染广告视图
     *
     * @return 广告视图
     */
    private View inflateAdView(float expressViewWidth, float expressViewHeight) {
        final ViewGroup adView = (ViewGroup) getLayoutInflater().inflate(R.layout.demo_layout_native_feed, null);
        TextView titleView = adView.findViewById(R.id.jad_title);
//        TextView descView = adView.findViewById(R.id.tt_insert_ad_text);
        final ImageView imageView = adView.findViewById(R.id.jad_image);
        View closeView = adView.findViewById(R.id.jad_close);
        ImageView logoView = adView.findViewById(R.id.jad_logo);
        Bitmap logo = JADNativeWidget.getJDLogo(this);
        if (logo != null) {
            logoView.setImageBitmap(logo);
        }
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ScreenUtils.dip2px(getApplicationContext(), expressViewWidth),
                ScreenUtils.dip2px(getApplicationContext(), expressViewHeight));
        adView.setLayoutParams(lp);

        if (mJADNative != null
                && mJADNative.getDataList() != null
                && !mJADNative.getDataList().isEmpty()
                && mJADNative.getDataList().get(0) != null) {
            JADMaterialData data = mJADNative.getDataList().get(0);
            titleView.setText(data.getTitle());
            if (data.getImageUrls() != null && !data.getImageUrls().isEmpty()) {
                ImageLoader.loadImage(NFeedAdActivity.this, data.getImageUrls().get(0), imageView
                        , true);
            }

            List<View> list = new ArrayList<>();
            list.add(imageView);
            List<View> closeList = new ArrayList<>();
            closeList.add(closeView);
            /*
             * Step4: 注册需要监听的视图，包括整体的广告View、点击视图列表、关闭视图列表
             * 这里非常重要，不要在View的listener中做点击操作，否则影响计费
             */
            mJADNative.registerNativeView(this, adView, list, closeList, new JADNativeInteractionListener() {

                @Override
                public void onExposure() {
                    logI(getString(R.string.ad_exposure, AD_TAG));
                }

                @Override
                public void onClick(View view) {
                    logI(getString(R.string.ad_click, AD_TAG));
                }

                @Override
                public void onClose(View view) {
                    logI(getString(R.string.ad_dismiss, AD_TAG));
                    //Step5:在回调中进行相应点击和关闭的操作
                    if (view != null && view.getId() == R.id.jad_close) {
                        ViewParent parent = adView.getParent();
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).removeAllViews();
                        }
                    }
                }

            });
        }
        mAdContainer.removeAllViews();
        mAdContainer.addView(adView);
        mShakeAnimationView = JADNativeWidget.getShakeAnimationView(NFeedAdActivity.this,"点击或摇一摇");
        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(ScreenUtils.dip2px(NFeedAdActivity.this,
                100), ScreenUtils.dip2px(NFeedAdActivity.this, 100));
        lp1.gravity = Gravity.CENTER;
        mShakeAnimationView.setLayoutParams(lp1);
        mAdContainer.addView(mShakeAnimationView);
        return adView;
    }


    /**
     * 页面销毁时可对广告进行销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁动效 view
        if (mShakeAnimationView != null) {
            mShakeAnimationView = null;
        }
        if (this.mJADNative != null) {
            this.mJADNative.destroy();
            this.mJADNative = null;
        }
    }

    /**
     * 更新页面显示
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

    }
}
