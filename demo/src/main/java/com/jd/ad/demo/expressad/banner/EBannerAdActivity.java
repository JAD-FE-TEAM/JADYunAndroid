package com.jd.ad.demo.expressad.banner;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.banner.JADBanner;
import com.jd.ad.sdk.banner.JADBannerListener;
import com.jd.ad.sdk.dl.model.JADSlot;

/**
 * 模板横幅(BANNER) 样例。
 */
public class EBannerAdActivity extends BaseActivity {

    private static final String AD_ID = "2532"; //Demo 模板横幅默认广告位
    private static final String AD_TAG = "Banner";
    /**
     * 开发者提供的横幅广告容器，用于广告渲染成功后，把广告视图添加到此容器
     */
    private ViewGroup mAdContainer;

    /**
     * 设置高度的seekBar
     */
    private SeekBar mSeekWidthBar;

    /**
     * 设置宽度的seekBar
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
    private EditText mPlacementEt;

    /**
     * 当前选择的宽高比
     */
    private TextView mSelectWHRatioTv;

    /**
     * 广告对象
     */
    private JADBanner mJADBanner;

    /**
     * 加载广告按钮
     */
    private Button mAdLoadBtn;

    /**
     * 选择广告的比例
     */
    private float mScale = 1.5f;

    /**
     * 广告的宽高比
     */
    private float whRation = 1.5f;

    /**
     * 广告比例选择器
     */
    private CheckBox mCheckbox;

    /**
     * 该比例是否被选择
     */
    private boolean mIsChecked = false;

    /**
     * 比例边界
     */
    private TextView mScaleTv1, mScaleTv2, mScaleTv3, mScaleTv4, mScaleTv5, mScaleTv6, mScaleTv7, mScaleTv8;

