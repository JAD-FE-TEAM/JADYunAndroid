package com.jd.ad.demo.expressad.splash;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.jd.ad.demo.DemoMainActivity;
import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.splash.JADSplash;
import com.jd.ad.sdk.splash.JADSplashListener;

/**
 * 模板开屏
 */
public class ESplashAdActivity extends BaseActivity {

    private static final String SLOT_ID = "2525";
    private static final String AD_TAG = "Splash";

    /**
     * 开屏广告容器
     */
    private ViewGroup mSplashAdContainer;

    /**
     * 开屏广告实例
     */
    private JADSplash mJADSplash;

    /**
     * 是否强制跳转到主页面
     * 主要用于控制，从落地页返回后是否马上进入主页。
     * 根据activity 的生命周期函数回调，在onStop 中，使
     * mForceGoMain=true。
     * 然后在onResume时，判断mForceGoMain的值，来决定是否马上
     * 进入主页
     */
    private boolean mForceGoMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_splash_ad_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 延伸显示区域到刘海
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        ScreenUtils.screenAdapt(this);
        mSplashAdContainer = findViewById(R.id.ad_container);

        if (ThreadChooseUtils.isMainThread(ESplashAdActivity.this)) {
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

    @Override
    protected void onResume() {
        super.onResume();
        //根据mForceGoMain 的值确定是否将跳转主页面
        if (mForceGoMain) {
            openMainActivity();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop 后 将 mForceGoMain 设置为true 标识再次 onResume 根据mForceGoMain 的值确定是否将跳转主页面
        mForceGoMain = true;
    }

    /**
     * 加载广告
     */
    private void loadAdAndShow() {
        //准备广告位 宽高  单位 dp
        int widthDp = ScreenUtils.getScreenWidthDip(this);
        //100dp demo 布局文件中 底部 logo 图片高度,开发者根据自身实际情况设置
        int heightDp = ScreenUtils.getRealScreenHeight(this) - 100;
        // 加载广告的容忍时间
        float mTolerateTime = 3.5f;
        //跳过按钮的起始时间
        int mSkitTime = 5;

        //Step1: 创建广告位参数，参数包括广告位ID，宽高，倒计时时间，广告超时时间(可选)
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(SLOT_ID)//广告位ID
                .setSize(widthDp, heightDp)//期望个性化模板广告view的size,单位dp，注意这里要保证传入尺寸符合申请的模版要求的比例
                .setTolerateTime(mTolerateTime) //广告加载容忍时间。如果设定的时间内任然没有加载到广告，则判断加载失败
                .setSkipTime(mSkitTime) //倒计时时间 单位：秒
                .setSplashClickAreaType(JADSlot.ClickStyle.SERVER)//设置模版开屏广告点击类型,
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
                openMainActivity();
            }

            @Override
            public void onRenderSuccess(View adView) {
                showToast(getString(R.string.ad_render_success, AD_TAG));
                logD(getString(R.string.ad_render_success, AD_TAG));
                if (!isFinishing()) {
                    //Step4:  在render成功之后调用, 将返回广告视图 adView 添加到自己广告容器 splashAdContainer 视图中
                    mSplashAdContainer.addView(adView);
                }
            }

            @Override
            public void onRenderFailure(int code, String error) {
                showToast(getString(R.string.ad_render_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_render_failed, AD_TAG, code, error));
                openMainActivity();
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
                openMainActivity();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Step5: 在页面销毁时销毁广告资源
        mJADSplash.destroy();
    }

    /**
     * 跳转到主页面
     */
    private void openMainActivity() {
        startActivity(new Intent(this, DemoMainActivity.class));

        //移除广告视图
        mSplashAdContainer.removeAllViews();
        finish();
    }
}