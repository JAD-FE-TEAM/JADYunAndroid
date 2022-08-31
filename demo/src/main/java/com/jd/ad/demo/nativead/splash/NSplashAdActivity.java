package com.jd.ad.demo.nativead.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.ActivityUtils;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ImageLoader;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.nativead.JADNative;
import com.jd.ad.sdk.nativead.JADNativeLoadListener;
import com.jd.ad.sdk.nativead.JADNativeSplashInteractionListener;
import com.jd.ad.sdk.nativead.JADNativeWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 开屏自渲染
 * 更多接入问题可查阅 https://help-sdk-doc.jd.com/ansdkDoc/jie-ru-wen-dang.html
 */
public class NSplashAdActivity extends BaseActivity {
    private static final String AD_TAG = "Splash";

    private static final String SPLASH_AD_PARAM = "ad_params";
    private static final String SPLASH_AD_TYPE_INDEX = "ad_click_area_type";
    private static final String INTERACTION_TYPE = "interaction_type";

    /**
     * 开屏广告容器
     */
    private ViewGroup mSplashContainer;
    /**
     * 开屏广告请求参数
     */
    private JADSlot mSlot;
    /**
     * 是否强制跳转到主页面
     */
    private boolean mForceGoMain = false;
    /**
     * 广告对象
     */
    private JADNative mJADNative;
    /**
     * 开屏显示类型
     */
    private int mTypeIndex = 0;

    /**
     * 交互方式
     * JadNativeSlot.InteractionType.NORMAL:点击
     * JadNativeSlot.InteractionType.SHAKE:摇一摇
     * JadNativeSlot.InteractionType.SWIPE:滑动
     */
    private int mInteractionType;
    /**
     * 摇一摇动画组件
     */
    private View mShakeAnimationView;
    /**
     * 滑动动画组件
     */
    private View mSwipeAnimationView;

    public static void startActivity(Activity activity, JADSlot slot,
                                     int typeIndex, int interaction) {
        Intent intent = new Intent(activity, NSplashAdActivity.class);
        intent.putExtra(SPLASH_AD_PARAM, slot);
        intent.putExtra(SPLASH_AD_TYPE_INDEX, typeIndex);
        intent.putExtra(INTERACTION_TYPE, interaction);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_n_splash_ad_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 延伸显示区域到刘海
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        // 设置页面全屏显示
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);