    /**
     * 素材尺寸
     */
    private View mScaleView1, mScaleView2, mScaleView3, mScaleView4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_banner_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initView();
    }

    private void initView() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_banner_title);
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

        mSelectWHRatioTv = findViewById(R.id.width_div_height);

        mScaleView1 = findViewById(R.id.scale1);
        mScaleView2 = findViewById(R.id.scale2);
        mScaleView3 = findViewById(R.id.scale3);
        mScaleView4 = findViewById(R.id.scale4);

        mSeekWidthBar.setProgress(100);
        mSeekWidthBar.setMax(100);

        initScaleView(0); //默认选择 720 * 360 广告图片

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

        mScaleView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initScaleView(0);
            }
        });
        mScaleView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initScaleView(1);
            }
        });
        mScaleView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initScaleView(2);
            }
        });
        mScaleView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initScaleView(3);
            }
        });

        mCheckbox = findViewById(R.id.checkbox);
        mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsChecked = isChecked;
                if (!isChecked) {
                    mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv5.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv6.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv7.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv8.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv1.setClickable(false);
                    mScaleTv2.setClickable(false);
                    mScaleTv3.setClickable(false);
                    mScaleTv4.setClickable(false);
                    mScaleTv5.setClickable(false);
                    mScaleTv6.setClickable(false);
                    mScaleTv7.setClickable(false);
                    mScaleTv8.setClickable(false);
                    mSeekWidthBar.setEnabled(true);
                    mSeekHeightBar.setEnabled(true);
                    mScaleView1.setEnabled(true);
                    mScaleView2.setEnabled(true);
                    mScaleView3.setEnabled(true);
                    mScaleView4.setEnabled(true);
                    initScaleView(0);
                } else {
                    mScaleTv1.setClickable(true);
                    mScaleTv2.setClickable(true);
                    mScaleTv3.setClickable(true);
                    mScaleTv4.setClickable(true);
                    mScaleTv5.setClickable(true);
                    mScaleTv6.setClickable(true);
                    mScaleTv7.setClickable(true);
                    mScaleTv8.setClickable(true);
                    mSeekWidthBar.setEnabled(false);
                    mSeekHeightBar.setEnabled(false);
                    mScaleView1.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleView2.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleView3.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleView4.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleView1.setEnabled(false);
                    mScaleView2.setEnabled(false);
                    mScaleView3.setEnabled(false);
                    mScaleView4.setEnabled(false);
                    mScale = whRation;
                }
            }
        });

        mScaleTv1 = findViewById(R.id.tv_scale1);
        mScaleTv2 = findViewById(R.id.tv_scale2);
        mScaleTv3 = findViewById(R.id.tv_scale3);
        mScaleTv4 = findViewById(R.id.tv_scale4);
        mScaleTv5 = findViewById(R.id.tv_scale5);
        mScaleTv6 = findViewById(R.id.tv_scale6);
        mScaleTv7 = findViewById(R.id.tv_scale7);
        mScaleTv8 = findViewById(R.id.tv_scale8);

        mScaleTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv5.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv6.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv7.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv8.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 1.76f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
            }
        });
        mScaleTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv5.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv6.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv7.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv8.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 2.14f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
            }
        });
        mScaleTv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv5.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv6.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv7.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv8.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 2.15f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
            }
        });
        mScaleTv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv5.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv6.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv7.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv8.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 2.57f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
            }
        });
        mScaleTv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv5.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv6.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv7.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv8.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 3.52f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
            }
        });
        mScaleTv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv5.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv6.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv7.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv8.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 4.48f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
            }
        });
        mScaleTv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv5.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv6.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv7.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv8.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 5.63f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
            }
        });
        mScaleTv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv5.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv6.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv7.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv8.setBackgroundResource(R.drawable.btn_border_clicked);
                mScale = 7.17f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
            }
        });

        mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv5.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv6.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv7.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv8.setBackgroundResource(R.drawable.btn_border_normal);

        if (mIsChecked) {
            mScaleTv1.setClickable(true);
            mScaleTv2.setClickable(true);
            mScaleTv3.setClickable(true);
            mScaleTv4.setClickable(true);
            mScaleTv5.setClickable(true);
            mScaleTv6.setClickable(true);
            mScaleTv7.setClickable(true);
            mScaleTv8.setClickable(true);
        } else {
            mScaleTv1.setClickable(false);
            mScaleTv2.setClickable(false);
            mScaleTv3.setClickable(false);
            mScaleTv4.setClickable(false);
            mScaleTv5.setClickable(false);
            mScaleTv6.setClickable(false);
            mScaleTv7.setClickable(false);
            mScaleTv8.setClickable(false);
        }


        mAdLoadBtn = findViewById(R.id.load_ad_btn);
        mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
        mAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float expressWidthDp = 0f, expressHeightDp = 0f;

                if (mIsChecked) {
                    float[] proWH = getProgressWH(mScale);
                    expressWidthDp = proWH[0];
                    expressHeightDp = proWH[1];
                } else {
                    expressWidthDp = (int) (mSeekWidthBar.getProgress() * 1.0f / mSeekWidthBar.getMax() *
                            ScreenUtils.getScreenWidthDip(EBannerAdActivity.this));
                    expressHeightDp = (int) (mSeekHeightBar.getProgress() * 1.0f / mSeekHeightBar.getMax()
                            * ScreenUtils.getScreenHeightDip(EBannerAdActivity.this));
                }
                mAdContainer.removeAllViews();
                mAdLoadBtn.setEnabled(false);
                mAdLoadBtn.setText("广告加载中...");
                if (ThreadChooseUtils.isMainThread(EBannerAdActivity.this)) {
                    loadAdAndShow(expressWidthDp, expressHeightDp);
                } else {
                    float finalExpressWidthDp = expressWidthDp;
                    float finalExpressHeightDp = expressHeightDp;
                    DemoExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadAdAndShow(finalExpressWidthDp, finalExpressHeightDp);
                        }
                    });
                }

            }
        });
    }

    public float[] getProgressWH(float scale) {
        float screenWidthDp = ScreenUtils.getScreenWidthDip(this);
        int proH = (int) (screenWidthDp / scale);
        float proW = proH * scale;
        float[] proWH = new float[]{proW, proH};
        return proWH;
    }

    @SuppressLint("SetTextI18n")
    private void loadAdAndShow(float expressWidthDp, float expressHeightDp) {
        //Step1: 创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink
        String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "";

        /*
         * 注意:
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、宽高大小单位为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(codeID) //广告位ID 必须正确 否则无广告返回
                .setSize(expressWidthDp, expressHeightDp)  //单位必须为dp 必须正确 否则无广告返回
                .setCloseButtonHidden(false) //是否关闭 关闭 按钮
                .build();
        //Step2: 创建 JADBanner，参数包括广告位参数和回调接口
        mJADBanner = new JADBanner(this, slot);
        //Step3: 加载广告
        mJADBanner.loadAd(new JADBannerListener() {
            @Override
            public void onLoadSuccess() {
                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
                // 获取竞价价格
                if (mJADBanner != null && mJADBanner.getExtra() != null) {
                    int price = mJADBanner.getExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }
            }

            @Override
            public void onLoadFailure(int code, @NonNull String error) {
                new DemoDialog(EBannerAdActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {
                        if (!mIsChecked) {
                            initScaleView(0);
                        }
                    }
                });
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_failed, AD_TAG));
            }

            @Override
            public void onRenderSuccess(@NonNull View adView) {
                //Step4: 在render成功之后调用, 将返回广告视图adView添加到自己广告容器 adContainer 视图中
                showToast(getString(R.string.ad_render_success, AD_TAG));
                logD(getString(R.string.ad_render_success, AD_TAG));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
                if (!isFinishing()) {
                    mAdContainer.removeAllViews();
                    mAdContainer.addView(adView);
                }
            }

            @Override
            public void onRenderFailure(int code, @NonNull String error) {
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
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Step5: 在页面销毁时close广告，来销毁其中使用到的资源
        if (mJADBanner != null) {
            mJADBanner.destroy();
            mJADBanner = null;
        }
    }

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


        int wProgress = mSeekWidthBar.getProgress();
        int wMax = mSeekWidthBar.getMax();
        float widthDp = wProgress * 1.0f / wMax * ScreenUtils.getScreenWidthDip(this);

        int hProgress = mSeekHeightBar.getProgress();
        if (hProgress == 0) {
            new DemoDialog(EBannerAdActivity.this, "Error", "0不可以做分母", new DemoDialog.dialogCallback() {
                @Override
                public void dismissCallback() {
                    initScaleView(0);
                }
            });
        }
        int hMax = mSeekHeightBar.getMax();
        float heightDp = hProgress * 1.0f / hMax * ScreenUtils.getScreenHeightDip(this);


        whRation = widthDp / heightDp;

        @SuppressLint("DefaultLocale")
        String whRatio = String.format("选择的宽高比 = %.2f", whRation);
        mSelectWHRatioTv.setText(whRatio);

        // 横幅广告的宽高比(开发者更具自己情况具体设置)，符合宽高比区间：[1.76-2.15）或 [2.15 - 2.57] 或 [3.52 - 4.48] 或 [5.63 - 7.17]
        boolean isPic1 = whRation >= 1.76 && whRation < 2.15;   //对应图片尺寸 720 * 360
        boolean isPic2 = whRation >= 2.15 && whRation <= 2.57;  //对应图片尺寸 644 * 280
        boolean isPic3 = whRation >= 3.52 && whRation <= 4.48;  //对应图片尺寸 640 * 160
        boolean isPic4 = whRation >= 5.63 && whRation <= 7.17;  //对应图片尺寸 640 * 100
        if (isPic1) {
            mScaleView1.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            mScaleView1.setBackgroundResource(R.drawable.btn_border_normal);
        }

        if (isPic2) {
            mScaleView2.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            mScaleView2.setBackgroundResource(R.drawable.btn_border_normal);
        }

        if (isPic3) {
            mScaleView3.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            mScaleView3.setBackgroundResource(R.drawable.btn_border_normal);
        }

        if (isPic4) {
            mScaleView4.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            mScaleView4.setBackgroundResource(R.drawable.btn_border_normal);
        }

        if (isPic1 || isPic2 || isPic3 || isPic4) {
            mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
        } else {
            mSelectWHRatioTv.setTextColor(Color.RED);
        }
    }

    private void initScaleView(int choosePic) {

        mSeekWidthBar.setProgress(100);
        int screenWidthDp = ScreenUtils.getScreenWidthDip(this);
        switch (choosePic) {
            case 1:
                int validHeightDp1 = screenWidthDp * 280 / 644;
                int initHeightProgress1 = (int) (validHeightDp1 * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
                mSeekHeightBar.setProgress(initHeightProgress1);
                mScaleView2.setBackgroundResource(R.drawable.btn_border_clicked);
                break;
            case 2:
                int validHeightDp2 = screenWidthDp * 160 / 640;
                int initHeightProgress2 = (int) (validHeightDp2 * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
                mSeekHeightBar.setProgress(initHeightProgress2);
                mScaleView3.setBackgroundResource(R.drawable.btn_border_clicked);
                break;
            case 3:
                int validHeightDp3 = screenWidthDp * 100 / 640;
                int initHeightProgress3 = (int) (validHeightDp3 * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
                mSeekHeightBar.setProgress(initHeightProgress3);
                mScaleView4.setBackgroundResource(R.drawable.btn_border_clicked);
                break;
            default:
                int validHeightDp = screenWidthDp * 360 / 720;
                int initHeightProgress = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
                mSeekHeightBar.setProgress(initHeightProgress);
                mScaleView1.setBackgroundResource(R.drawable.btn_border_clicked);
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", whRation);
                mSelectWHRatioTv.setText(whRatio);
                break;
        }
    }
}
