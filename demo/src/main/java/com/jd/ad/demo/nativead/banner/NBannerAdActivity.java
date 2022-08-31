package com.jd.ad.demo.nativead.banner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.ActivityUtils;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ImageLoader;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.dl.addata.JADMaterialData;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.nativead.JADNative;
import com.jd.ad.sdk.nativead.JADNativeInteractionListener;
import com.jd.ad.sdk.nativead.JADNativeLoadListener;
import com.jd.ad.sdk.nativead.JADNativeWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * 横幅自渲染页面
 */
public class NBannerAdActivity extends BaseActivity {

    private static final String AD_ID = "2532";
    private static final String AD_TAG = "Banner";
    private static final int SEEK_BAR_PROGRESS_MAX = 100;
    private static final int SEEK_BAR_PROGRESS = 100;

    /**
     * 广告容器
     */
    private ViewGroup mAdContainer;
    /**
     * 设置广告尺寸的进度条
     */
    private SeekBar mSeekBar;
    /**
     * 显示广告尺寸
     */
    private TextView mSeedBarTv;
    /**
     * 输入广告位ID
     */
    private EditText mPlacementEt;
    /**
     * 加载按钮
     */
    private Button mAdLoadBtn;
    /**
     * 选择广告尺寸比例的索引
     */
    private int mScaleIndex = 0;
    /**
     * 广告对象
     */
    private JADNative mJADNative;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_n_banner_activity);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.n_banner_title);
        }
        initView();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        mPlacementEt = findViewById(R.id.et_code);
        mPlacementEt.setText(AD_ID);

        mAdContainer = findViewById(R.id.ad_container);

        mSeedBarTv = findViewById(R.id.seek_width_bar_progress);

        mSeekBar = findViewById(R.id.seek_width_bar);
        mSeekBar.setProgress(SEEK_BAR_PROGRESS);
        mSeekBar.setMax(SEEK_BAR_PROGRESS_MAX);
        resetDes(mSeekBar.getProgress(), mSeekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resetDes(progress, seekBar);
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
                float expressWidthDp = (mSeekBar.getProgress() * 1.0f / mSeekBar.getMax() *
                        ScreenUtils.getScreenWidthDip(NBannerAdActivity.this));
                float expressHeightDp = getExpressHeightDp(expressWidthDp);

                if (expressHeightDp == 0) {
                    showToast(getResources().getString(R.string.choose_scale_w_h));
                    return;
                }

                mAdContainer.removeAllViews();
                mAdLoadBtn.setEnabled(false);
                mAdLoadBtn.setText(getString(R.string.btn_ad_loading, AD_TAG));
                if (ThreadChooseUtils.isMainThread(NBannerAdActivity.this)) {
                    loadAndShowBanner(expressWidthDp, expressHeightDp);
                } else {
                    DemoExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadAndShowBanner(expressWidthDp, expressHeightDp);
                        }
                    });
                }


            }
        });

        initScaleView();
    }

    /**
     * 请求广告
     */
    public void loadAndShowBanner(float expressWidthDp, float expressHeightDp) {
        String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "";
        /* Step1:创建自渲染广告参数，包括广告位id、图片宽高、是否支持 deepLink
         * 注意:
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、宽高大小单位为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(codeID)
                .setImageSize(expressWidthDp, expressHeightDp)
                .setAdType(JADSlot.AdType.BANNER)
                .build();
        //Step2:加载自渲染相关广告数据，监听加载回调
        mJADNative = new JADNative(slot);
        mJADNative.loadAd(new JADNativeLoadListener() {
            @Override
            public void onLoadSuccess() {
                if (!ActivityUtils.isActivityAvailable(NBannerAdActivity.this)) {
                    return;
                }

                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
                // 获取竞价价格
                if (mJADNative != null) {
                    int price = mJADNative.getJADExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }

                //Step3:媒体创建自渲染视图
                View adView = inflateAdView();
                ViewGroup.LayoutParams lp = adView.getLayoutParams();
                if (lp == null) {
                    lp = new ViewGroup.LayoutParams(ScreenUtils.dip2px(getApplicationContext(), expressWidthDp),
                            ScreenUtils.dip2px(getApplicationContext(), expressHeightDp));
                } else {
                    lp.width = ScreenUtils.dip2px(getApplicationContext(), expressWidthDp);
                    lp.height = ScreenUtils.dip2px(getApplicationContext(), expressHeightDp);
                }
                adView.setLayoutParams(lp);
                mAdContainer.removeAllViews();
                mAdContainer.addView(adView);

            }

            @Override
            public void onLoadFailure(int code, @NonNull String error) {
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
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
    private View inflateAdView() {
        final ViewGroup adView = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_native_banner, null);
        final ImageView imageView = adView.findViewById(R.id.jad_image);
        View closeView = adView.findViewById(R.id.jad_close);
        ImageView logoView = adView.findViewById(R.id.jad_logo);
        logoView.setImageBitmap(JADNativeWidget.getLogo(this));
        if (mJADNative != null
                && mJADNative.getDataList() != null
                && !mJADNative.getDataList().isEmpty()
                && mJADNative.getDataList().get(0) != null) {

            JADMaterialData data = mJADNative.getDataList().get(0);
            if (data.getImageUrls() != null && !data.getImageUrls().isEmpty()) {
                ImageLoader.loadImage(NBannerAdActivity.this, data.getImageUrls().get(0), imageView);
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
                    showToast(getString(R.string.ad_exposure, AD_TAG));
                    logD(getString(R.string.ad_exposure, AD_TAG));
                }

                @Override
                public void onClick(@NonNull View view) {
                    showToast(getString(R.string.ad_click, AD_TAG));
                    logD(getString(R.string.ad_click, AD_TAG));
                }

                @Override
                public void onClose(View view) {
                    showToast(getString(R.string.ad_dismiss, AD_TAG));
                    logD(getString(R.string.ad_dismiss, AD_TAG));
                    //Step5:在回调中进行响应点击和关闭的操作
                    if (view != null && view.getId() == R.id.jad_close) {
                        ViewParent parent = adView.getParent();
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).removeView(adView);
                        }
                    }
                }
            });
        }
        return adView;
    }

    /**
     * 页面销毁时可对广告进行销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mJADNative != null) {
            this.mJADNative.destroy();
            this.mJADNative = null;
        }
    }

    /**
     * 根据宽度计算广告的高度
     *
     * @param expressWidthDp 广告宽度
     * @return
     */
    private float getExpressHeightDp(float expressWidthDp) {
        float expressViewHeight;
        if (mScaleIndex == 1) {
            expressViewHeight = (expressWidthDp * 360) / 720;
        } else if (mScaleIndex == 2) {
            expressViewHeight = (expressWidthDp * 280) / 644;
        } else if (mScaleIndex == 3) {
            expressViewHeight = (expressWidthDp * 160) / 640;
        } else if (mScaleIndex == 4) {
            expressViewHeight = (expressWidthDp * 100) / 640;
        } else {
            expressViewHeight = 0;
        }
        return expressViewHeight;
    }

    /**
     * 更新页面显示
     *
     * @param progress 进度值
     * @param seekBar  进度条
     */
    private void resetDes(int progress, SeekBar seekBar) {
        int width = ScreenUtils.getScreenWidthDip(this);
        float ratio = progress * 1.0f / seekBar.getMax();
        int height = (int) (ratio * width);
        mSeedBarTv.setText(getString(R.string.scale_w_h, height, width, ratio));
    }

    /**
     * 初始化广告比例选择项
     */
    private void initScaleView() {
        View view1 = findViewById(R.id.scale1);
        View view2 = findViewById(R.id.scale2);
        View view3 = findViewById(R.id.scale3);
        View view4 = findViewById(R.id.scale4);
        final List<View> list = new ArrayList<>(4);
        list.add(view1);
        list.add(view2);
        list.add(view3);
        list.add(view4);
        for (int i = 0; i < list.size(); i++) {
            View view = list.get(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < list.size(); j++) {
                        View tmp = list.get(j);
                        if (tmp.getId() == v.getId()) {
                            mScaleIndex = j + 1;
                            tmp.setBackgroundResource(R.drawable.btn_border_clicked);
                        } else {
                            tmp.setBackgroundResource(R.drawable.btn_border_normal);
                        }
                    }
                }
            });
        }

        view1.setBackgroundResource(R.drawable.btn_border_clicked);
        mScaleIndex = 1;
    }
}
