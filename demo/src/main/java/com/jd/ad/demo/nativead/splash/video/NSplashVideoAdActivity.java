package com.jd.ad.demo.nativead.splash.video;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.jd.ad.sdk.bl.initsdk.JADYunSdk;
import com.jd.ad.sdk.dl.addata.JADMaterialData;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.logger.Logger;
import com.jd.ad.sdk.nativead.JADNative;
import com.jd.ad.sdk.nativead.JADNativeLoadListener;
import com.jd.ad.sdk.nativead.JADNativeSplashInteractionListener;
import com.jd.ad.sdk.nativead.JADNativeWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 开屏媒体渲染
 * 更多接入问题可查阅 https://help-sdk-doc.jd.com/ansdkDoc/jie-ru-wen-dang.html
 */
public class NSplashVideoAdActivity extends BaseActivity {
    private static final String TAG = "NSplashVideoAdActivity";
    private static final String AD_TAG = "VideoSplash";
    private static final String SPLASH_AD_PARAM = "ad_params";

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
     * 交互方式
     * JADSlot.InteractionType.NORMAL:点击
     * JADSlot.InteractionType.SHAKE:摇一摇
     * JADSlot.InteractionType.SWIPE:滑动
     */
    private int mEventInteractionType;
    /**
     * 摇一摇动画组件
     */
    private View mShakeAnimationView;
    /**
     * 滑动动画组件
     */
    private View mSwipeAnimationView;

    /**
     * 是否静音
     */
    private boolean isMuted = true;
    /**
     * 视频播放器
     */
    private MediaPlayer mMediaPlayer;
    /**
     * 获取播放器已经播放的时长
     * 单位是：毫秒
     */
    private int mDuration = 0;
    private VideoView mVideoView;
    private View coverImage;

