package com.jd.ad.demo.nativead.interstitial;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
 * 插屏自渲染
 */
public class NInterstitialAdActivity extends BaseActivity {

    private static final String AD_ID = "2534";
    private static final String AD_TAG = "Interstitial";
    /**
     * 设置广告宽度的进度条
     */
    private SeekBar mSeekBar;

    /**
     * 显示广告宽度
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
        setContentView(R.layout.demo_n_inter_ad_activity);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.n_inter_title);
        }
        initViews();
    }

    /**
     * 初始化页面视图
     */
    private void initViews() {
        mPlacementEt = findViewById(R.id.et_code);
        mPlacementEt.setText(AD_ID);

        mSeedBarTv = findViewById(R.id.seek_width_bar_progress);

        mSeekBar = findViewById(R.id.seek_width_bar);
        mSeekBar.setProgress(100);
        mSeekBar.setMax(100);
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

        final View view2 = findViewById(R.id.scale2);//2:3
        final View view3 = findViewById(R.id.scale3);//3:2

        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleIndex = 2;
                view2.setBackgroundResource(R.drawable.btn_border_clicked);
                view3.setBackgroundResource(R.drawable.btn_border_normal);
            }
        });

        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleIndex = 3;
                view2.setBackgroundResource(R.drawable.btn_border_normal);
                view3.setBackgroundResource(R.drawable.btn_border_clicked);
            }
        });

        mScaleIndex = 2;
        view2.setBackgroundResource(R.drawable.btn_border_clicked);
        view3.setBackgroundResource(R.drawable.btn_border_normal);

        mAdLoadBtn = findViewById(R.id.load_ad_btn);
        mAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float expressViewWidthDp = (int) (mSeekBar.getProgress() * 1.0f / mSeekBar.getMax() *
                        ScreenUtils.getScreenWidthDip(NInterstitialAdActivity.this));

                float expressViewHeightDp;
                if (mScaleIndex == 2) {
                    expressViewHeightDp = (expressViewWidthDp * 3) / 2;
                } else if (mScaleIndex == 3) {
                    expressViewHeightDp = (expressViewWidthDp * 2) / 3;
                } else {
                    showToast(getResources().getString(R.string.choose_scale_w_h));
                    return;
                }

                mAdLoadBtn.setEnabled(false);
                mAdLoadBtn.setText(getString(R.string.btn_ad_loading, AD_TAG));
                String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "";

                if (ThreadChooseUtils.isMainThread(NInterstitialAdActivity.this)) {
                    loadAndShowAd(codeID, expressViewWidthDp, expressViewHeightDp);
                } else {
                    DemoExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadAndShowAd(codeID, expressViewWidthDp, expressViewHeightDp);
                        }
                    });

                }

            }
        });

    }

    /**
     * 点击按钮加载插屏广告
     */
    @SuppressLint("SetTextI18n")
    public void loadAndShowAd(String codeID, float expressViewWidthDp, float expressViewHeightDp) {

        /*
         * Step1:创建自渲染广告参数，包括广告位id、图片宽高、是否支持 deepLink
         * 注意:
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、宽高大小单位为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(codeID)
                .setImageSize(expressViewWidthDp, expressViewHeightDp)
                .setAdType(JADSlot.AdType.INTERSTITIAL)
                .build();

        //Step2:加载自渲染相关广告数据，监听加载回调
        mJADNative = new JADNative(slot);
        mJADNative.loadAd(new JADNativeLoadListener() {
            @Override
            public void onLoadSuccess() {
                if (!ActivityUtils.isActivityAvailable(NInterstitialAdActivity.this)) {
                    return;
                }
                // 获取竞价价格
                if (mJADNative != null) {
                    int price = mJADNative.getJADExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }

                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
                //Step3:媒体创建自渲染视图
                inflateAdView(expressViewWidthDp, expressViewHeightDp);
            }

            @Override
            public void onLoadFailure(int code, String error) {
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
    private void inflateAdView(float expressViewWidth, float expressViewHeight) {
        ViewGroup adView = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_native_interstitial, null);
//        TextView titleView = adView.findViewById(R.id.jad_title);
//        TextView descView = adView.findViewById(R.id.tt_insert_ad_text);
        ImageView logoView = adView.findViewById(R.id.jad_logo);
        logoView.setImageBitmap(JADNativeWidget.getLogo(this));

        final ImageView imageView = adView.findViewById(R.id.jad_native_insert_ad_img);
        View closeView = adView.findViewById(R.id.jad_close);

        final Dialog adDialog = new Dialog(this, R.style.jad_native_insert_dialog);
        adDialog.setCancelable(true);

        adDialog.setContentView(adView);
        if (mJADNative != null
                && mJADNative.getDataList() != null
                && !mJADNative.getDataList().isEmpty()
                && mJADNative.getDataList().get(0) != null) {


            JADMaterialData data = mJADNative.getDataList().get(0);
//            titleView.setText(data.getAdTitle());
            if (data.getImageUrls() != null && !data.getImageUrls().isEmpty()) {
                ImageLoader.loadImage(NInterstitialAdActivity.this, data.getImageUrls().get(0), imageView);
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
                public void onClick(View view) {
                    showToast(getString(R.string.ad_click, AD_TAG));
                    logD(getString(R.string.ad_click, AD_TAG));
                }

                @Override
                public void onClose(View view) {
                    showToast(getString(R.string.ad_dismiss, AD_TAG));
                    logD(getString(R.string.ad_dismiss, AD_TAG));
                    //Step5:在回调中进行相应点击和关闭的操作
                    if (adDialog.isShowing()) {
                        adDialog.dismiss();
                    }
                }

            });
        }

        adDialog.show();
        adDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = adDialog.getWindow().getAttributes();
        lp.width = ScreenUtils.dip2px(this, expressViewWidth);
        lp.height = ScreenUtils.dip2px(this, expressViewHeight);
        adDialog.getWindow().setAttributes(lp);
        //注意：请不要在注册的view中再添加 OnClickListener
//        closeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                adDialog.dismiss();
//            }
//        });
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
     * 更新页面显示
     *
     * @param progress 进度值
     * @param seekBar  进度条
     */
    private void resetDes(int progress, SeekBar seekBar) {
        String format = "宽度/总宽度 = %ddp / %ddp = %.2f";
        int width = ScreenUtils.getScreenWidthDip(this);
        float ratio = progress * 1.0f / seekBar.getMax();
        int height = (int) (ratio * width);

        @SuppressLint("DefaultLocale")
        String value = String.format(format, height, width, ratio);
        mSeedBarTv.setText(value);
    }
}
