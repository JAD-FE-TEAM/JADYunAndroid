package com.jd.ad.demo.expressad.splash;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.jd.ad.sdk.splash.JADSplash;
import com.jd.ad.sdk.splash.JADSplashListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 多进程模版开屏页面
 * 若需在该进程进行数据展示，则需要在该进程进行初始化,
 * 该示例已在DemoApplication中进行初始化，则该页面无需再进行初始化
 * 若未在Application中进行初始化，则需进行初始化操作 initOaid() 和 initJdSdk() 操作
 */
public class EMultiSplashAdActivity extends BaseActivity {

    private static final String AD_ID = "2525";
    private static final String AD_TAG = "Splash";

    private ViewGroup mAdContainer; //开发者提供的开屏广告容器，用于广告渲染成功后，把广告视图添加到此容器

    private SeekBar mSeekWidthBar;  //高度，宽度的 seekBar
    private SeekBar mSeekHeightBar;

    private TextView mSeekWidthBarTv; //用于显示当前 SeekBar 进度情况
    private TextView mSeekHeightBarTv;

    private EditText mPlacementEt; //广告 ID 输入控件
    private TextView mSelectWHRatioTv; //当前选择的宽高比
    private Button mAdLoadBtn;
    private TextView mScaleTv1, mScaleTv2, mScaleTv3, mScaleTv4;
    private View mScaleView1, mScaleView2;
    private JADSplash mJADSplash;

