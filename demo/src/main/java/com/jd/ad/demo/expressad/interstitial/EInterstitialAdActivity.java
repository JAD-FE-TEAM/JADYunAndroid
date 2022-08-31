package com.jd.ad.demo.expressad.interstitial;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
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
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.interstitial.JADInterstitial;
import com.jd.ad.sdk.interstitial.JADInterstitialListener;


/**
 * 模板插屏 样例。
 * 更多接入问题可查阅 https://help-sdk-doc.jd.com/ansdkDoc/jie-ru-wen-dang.html
 */
public class EInterstitialAdActivity extends BaseActivity {

    private static final String AD_ID = "2534";
    private static final String AD_TAG = "Interstitial";

    private SeekBar mSeekWidthBar; //高度，宽度的 seekBar
    private SeekBar mSeekHeightBar;

    private TextView mSeekWidthBarTv; //用于显示当前 SeekBar 进度情况
    private TextView mSeekHeightBarTv;

    private EditText mPlacementEt; //广告 ID 输入控件

    private TextView mSelectWHRatioTv; //当前选择的宽高比

    private JADInterstitial mJADInterstitial;

    private Button mAdLoadBtn;

    private float mScale = 1.5f;
    private float whRation = 1.5f;
    private boolean isChecked = false;
    private TextView mScaleTv1, mScaleTv2, mScaleTv3, mScaleTv4;
    private View mScaleView1, mScaleView2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_inter_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initViews();
    }

    private void initViews() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_inter_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPlacementEt = findViewById(R.id.et_code);
        mPlacementEt.setText(AD_ID);

        mSeekWidthBar = findViewById(R.id.seek_width_bar);
        mSeekWidthBarTv = findViewById(R.id.seek_width_bar_progress);

        mSelectWHRatioTv = findViewById(R.id.width_div_height);

        mSeekHeightBar = findViewById(R.id.seek_height_bar);
        mSeekHeightBarTv = findViewById(R.id.seek_height_bar_progress);

        mScaleView1 = findViewById(R.id.scale1);
        mScaleView2 = findViewById(R.id.scale2);

        mSeekWidthBar.setProgress(100);
        mSeekWidthBar.setMax(100);

        initScaleView(0); //默认选择 800 * 1200 广告图片

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

        CheckBox checkbox = findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EInterstitialAdActivity.this.isChecked = isChecked;
                if (!isChecked) {
                    mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv1.setClickable(false);
                    mScaleTv2.setClickable(false);
                    mScaleTv3.setClickable(false);
                    mScaleTv4.setClickable(false);
                    mSeekWidthBar.setEnabled(true);
                    mSeekHeightBar.setEnabled(true);
                    mScaleView1.setEnabled(true);
                    mScaleView2.setEnabled(true);
                    initScaleView(0);
                } else {
                    mScaleTv1.setClickable(true);
                    mScaleTv2.setClickable(true);
                    mScaleTv3.setClickable(true);
                    mScaleTv4.setClickable(true);
                    mSeekWidthBar.setEnabled(false);
                    mSeekHeightBar.setEnabled(false);
                    mScaleView1.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleView2.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleView1.setEnabled(false);
                    mScaleView2.setEnabled(false);
                    mScale = whRation;
                }
            }
        });

        mScaleTv1 = findViewById(R.id.tv_scale1);
        mScaleTv2 = findViewById(R.id.tv_scale2);
        mScaleTv3 = findViewById(R.id.tv_scale3);
        mScaleTv4 = findViewById(R.id.tv_scale4);

        mScaleTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 0.61f;
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
                mScale = 0.75f;
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
                mScale = 1.32f;
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
                mScale = 1.64f;
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
        if (isChecked) {
            mScaleTv1.setClickable(true);
            mScaleTv2.setClickable(true);
            mScaleTv3.setClickable(true);
            mScaleTv4.setClickable(true);
        } else {
            mScaleTv1.setClickable(false);
            mScaleTv2.setClickable(false);
            mScaleTv3.setClickable(false);
            mScaleTv4.setClickable(false);
        }

        mAdLoadBtn = findViewById(R.id.load_ad_btn);
        mAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float expressWidthDp = 0f, expressHeightDp = 0f;

                if (isChecked) {
                    float[] proWH = getProgressWH(mScale);
                    expressWidthDp = proWH[0];
                    expressHeightDp = proWH[1];
                } else {
                    expressWidthDp = (int) (mSeekWidthBar.getProgress() * 1.0f / mSeekWidthBar.getMax() *
                            ScreenUtils.getScreenWidthDip(EInterstitialAdActivity.this));
                    expressHeightDp = (int) (mSeekHeightBar.getProgress() * 1.0f / mSeekHeightBar.getMax()
                            * ScreenUtils.getScreenHeightDip(EInterstitialAdActivity.this));
                }

                mAdLoadBtn.setEnabled(false);
                mAdLoadBtn.setText("广告加载中...");
                String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "";
                if (ThreadChooseUtils.isMainThread(EInterstitialAdActivity.this)) {
                    loadAdAndShow(codeID, expressWidthDp, expressHeightDp);
                } else {
                    float finalExpressWidthDp = expressWidthDp;
                    float finalExpressHeightDp = expressHeightDp;
                    DemoExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadAdAndShow(codeID, finalExpressWidthDp, finalExpressHeightDp);
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

    /**
     * 点击按钮加载插屏广告
     */
    @SuppressLint("SetTextI18n")
    public void loadAdAndShow(String codeID, float expressWidthDp, float expressHeightDp) {

        //Step1: 创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink
        /**
         * 注意:
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、宽高大小单位为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(codeID) //广告位ID 必须正确 否则无广告返回
                .setSize(expressWidthDp, expressHeightDp) //单位必须为dp 必须正确 否则无广告返回
                .build();
        //Step2: 创建 JadInterstitial 对象，参数包括广告位参数和回调接口
        mJADInterstitial = new JADInterstitial(this, slot);
        //Step3: 加载 JadInterstitial
        mJADInterstitial.loadAd(new JADInterstitialListener() {

            @Override
            public void onLoadSuccess() {
                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
                // 获取竞价价格
                if (mJADInterstitial != null) {
                    int price = mJADInterstitial.getExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }
            }

            @Override
            public void onLoadFailure(int code, @NonNull String error) {
                logE("JadInterstitial AD onAdLoadFailed [" + code + ", " + error + "]");
                new DemoDialog(EInterstitialAdActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {
                        if (!isChecked) {
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
            public void onRenderSuccess(@NonNull View view) {
                showToast(getString(R.string.ad_render_success, AD_TAG));
                logD(getString(R.string.ad_render_success, AD_TAG));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));

                //Step4: 在render成功之后调用show方法来展示广告
                if (!isFinishing()) {
                    mJADInterstitial.showAd(EInterstitialAdActivity.this);
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
        if (mJADInterstitial != null) {
            mJADInterstitial.destroy();
            mJADInterstitial = null;
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
            new DemoDialog(EInterstitialAdActivity.this, "Error", "0不可以做分母", new DemoDialog.dialogCallback() {
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

        // 插屏广告的宽高比(开发者更具自己情况具体设置)，符合宽高比区间： [0.61 - 0.75] 或 [1.32 - 1.64]
        boolean isPic1 = whRation >= 0.61 && whRation <= 0.75;   //对应图片尺寸 1200 * 800
        boolean isPic2 = whRation >= 1.32 && whRation <= 1.64;  //对应图片尺寸 480 * 320

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

        if (isPic1 || isPic2) {
            mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
        } else {
            mSelectWHRatioTv.setTextColor(Color.RED);
        }
    }

    private void initScaleView(int choosePic) {
        mSeekWidthBar.setProgress(100);
        int screenWidthDp = ScreenUtils.getScreenWidthDip(this);

        if (choosePic == 1) {
            int validHeightDp1 = screenWidthDp * 320 / 480;
            int initHeightProgress1 = (int) (validHeightDp1 * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress1);
            mScaleView2.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            int validHeightDp = screenWidthDp * 1200 / 800;
            int initHeightProgress = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress);
            mScaleView1.setBackgroundResource(R.drawable.btn_border_clicked);
            @SuppressLint("DefaultLocale")
            String whRatio = String.format("选择的宽高比 = %.2f", whRation);
            mSelectWHRatioTv.setText(whRatio);
        }
    }
}
