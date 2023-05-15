package com.jd.ad.demo.expressad.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.logger.Logger;
import com.jd.ad.sdk.splash.JADSplash;
import com.jd.ad.sdk.splash.JADSplashListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板 开屏参数配置与展示
 * 如果采用 独立的 activity 作为开屏 请参看 {@link ESplashAdActivity}
 * 如果只是以主页的一个 view 作为 开屏的容器，可参看本类实现。
 */
public class ESplashManagerActivity extends BaseActivity {

    private static final String AD_ID = "2525";
    private static final String AD_TAG = "ESplash";

    private ViewGroup mAdContainer; //开发者提供的开屏广告容器，用于广告渲染成功后，把广告视图添加到此容器

    private SeekBar mSeekWidthBar;  //高度，宽度的 seekBar
    private SeekBar mSeekHeightBar;

    private TextView mSeekWidthBarTv; //用于显示当前 SeekBar 进度情况
    private TextView mSeekHeightBarTv;

    private EditText mPlacementEt; //广告 ID 输入控件
    private Button mAdLoadBtn;
    private Button mMultiProcessBtn;
    private JADSplash mJADSplash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_splash_manager_activity);
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
        headBarLayout.setTitle(R.string.e_splash_title);
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

        mAdLoadBtn = findViewById(R.id.load_ad_btn);
        mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
        mAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float expressViewWidthDp = 0f, expressViewHeightDp = 0f;
                mAdContainer.setVisibility(View.VISIBLE);

                expressViewWidthDp = (int) (mSeekWidthBar.getProgress() * 1.0f / mSeekWidthBar.getMax()
                        * ScreenUtils.getScreenWidthDip(ESplashManagerActivity.this));
                expressViewHeightDp = (int) (mSeekHeightBar.getProgress() * 1.0f / mSeekHeightBar.getMax()
                        * ScreenUtils.getRealScreenHeight(ESplashManagerActivity.this));

                mAdLoadBtn.setEnabled(false);
                mAdLoadBtn.setText("广告加载中...");

                String placementId = mPlacementEt.getText() == null ? "" : mPlacementEt.getText().toString();
                //线程选择
                if (ThreadChooseUtils.isMainThread(ESplashManagerActivity.this)) {
                    loadAdAndShow(placementId, expressViewWidthDp, expressViewHeightDp);
                } else {
                    float finalExpressViewWidthDp = expressViewWidthDp;
                    float finalExpressViewHeightDp = expressViewHeightDp;
                    DemoExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadAdAndShow(placementId, finalExpressViewWidthDp, finalExpressViewHeightDp);
                        }
                    });
                }

            }
        });
        mMultiProcessBtn = findViewById(R.id.ad_multi_process);
        mMultiProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ESplashManagerActivity.this, EMultiSplashAdActivity.class));
            }
        });

        findViewById(R.id.multi_requests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ESplashManagerActivity.this, ESplashMultiRequestActivity.class));

            }
        });

    }

    private void loadAdAndShow(String slotId, float expressViewWidthDp, float expressViewHeightDp) {
        //Step1: 创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink，广告超时时间(可选)
        /**
         * 注意：
         *  1、宽度必须为屏幕宽度，高度必须大于等于屏幕高度的50%。否则影响有效曝光
         *  2、宽高单位必须为 dp
         */
        JADSlot slot =
                new JADSlot.Builder()
                        .setSlotID(slotId) //代码位ID
                        .setSize(expressViewWidthDp, expressViewHeightDp)//期望个性化模板广告view的size,单位dp，注意这里要保证传入尺寸符合申请的模版要求的比例
                        .setTolerateTime(3.5f) //广告加载容忍时间。如果设定的时间内任然没有加载到广告，则判断加载失败
                        .setSkipTime(5) //倒计时时间 单位：秒
                        .build();

        //Step2: 创建JadSplash，参数包括广告位参数和回调接口
        mJADSplash = new JADSplash(this, slot);
        //Step3: 加载广告
        mJADSplash.loadAd(new JADSplashListener() {
            @Override
            public void onLoadSuccess() {
                Logger.i("【load】广告加载成功");

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
                Logger.i("【load】广告加载失败");

                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_failed, AD_TAG));
                new DemoDialog(ESplashManagerActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {
                        finish();
                    }
                });
                removeSplashAd();
            }

            @Override
            public void onRenderSuccess(View view) {
                Logger.i("【load】广告渲染成功");


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
                Logger.i("【load】广告渲染失败");

                showToast(getString(R.string.ad_render_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_render_failed, AD_TAG, code, error));
                removeSplashAd();
                mAdLoadBtn.setEnabled(true);
                mAdLoadBtn.setText(getString(R.string.btn_ad_load_failed, AD_TAG));
            }

            @Override
            public void onExposure() {
                Logger.i("【load】广告被曝光");

                showToast(getString(R.string.ad_exposure, AD_TAG));
                logD(getString(R.string.ad_exposure, AD_TAG));

            }

            @Override
            public void onClick() {
                Logger.i("【load】广告被点击");

                showToast(getString(R.string.ad_click, AD_TAG));
                logD(getString(R.string.ad_click, AD_TAG));
            }

            @Override
            public void onClose() {
                Logger.i("【load】广告被关闭");
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
            new DemoDialog(ESplashManagerActivity.this, "Error", "0不可以做分母", new DemoDialog.dialogCallback() {
                @Override
                public void dismissCallback() {
                    initScaleView(0);
                }
            });
        }
    }

    private void initScaleView(int choosePic) {
        mSeekWidthBar.setProgress(100);
        int screenWidthDp = ScreenUtils.getScreenWidthDip(this);

        if (choosePic == 0) {
            int validHeightDp = screenWidthDp * 1920 / 1080;
            int initHeightProgress = (int) (validHeightDp * 1.0f / ScreenUtils.getRealScreenHeight(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress);
        }
    }

}