        mSplashContainer = findViewById(R.id.splash_container);
        mSlot = getIntent().getParcelableExtra(SPLASH_AD_PARAM);
        mTypeIndex = getIntent().getIntExtra(SPLASH_AD_TYPE_INDEX, 0);
        mInteractionType = getIntent().getIntExtra(INTERACTION_TYPE, JADSlot.InteractionType.NORMAL);
        if (ThreadChooseUtils.isMainThread(NSplashAdActivity.this)) {
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
        /*
         * Step1:创建自渲染广告参数，包括广告位id、图片宽高、是否支持 deeplink
         * 注意：
         *  1、宽度必须为屏幕宽度，高度必须大于等于屏幕高度的50%。否则影响有效曝光
         *  2、宽高单位必须为 dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(mSlot.getSlotID())
                .setImageSize(mSlot.getAdImageWidth(), mSlot.getAdImageHeight())
                .setSkipTime(mSlot.getSkipTime())
                .setAdType(JADSlot.AdType.SPLASH)
                .setInteractionType(mInteractionType)
                .build();

        //Step2:加载自渲染相关广告数据，监听加载回调
        mJADNative = new JADNative(slot);
        mJADNative.loadAd(new JADNativeLoadListener() {
            @Override
            public void onLoadSuccess() {
                if (!ActivityUtils.isActivityAvailable(NSplashAdActivity.this)) {
                    return;
                }
                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));

                // 获取竞价价格
                if (mJADNative != null) {
                    int price = mJADNative.getJADExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }
                //Step3:媒体创建自渲染视图
                View adView = inflateSplashView(mJADNative);
                ViewGroup.LayoutParams lp = adView.getLayoutParams();
                if (lp == null) {
                    lp = new ViewGroup.LayoutParams(
                            ScreenUtils.dip2px(NSplashAdActivity.this, mSlot.getAdImageWidth()),
                            ScreenUtils.dip2px(NSplashAdActivity.this, mSlot.getAdImageHeight()));
                } else {
                    lp.width = ScreenUtils.dip2px(NSplashAdActivity.this, mSlot.getAdImageWidth());
                    lp.height = ScreenUtils.dip2px(NSplashAdActivity.this, mSlot.getAdImageHeight());
                }
                if (mInteractionType == JADSlot.InteractionType.NORMAL) { // 点击交互，不使用SDK提供的组件
                    mSplashContainer.removeAllViews();
                    adView.setLayoutParams(lp);
                    mSplashContainer.addView(adView);
                } else if (mInteractionType == JADSlot.InteractionType.SHAKE) { // 摇一摇交互，使用SDk提供的摇一摇组件
                    mSplashContainer.removeAllViews();
                    adView.setLayoutParams(lp);
                    FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(ScreenUtils.dip2px(NSplashAdActivity.this,
                            100), ScreenUtils.dip2px(NSplashAdActivity.this, 100));
                    lp1.gravity = Gravity.BOTTOM | Gravity.CENTER;
                    lp1.setMargins(0, 0, 0, 210);
                    mShakeAnimationView.setLayoutParams(lp1);
                    FrameLayout parent = new FrameLayout(NSplashAdActivity.this);
                    parent.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                    parent.addView(adView);
                    parent.addView(mShakeAnimationView);
                    mSplashContainer.addView(parent);
                } else { // 滑动交互，使用SDK提供的滑动组件
                    mSplashContainer.removeAllViews();
                    adView.setLayoutParams(lp);
                    FrameLayout.LayoutParams lp2 =
                            new FrameLayout.LayoutParams((FrameLayout.LayoutParams.MATCH_PARENT),
                                    ScreenUtils.dip2px(NSplashAdActivity.this, 120));
                    lp2.gravity = Gravity.BOTTOM;
                    lp2.setMargins(0, 0, 0, 210);
                    mSwipeAnimationView.setLayoutParams(lp2);
                    FrameLayout parent = new FrameLayout(NSplashAdActivity.this);
                    parent.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                    parent.addView(adView);
                    parent.addView(mSwipeAnimationView);
                    mSplashContainer.addView(parent);
                }
            }

            @Override
            public void onLoadFailure(int code, String error) {
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
                openMainActivity();
            }
        });
    }


    private View inflateSplashView(JADNative nativeAd) {
        ViewGroup adView = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_splash_native, null);
        FrameLayout clickContainer = adView.findViewById(R.id.click_container);
        if (mTypeIndex == 1) {
            clickContainer.setVisibility(View.VISIBLE);
        } else {
            clickContainer.setVisibility(View.GONE);
        }
        final ImageView imageView = adView.findViewById(R.id.jad_splash_image);
        if (nativeAd != null
                && nativeAd.getDataList() != null
                && !nativeAd.getDataList().isEmpty()
                && nativeAd.getDataList().get(0) != null) {
            ImageLoader.loadImage(NSplashAdActivity.this,
                    nativeAd.getDataList().get(0).getImageUrls().get(0), imageView);
        }

        ImageView logoView = adView.findViewById(R.id.jad_logo);
        logoView.setImageResource(R.mipmap.ad_logo_width_txt);
        final TextView skipBtn = adView.findViewById(R.id.jad_splash_skip_btn);
        List<View> clickList = new ArrayList<>();
        if (mTypeIndex == 1) {
            clickList.add(clickContainer);
        } else if (mInteractionType == JADSlot.InteractionType.NORMAL) {
            clickList.add(imageView);
        }
        if (mInteractionType == JADSlot.InteractionType.SHAKE) {
            mShakeAnimationView = JADNativeWidget.getShakeAnimationView(this); // 摇一摇组件，返回View，大小为
            // (100dp,100dp) 当View attachedToWindow时，动画start,detachedFromWindow时，动画end
            clickList.add(mShakeAnimationView);
        }
        if (mInteractionType == JADSlot.InteractionType.SWIPE) {
            mSwipeAnimationView = JADNativeWidget.getSwipeAnimationView(this); // 滑动组件，返回View，大小为
            // (matchParent,120dp) 当View attachedToWindow时，动画start,detachedFromWindow时，动画end
            clickList.add(mSwipeAnimationView);
        }
        List<View> closeList = new ArrayList<>();
        closeList.add(skipBtn);
        if (nativeAd != null) {
            this.mJADNative = nativeAd;
            /*
             * Step4: 注册需要监听的视图，包括整体的广告View、点击视图列表、关闭视图列表
             * 这里非常重要，不要在View的listener中做点击操作，否则影响计费
             */
            nativeAd.registerNativeView(this, adView, clickList, closeList,
                    new JADNativeSplashInteractionListener() {

                        @Override
                        public void onExposure() {
                            showToast(getString(R.string.ad_exposure, AD_TAG));
                            logD(getString(R.string.ad_exposure, AD_TAG));
                        }

                        /*
                         * 这里不再推荐使用View 实现 JADSkipInterface 接口的方式，JadSkipInterface在之后的版本中将不再对外开放
                         * 关于倒计时视图修改推荐到这个回调中进行操作
                         */
                        @Override
                        public void onCountdown(int time) {
                            logD(getString(R.string.ad_count_down, AD_TAG, time));
                            skipBtn.setText(String.format(Locale.getDefault(), getString(R.string.ad_skip, time), time));
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
                            //mAdOnClicked 为true 时，表示有落地页展示，不跳转主页面
                            openMainActivity();
                        }
                    });
        }
        return adView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //根据mForceGoMain 的值确定是否将跳转主页面
        logD("SplashAd onResume = " + mForceGoMain);
        if (mForceGoMain) {
            openMainActivity();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        logD("SplashAd onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop 后 将 mForceGoMain 设置为true 标识再次 onResume 根据mForceGoMain 的值确定是否将跳转主页面
        mForceGoMain = true;
        logD("SplashAd onStop");
    }

    @Override
    protected void onDestroy() {
        closeAd();
        super.onDestroy();
    }

    private void closeAd() {
        if (this.mJADNative != null) {
            this.mJADNative.destroy();
            this.mJADNative = null;
        }
        // 销毁动效 view
        if (mShakeAnimationView != null) {
            mShakeAnimationView = null;
        }
        if (mSwipeAnimationView != null) {
            mSwipeAnimationView = null;
        }
    }

    private void openMainActivity() {
        mSplashContainer.removeAllViews();
        finish();
        logD("SplashAd finished");
    }
}
