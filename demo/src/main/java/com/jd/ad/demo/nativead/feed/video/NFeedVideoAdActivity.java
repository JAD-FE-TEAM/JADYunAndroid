package com.jd.ad.demo.nativead.feed.video;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.ActivityUtils;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ImageLoader;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.dl.addata.JADMaterialData;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.nativead.JADNative;
import com.jd.ad.sdk.nativead.JADNativeInteractionListener;
import com.jd.ad.sdk.nativead.JADNativeLoadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 信息流媒体渲染
 */
public class NFeedVideoAdActivity extends BaseActivity {

    private static final String AD_TAG = "Feed";

    /**
     * 1540514974-单视频规格集
     * 1540516523-多视频规格集
     */
    private static final String AD_ID = "1540514974"; //"1450589543";

    /**
     * 广告容器
     */
    private ViewGroup mAdContainer;
    /**
     * 加载按钮
     */
    private Button mAdLoadBtn;
    /**
     * 广告对象
     */
    private JADNative mJADNative;

    private float expressViewWidth;
    private float expressViewHeight;

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
    /**
     * 视频是否播放完成
     */
    private boolean isVideoPlayed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_n_feed_video_activity);
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
        headBarLayout.setTitle(R.string.n_feed_video_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdContainer = findViewById(R.id.ad_container);
        expressViewWidth = ScreenUtils.getScreenWidthDip(NFeedVideoAdActivity.this);
        expressViewHeight = expressViewWidth / 1.5f;

        //宽高比区间：[1.36-1.64] 或 [1.64 - 1.92]
        mAdLoadBtn = findViewById(R.id.load_ad_btn);
        mAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdLoadBtn.setEnabled(false);
                mAdLoadBtn.setText("广告加载中...");
                mAdContainer.removeAllViews();
                if (ThreadChooseUtils.isMainThread(NFeedVideoAdActivity.this)) {
                    loadAndShowAd(expressViewWidth, expressViewHeight);
                } else {
                    DemoExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadAndShowAd(expressViewWidth, expressViewHeight);
                        }
                    });
                }
            }
        });
    }

    /**
     * 请求广告
     */
    private void loadAndShowAd(float expressViewWidth, float expressViewHeight) {
        /*
         * Step1:创建媒体渲染广告参数，包括广告位id、图片宽高、是否支持 deepLink
         * 注意:
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、宽高大小单位为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(AD_ID)
                .setImageSize(expressViewWidth, expressViewHeight)
                .setAdType(JADSlot.AdType.FEED)
                .build();
        //Step2:加载媒体渲染相关广告数据，监听加载回调
        mJADNative = new JADNative(slot);
        mJADNative.loadAd(new JADNativeLoadListener() {
            @Override
            public void onLoadSuccess() {
                logI(getString(R.string.ad_load_success, AD_TAG));
                if (!ActivityUtils.isActivityAvailable(NFeedVideoAdActivity.this)) {
                    return;
                }
                // 获取竞价价格
                if (mJADNative != null) {
                    int price = mJADNative.getJADExtra().getPrice();
                    logI(getString(R.string.ad_data_price, AD_TAG, price));
                }
                JADMaterialData data = getAdData();
                if (data != null) {
                    mAdLoadBtn.setEnabled(true);
                    mAdLoadBtn.setText(getString(R.string.btn_ad_load_default, AD_TAG));
                    //Step3:媒体创建媒体渲染视图
                    View adView = inflateAdView(data);
                    mAdContainer.removeAllViews();
                    mAdContainer.addView(adView);
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
    private View inflateAdView(JADMaterialData data) {
        if (data.getMediaSpecSetType() == JADSlot.MediaSpecSetType.MEDIA_SPEC_SET_TYPE_FEED16_9_SINGLE_VIDEO) {
            return getHorizontalVideoAdView(data);
        }
        return getVerticalVideoAdView(data);
    }


    /**
     * 渲染水平16：9尺寸视频广告视图
     *
     * @param data
     * @return
     */
    private View getHorizontalVideoAdView(JADMaterialData data) {
        final ViewGroup adView =
                (ViewGroup) getLayoutInflater().inflate(R.layout.demo_layout_native_video,
                        null);
        View video = adView.findViewById(R.id.fl_ad);
        ViewGroup.LayoutParams layoutParams = video.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.width = ScreenUtils.getScreenWidth(this) - ScreenUtils.dip2px(this, 24);
        layoutParams.height = layoutParams.width * 9 / 16;
        video.setLayoutParams(layoutParams);

        if (data != null) {
            bindCommonView(data, adView);
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
     * 渲染垂直9：16尺寸视频广告视图
     *
     * @param data
     * @return
     */
    private View getVerticalVideoAdView(JADMaterialData data) {
        final ViewGroup adView =
                (ViewGroup) this.getLayoutInflater().inflate(R.layout.demo_layout_native_video,
                        null);
        View video = adView.findViewById(R.id.fl_ad);
        ViewGroup.LayoutParams layoutParams = video.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.width = ScreenUtils.getScreenWidth(this) - ScreenUtils.dip2px(this, 100);
        layoutParams.height = layoutParams.width * 16 / 9;
        video.setLayoutParams(layoutParams);

        if (data != null) {
            bindCommonView(data, adView);
        }
        return adView;
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

    private void bindCommonView(JADMaterialData data, ViewGroup adView) {
        TextView tv_title = adView.findViewById(R.id.tv_title);
        ImageView iv_close = adView.findViewById(R.id.iv_close);
        ImageView iv_cover = adView.findViewById(R.id.iv_cover);

        if (data != null) {
            tv_title.setText(data.getTitle());
            if (data.getImageUrls() != null && !data.getImageUrls().isEmpty()) {
                ImageLoader.loadImage(this, data.getImageUrls().get(0), iv_cover, true);
            }
        }
        List<View> clickList = new ArrayList<>();
        clickList.add(adView);
        List<View> closeList = new ArrayList<>();
        closeList.add(iv_close);
        /*
         * Step4: 注册需要监听的视图，包括整体的广告View、点击视图列表、关闭视图列表
         * 这里非常重要，不要在View的listener中做点击操作，否则影响计费
         */
        mJADNative.registerNativeView(this, adView, clickList, closeList,
                new JADNativeInteractionListener() {

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
                        if (view != null && view.getId() == R.id.iv_close) {
                            ViewParent parent = adView.getParent();
                            if (parent != null && parent instanceof ViewGroup) {
                                ((ViewGroup) parent).removeView(adView);
                            }
                        }
                    }

                });
        View coverImage = adView.findViewById(R.id.fl_ad_cover);
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
                    isVideoPlayed = true;
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
                mVideoView.setVideoPath(data.getVideoUrl());
                mDuration = 0;
                isVideoPlayed = false;
                mVideoView.start();
                coverImage.setVisibility(View.GONE);
                if (mJADNative != null) {
                    logI("video_log onClick " + mDuration);
                    mJADNative.getJADVideoReporter().reportVideoWillStart();
                }
            }
        });

        ImageView iv_volume = adView.findViewById(R.id.iv_volume);
        int muted = data.getMuted();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
            if(mJADNative!= null){
                if(mVideoView !=null){
                    mVideoView.start();
                    logI("video_log onResume "+mDuration);
                }
                mJADNative.getJADVideoReporter().reportVideoResume(transferDuration());

            }
        }


    @Override
    protected void onPause() {
        super.onPause();
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

    /**
     * 单位转换为：秒
     *
     * @return
     */
    private float transferDuration() {
        return mDuration / 1000f;
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
}