    public static void startActivity(Activity activity, JADSlot slot) {
        Intent intent = new Intent(activity, NSplashVideoAdActivity.class);
        intent.putExtra(SPLASH_AD_PARAM, slot);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_n_splash_video_ad_activity);

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
        mSlot = (JADSlot) getIntent().getSerializableExtra(SPLASH_AD_PARAM);
        if (ThreadChooseUtils.isMainThread(NSplashVideoAdActivity.this)) {
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
        // 由于 mSlot 是通过 Intent 获取的
        // 此处的 mSlot 并不能保证不为空
        // 所以在这里增加对 mSlot 的判断
        if (mSlot == null) {
            return;
        }
        /*
         * Step1:创建媒体渲染广告参数，包括广告位id、图片宽高、是否支持 deeplink
         * 注意：
         *  1、宽度必须为屏幕宽度，高度必须大于等于屏幕高度的50%。否则影响有效曝光
         *  2、宽高单位必须为 dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(mSlot.getSlotID())
                .setImageSize(mSlot.getAdImageWidth(), mSlot.getAdImageHeight())
                .setSkipTime(mSlot.getSkipTime())
                .setAdType(JADSlot.AdType.SPLASH)
                .build();

        //Step2:加载媒体渲染相关广告数据，监听加载回调
        mJADNative = new JADNative(slot);
        mJADNative.loadAd(new JADNativeLoadListener() {
            @Override
            public void onLoadSuccess() {
                if (!ActivityUtils.isActivityAvailable(NSplashVideoAdActivity.this)) {
                    return;
                }
                logI(getString(R.string.ad_load_success, AD_TAG));

                // 获取竞价价格
                if (mJADNative != null) {
                    int price = mJADNative.getJADExtra().getPrice();
                    logI(getString(R.string.ad_data_price, AD_TAG, price));
                }
                JADMaterialData adData = getAdData();
                //Step3:媒体创建媒体渲染视图
                if (adData != null){
                    View adView = inflateSplashView(mJADNative,adData);
                    ViewGroup.LayoutParams lp = adView.getLayoutParams();
                    if (lp == null) {
                        lp = new ViewGroup.LayoutParams(
                                ScreenUtils.dip2px(NSplashVideoAdActivity.this, mSlot.getAdImageWidth()),
                                ScreenUtils.dip2px(NSplashVideoAdActivity.this, mSlot.getAdImageHeight()));
                    } else {
                        lp.width = ScreenUtils.dip2px(NSplashVideoAdActivity.this, mSlot.getAdImageWidth());
                        lp.height = ScreenUtils.dip2px(NSplashVideoAdActivity.this, mSlot.getAdImageHeight());
                    }

                    if (mEventInteractionType == JADSlot.EventInteractionType.EVENT_INTERACTION_TYPE_NORMAL) { // 点击交互，不使用SDK提供的组件
                        mSplashContainer.removeAllViews();
                        adView.setLayoutParams(lp);
                        mSplashContainer.addView(adView);
                    } else{
                        FrameLayout parent = new FrameLayout(NSplashVideoAdActivity.this);
                        FrameLayout.LayoutParams layoutParams =
                                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.width = ScreenUtils.getScreenWidth(NSplashVideoAdActivity.this);
                        layoutParams.height = layoutParams.width * 16 / 9;
                        parent.setLayoutParams(layoutParams);

                        if (mEventInteractionType == JADSlot.EventInteractionType.EVENT_INTERACTION_TYPE_SHAKE) { //
                            // 摇一摇交互，使用SDk提供的摇一摇组件
                            mSplashContainer.removeAllViews();
                            adView.setLayoutParams(lp);
                            FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(ScreenUtils.dip2px(NSplashVideoAdActivity.this,
                                    100), ScreenUtils.dip2px(NSplashVideoAdActivity.this, 100));
                            lp1.gravity = Gravity.BOTTOM | Gravity.CENTER;
                            lp1.setMargins(0, 0, 0, 210);
                            mShakeAnimationView.setLayoutParams(lp1);
                            parent.addView(adView);
                            parent.addView(mShakeAnimationView);
                            mSplashContainer.addView(parent);
                        } else { // 滑动交互，使用SDK提供的滑动组件
                            mSplashContainer.removeAllViews();
                            adView.setLayoutParams(lp);
                            FrameLayout.LayoutParams lp2 =
                                    new FrameLayout.LayoutParams((FrameLayout.LayoutParams.MATCH_PARENT),
                                            ScreenUtils.dip2px(NSplashVideoAdActivity.this, 120));
                            lp2.gravity = Gravity.BOTTOM;
                            lp2.setMargins(0, 0, 0, 210);
                            mSwipeAnimationView.setLayoutParams(lp2);
                        parent.addView(adView);
                        parent.addView(mSwipeAnimationView);
                        mSplashContainer.addView(parent);
                        }
                    }
                }
            }

            @Override
            public void onLoadFailure(int code, String error) {
                logI(getString(R.string.ad_load_failed, AD_TAG, code, error));
                openMainActivity();
            }
        });
    }


    private View inflateSplashView(JADNative nativeAd,@NonNull JADMaterialData adData) {
        ViewGroup adView = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_native_splash_video, null);
        final ImageView imageView = adView.findViewById(R.id.iv_cover);
        mEventInteractionType = adData.getEventInteractionType();
        ImageLoader.loadImage(NSplashVideoAdActivity.this,
                adData.getImageUrls().get(0), imageView, true);
        ImageView logoView = adView.findViewById(R.id.jad_logo);
        logoView.setImageResource(R.mipmap.ad_logo_width_txt);
        final TextView skipBtn = adView.findViewById(R.id.jad_splash_skip_btn);
        List<View> clickList = new ArrayList<>();
        clickList.add(adView);
        if (mEventInteractionType == JADSlot.EventInteractionType.EVENT_INTERACTION_TYPE_SHAKE) {
            mShakeAnimationView = JADNativeWidget.getShakeAnimationView(this); // 摇一摇组件，返回View，大小为
            // (100dp,100dp) 当View attachedToWindow时，动画start,detachedFromWindow时，动画end
            clickList.add(mShakeAnimationView);
        }
        if (mEventInteractionType == JADSlot.EventInteractionType.EVENT_INTERACTION_TYPE_SWIPE_UP) {
            mSwipeAnimationView = JADNativeWidget.getSwipeAnimationView(this); // 滑动组件，返回View，大小为
            // (matchParent,120dp) 当View attachedToWindow时，动画start,detachedFromWindow时，动画end
            clickList.add(mSwipeAnimationView);
        }

        List<View> closeList = new ArrayList<>();
        closeList.add(skipBtn);
        if (nativeAd != null) {
            /*
             * Step4: 注册需要监听的视图，包括整体的广告View、点击视图列表、关闭视图列表
             * 这里非常重要，不要在View的listener中做点击操作，否则影响计费
             */
            nativeAd.registerNativeView(this, adView, clickList, closeList,
                    new JADNativeSplashInteractionListener() {

                        @Override
                        public void onExposure() {
                            logI(getString(R.string.ad_exposure, AD_TAG));
                        }

                        /*
                         * 这里不再推荐使用View 实现 JADSkipInterface 接口的方式，JadSkipInterface在之后的版本中将不再对外开放
                         * 关于倒计时视图修改推荐到这个回调中进行操作
                         */
                        @Override
                        public void onCountdown(int time) {
                            logI(getString(R.string.ad_count_down, AD_TAG, time));
                            skipBtn.setText(String.format(Locale.getDefault(), getString(R.string.ad_skip, time), time));
                        }

                        @Override
                        public void onClick(View view) {
                            logI(getString(R.string.ad_click, AD_TAG));
                        }

                        @Override
                        public void onClose(View view) {
                            logI(getString(R.string.ad_dismiss, AD_TAG));
                            //Step5:在回调中进行相应点击和关闭的操作
                            //mAdOnClicked 为true 时，表示有落地页展示，不跳转主页面
                            openMainActivity();
                        }
                    });

            coverImage = adView.findViewById(R.id.fl_ad_cover);
            mVideoView = adView.findViewById(R.id.vv_video);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer = mp;
                    setVolumeMuted(isMuted);
                    if (mJADNative != null) {
                        mVideoView.seekTo(mDuration);
                        mDuration = mVideoView.getCurrentPosition();
                        logI("video_log onPrepared " + mDuration);
                        mJADNative.getJADVideoReporter().reportVideoStart(transferDuration());
                    }
                }
            });
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    coverImage.setVisibility(View.VISIBLE);
                    if (mJADNative != null) {
                        mDuration = mVideoView.getDuration();
                        logI("video_log onCompletion " + mDuration + ", curPos:" + mVideoView.getCurrentPosition());
                        mJADNative.getJADVideoReporter().reportVideoCompleted(transferDuration());
                    }
                }
            });
            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    coverImage.setVisibility(View.VISIBLE);
                    if (mJADNative != null) {
                        mDuration = mVideoView.getCurrentPosition();
                        logI("video_log onError " + mDuration);
                        mJADNative.getJADVideoReporter().reportVideoError(transferDuration(), what, extra);
                    }
                    return false;
                }
            });

            ImageView iv_play = adView.findViewById(R.id.iv_play);
            iv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDuration = 0;
                    mVideoView.start();
                    coverImage.setVisibility(View.GONE);
                    if (mJADNative != null) {
                        logI("video_log onClick " + mDuration);
                        mJADNative.getJADVideoReporter().reportVideoWillStart();
                    }
                }
            });

            ImageView iv_volume = adView.findViewById(R.id.iv_volume);
            int muted = adData.getMuted();
            if (muted == JADSlot.AdVideoVoiceType.VOICE_NO_MUTED) {
                isMuted = false;
            }
            if (isMuted) {
                iv_volume.setImageResource(R.mipmap.player_no_volume);
            } else {
                iv_volume.setImageResource(R.mipmap.palyer_volume);
            }
            iv_volume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isMuted = !isMuted;
                    setVolumeMuted(isMuted);
                    if (isMuted) {
                        iv_volume.setImageResource(R.mipmap.player_no_volume);
                    } else {
                        iv_volume.setImageResource(R.mipmap.palyer_volume);
                    }
                }
            });
            mVideoView.setVideoPath(adData.getVideoUrl());
        }
        return adView;
    }

    private void setVolumeMuted(boolean isMuted) {
        if (mMediaPlayer == null) {
            return;
        }
        if (isMuted) {
            mMediaPlayer.setVolume(0f, 0f);
        } else {
            mMediaPlayer.setVolume(1f, 1f);
        }
    }
    /**
     * 单位转换为：秒
     *
     * @return
     */
    private float transferDuration() {
        return mDuration / 1000f;
    }

    private JADMaterialData getAdData() {
        if (mJADNative != null
                && mJADNative.getDataList() != null
                && !mJADNative.getDataList().isEmpty()
                && mJADNative.getDataList().get(0) != null) {
            JADMaterialData data = mJADNative.getDataList().get(0);
            return data;
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //根据mForceGoMain 的值确定是否将跳转主页面
        logI("SplashAd onResume = " + mForceGoMain);
//        if (mForceGoMain) {
//            openMainActivity();
//        }
        if(mJADNative!= null){
            if(mVideoView !=null){
                mVideoView.start();
                logI("video_log onResume "+mDuration);
            }
            mJADNative.getJADVideoReporter().reportVideoResume(transferDuration());

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        logI("SplashAd onPause");
        if (mJADNative != null) {
            if (mVideoView != null) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }
                mDuration = mVideoView.getCurrentPosition();
                logI("video_log onPause " + mDuration);
            }
            mJADNative.getJADVideoReporter().reportVideoPause(transferDuration());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop 后 将 mForceGoMain 设置为true 标识再次 onResume 根据mForceGoMain 的值确定是否将跳转主页面
        mForceGoMain = true;
        logI("SplashAd onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeAd();
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
        logI("SplashAd finished");
    }
}