    private float mScale = 1.5f;
    private float whRation = 1.5f;
    private boolean isChecked = false;
    private int mTypeIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_multi_splash_manager_activity);
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
        headBarLayout.setTitle(R.string.e_splash_multi_process_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPlacementEt = findViewById(R.id.et_code);
        mPlacementEt.setText(AD_ID);

        mAdContainer = findViewById(R.id.ad_container);

        mSeekHeightBarTv = findViewById(R.id.seek_height_bar_progress);
        mSeekHeightBar = findViewById(R.id.seek_height_bar);

        mSeekWidthBar = findViewById(R.id.seek_width_bar);
        mSeekWidthBarTv = findViewById(R.id.seek_width_bar_progress);

        mSelectWHRatioTv = findViewById(R.id.width_div_height);

        mScaleView1 = findViewById(R.id.scale1);
        mScaleView2 = findViewById(R.id.scale2);

        mSeekWidthBar.setProgress(100);
        mSeekWidthBar.setMax(100);

        initScaleView(0); //默认选择 1080 * 1920 广告图片

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
                EMultiSplashAdActivity.this.isChecked = isChecked;
                if (!isChecked) {
                    mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv1.setClickable(false);
                    mScaleTv2.setClickable(false);
                    mScaleTv3.setClickable(false);
                    mScaleTv4.setClickable(false);
                    mSeekHeightBar.setEnabled(true);
                    mSeekWidthBar.setEnabled(true);
                    mScaleView1.setEnabled(true);
                    mScaleView2.setEnabled(true);
                    initScaleView(0);
                } else {
                    mScaleTv1.setClickable(true);
                    mScaleTv2.setClickable(true);
                    mScaleTv3.setClickable(true);
                    mScaleTv4.setClickable(true);
                    mSeekHeightBar.setEnabled(false);
                    mSeekWidthBar.setEnabled(false);
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
                mScale = 0.49f;
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
                mScale = 0.60f;
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
                mScale = 0.61f;
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
                mScale = 0.75f;
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
        mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
        mAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float expressViewWidthDp = 0f, expressViewHeightDp = 0f;
                mAdContainer.setVisibility(View.VISIBLE);
                if (isChecked) {
                    float[] proWH = getProgressWH(mScale);
                    expressViewWidthDp = proWH[0];
                    expressViewHeightDp = proWH[1];
                } else {
                    expressViewWidthDp = (int) (mSeekWidthBar.getProgress() * 1.0f / mSeekWidthBar.getMax()
                            * ScreenUtils.getScreenWidthDip(EMultiSplashAdActivity.this));
                    expressViewHeightDp = (int) (mSeekHeightBar.getProgress() * 1.0f / mSeekHeightBar.getMax()
                            * ScreenUtils.getRealScreenHeight(EMultiSplashAdActivity.this));
                }
                mAdLoadBtn.setEnabled(false);
                mAdLoadBtn.setText("广告加载中...");
                if (ThreadChooseUtils.isMainThread(EMultiSplashAdActivity.this)) {
                    loadAdAndShow(expressViewWidthDp, expressViewHeightDp);
                } else {
                    float finalExpressViewWidthDp = expressViewWidthDp;
                    float finalExpressViewHeightDp = expressViewHeightDp;
                    DemoExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadAdAndShow(finalExpressViewWidthDp, finalExpressViewHeightDp);
                        }
                    });
                }

            }
        });
        initClickTypeView();
    }

    public float[] getProgressWH(float scale) {
        float screenWidthDp = ScreenUtils.getScreenWidthDip(this);

        int proH = (int) (screenWidthDp / scale);
        float proW = proH * scale;
        float[] proWH = new float[]{proW, proH};
        return proWH;
    }

    private void loadAdAndShow(float expressViewWidthDp, float expressViewHeightDp) {
        String placementId = mPlacementEt.getText() == null ? "" : mPlacementEt.getText().toString();
        //Step1: 创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink，广告超时时间(可选)
        /**
         * 注意：
         *  1、宽度必须为屏幕宽度，高度必须大于等于屏幕高度的50%。否则影响有效曝光
         *  2、宽高单位必须为 dp
         */
        JADSlot slot =
                new JADSlot.Builder()
                        .setSlotID(placementId) //代码位ID
                        .setSize(expressViewWidthDp, expressViewHeightDp)//期望个性化模板广告view的size,单位dp，注意这里要保证传入尺寸符合申请的模版要求的比例
                        .setTolerateTime(3.5f) //广告加载容忍时间。如果设定的时间内任然没有加载到广告，则判断加载失败
                        .setSkipTime(5) //倒计时时间 单位：秒
                        .setSplashClickAreaType(getClickAreaType())
                        .build();

        //Step2: 创建JadSplash，参数包括广告位参数和回调接口
        mJADSplash = new JADSplash(this, slot);
        //Step3: 加载广告
        mJADSplash.loadAd(new JADSplashListener() {
            @Override
            public void onLoadSuccess() {
                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                // 获取竞价价格
                if (mJADSplash.getJADExtra() != null) {
                    int price = mJADSplash.getJADExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }
            }

            @Override
            public void onLoadFailure(int code, String error) {
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_failed, AD_TAG));
                new DemoDialog(EMultiSplashAdActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {
                        finish();
                    }
                });
                removeSplashAd();
            }

            @Override
            public void onRenderSuccess(View view) {
                showToast(getString(R.string.ad_render_success, AD_TAG));
                logD(getString(R.string.ad_render_success, AD_TAG));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));

                if (!isFinishing()) {
                    //Step4:  在render成功之后调用, 将返回广告视图 adView 添加到自己广告容器 splashAdContainer 视图中
                    mAdContainer.removeAllViews();
                    mAdContainer.addView(view);
                } else {
                    removeSplashAd();
                }
            }

            @Override
            public void onRenderFailure(int code, String error) {
                showToast(getString(R.string.ad_render_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_render_failed, AD_TAG, code, error));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_failed, AD_TAG));
                removeSplashAd();
            }

            @Override
            public void onClick() {
                showToast(getString(R.string.ad_click, AD_TAG));
                logD(getString(R.string.ad_click, AD_TAG));
            }

            @Override
            public void onExposure() {
                showToast(getString(R.string.ad_exposure, AD_TAG));
                logD(getString(R.string.ad_exposure, AD_TAG));
            }

            @Override
            public void onClose() {
                showToast(getString(R.string.ad_dismiss, AD_TAG));
                logD(getString(R.string.ad_dismiss, AD_TAG));
                removeSplashAd();
            }
        });

    }

    private void removeSplashAd() {
        mAdContainer.setVisibility(View.GONE);
        mAdContainer.removeAllViews();
        if (mJADSplash != null) {
            mJADSplash.destroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Step5: 在页面销毁时销毁广告资源
        if (mJADSplash != null) {
            mJADSplash.destroy();
        }
    }

    private void resetDes(int progress, SeekBar seekBar, TextView desTv, boolean isWidth) {
        String format = "宽度/总宽度 = %ddp / %ddp = %.2f";
        int totalSize = ScreenUtils.getScreenWidthDip(this);
        if (!isWidth) {
            format = "高度/总高度 = %ddp / %ddp = %.2f";
            totalSize = ScreenUtils.getRealScreenHeight(this);
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
            new DemoDialog(EMultiSplashAdActivity.this, "Error", "0不可以做分母", new DemoDialog.dialogCallback() {
                @Override
                public void dismissCallback() {
                    initScaleView(0);
                }
            });
        }
        int hMax = mSeekHeightBar.getMax();
        float heightDp = hProgress * 1.0f / hMax * ScreenUtils.getRealScreenHeight(this);
        whRation = widthDp / heightDp;

        @SuppressLint("DefaultLocale")
        String whRatio = String.format("选择的宽高比 = %.2f", whRation);
        mSelectWHRatioTv.setText(whRatio);

        // 开屏广告的宽高比(开发者更具自己情况具体设置)，符合宽高比区间： [0.49 - 0.61） 或 [0.61 - 0.75]
        boolean isPic1 = whRation >= 0.49 && whRation < 0.61;   //对应图片尺寸 1200 * 800
        boolean isPic2 = whRation >= 0.61 && whRation <= 0.75;  //对应图片尺寸 480 * 320

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
            int validHeightDp1 = screenWidthDp * 1200 / 800;
            int initHeightProgress1 = (int) (validHeightDp1 * 1.0f / ScreenUtils.getRealScreenHeight(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress1);
            mScaleView2.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            int validHeightDp = screenWidthDp * 1920 / 1080;
            int initHeightProgress = (int) (validHeightDp * 1.0f / ScreenUtils.getRealScreenHeight(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress);
            mScaleView1.setBackgroundResource(R.drawable.btn_border_clicked);
            @SuppressLint("DefaultLocale")
            String whRatio = String.format("选择的宽高比 = %.2f", whRation);
            mSelectWHRatioTv.setText(whRatio);
        }
    }

    private void initClickTypeView() {
        View view1 = findViewById(R.id.type1);
        View view2 = findViewById(R.id.type2);
        View view3 = findViewById(R.id.type3);
        View view4 = findViewById(R.id.type4);
        View view5 = findViewById(R.id.type5);
        final List<View> list = new ArrayList<>(5);
        list.add(view1);
        list.add(view2);
        list.add(view3);
        list.add(view4);
        list.add(view5);
        for (int i = 0; i < list.size(); i++) {
            View view = list.get(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < list.size(); j++) {
                        View tmp = list.get(j);
                        if (tmp.getId() == v.getId()) {
                            mTypeIndex = j;
                            tmp.setBackgroundResource(R.drawable.btn_border_clicked);
                        } else {
                            tmp.setBackgroundResource(R.drawable.btn_border_normal);
                        }
                    }
                }
            });
        }

        view1.setBackgroundResource(R.drawable.btn_border_clicked);
    }

    /**
     * 设置模版开屏广告点击类型
     * 注意⚠️： ClickAreaType 枚举定义的类型作废，v130之后使用 ClickStyle接口中定义的类型
     * SERVER:           默认,采用服务端配置
     * ONLY_TEXT:        只显示文案，全屏可点击
     * ONLY_TEXT_CLICK:  只有文案部分可点击
     * NORMAL:           不处理，无文案，全屏可点击
     * SHOW_TEXT_MASK:   全屏可点击，且显示文字蒙层
     */
    private int getClickAreaType() {
        if (mTypeIndex == 0) {
            return JADSlot.ClickStyle.SERVER;
        } else if (mTypeIndex == 1) {
            return JADSlot.ClickStyle.ONLY_TEXT;
        } else if (mTypeIndex == 2) {
            return JADSlot.ClickStyle.ONLY_TEXT_CLICK;
        } else if (mTypeIndex == 3) {
            return JADSlot.ClickStyle.NORMAL;
        } else if (mTypeIndex == 4) {
            return JADSlot.ClickStyle.SHOW_TEXT_MASK;
        }
        return JADSlot.ClickStyle.SERVER;
    }
}