package com.jd.ad.demo.nativead.video;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.utils.ActivityUtils;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ImageLoader;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.dl.addata.JADMaterialData;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.logger.Logger;
import com.jd.ad.sdk.nativead.JADNative;
import com.jd.ad.sdk.nativead.JADNativeInteractionListener;
import com.jd.ad.sdk.nativead.JADNativeLoadListener;
import com.jd.ad.sdk.nativead.JADNativeWidget;

import java.util.ArrayList;
import java.util.List;

public class NFeedVideoAdWrapper {
    private static final String AD_TAG = "Feed";
    private final Activity mContext;
    private View mAdView;
    private final JADSlot mAdSlot;
    private JADNative mJADNative;
    /**
     * 摇一摇动画组件
     */
    private View mShakeAnimationView;
    /**
     * 是否静音
     */
    private boolean isMuted = true;

    private MediaPlayer mMediaPlayer;
    private VideoView mVideoView;
    /**
     * 获取播放器已经播放的时长
     * 单位是：毫秒
     */
    private int mDuration = 0;


    public interface OnAdLoadListener {
        void onLoadSuccess(NFeedVideoAdWrapper feedAdWrapper);

        void onLoadFailure(int code, String error);
    }

    public NFeedVideoAdWrapper(Activity context, JADSlot slot) {
        this.mContext = context;
        this.mAdSlot = slot;

    }

    public void loadAd(OnAdLoadListener onAdLoadListener) {
        if (ThreadChooseUtils.isMainThread(mContext)) {
            loadAdInner(onAdLoadListener);
        } else {
            DemoExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    loadAdInner(onAdLoadListener);
                }
            });
        }
    }

    public void loadAdInner(OnAdLoadListener onAdLoadListener) {
        //Step1:创建 JADNative，参数包括广告位参数和回调接口
        mJADNative = new JADNative(mAdSlot);
        //Step2:加载媒体渲染相关广告数据，监听加载回调
        mJADNative.loadAd(new JADNativeLoadListener() {
            @Override
            public void onLoadSuccess() {
                logI(mContext.getString(R.string.ad_load_success, AD_TAG) + ", slotId:" + mAdSlot.getSlotID());
                if (!ActivityUtils.isActivityAvailable(mContext)) {
                    return;
                }
                // 获取竞价价格
                if (mJADNative != null) {
                    int price = mJADNative.getJADExtra().getPrice();
                    logI(mContext.getString(R.string.ad_data_price, AD_TAG, price) + ", slotId:" + mAdSlot.getSlotID());
                }
                JADMaterialData data = getAdData();
                if (data != null) {
                    //Step3:媒体创建媒体渲染视图
                    mAdView = inflateAdView(data);
                    onAdLoadListener.onLoadSuccess(NFeedVideoAdWrapper.this);
                } else {
                    onLoadFailure(-1, "load ad is empty" + ", slotId:" + mAdSlot.getSlotID());
                }
            }

            @Override
            public void onLoadFailure(int code, String error) {
                logI(mContext.getString(R.string.ad_load_failed, AD_TAG, code, error) + ", slotId:" + mAdSlot.getSlotID());
                onAdLoadListener.onLoadFailure(code, error);
            }
        });
    }

    public View getAdView() {
        return mAdView;
    }

    public void destroy() {
        // 销毁动效 view
        if (mShakeAnimationView != null) {
            mShakeAnimationView = null;
        }
        if (mJADNative != null) {
            mJADNative.destroy();
        }
    }

    private View inflateAdView(JADMaterialData data) {
        if (data.getMediaSpecSetType() == JADSlot.MediaSpecSetType.MEDIA_SPEC_SET_TYPE_FEED16_9_SINGLE_VIDEO ||
                data.getMediaSpecSetType() == JADSlot.MediaSpecSetType.MEDIA_SPEC_SET_TYPE_FEED9_16_SINGLE_VIDEO) {
            return getVideoAdView(data);
        }
        return getImageAdView(data);
    }


    /**
     * 渲染图片广告视图
     *
     * @return 广告视图
     */
    private View getImageAdView(JADMaterialData data) {
        final ViewGroup adView =
                (ViewGroup) mContext.getLayoutInflater().inflate(R.layout.demo_layout_native_feed,
                        null);
        if (data == null) {
            return adView;
        }
        TextView titleView = adView.findViewById(R.id.jad_title);
        final ImageView imageView = adView.findViewById(R.id.jad_image);
        View closeView = adView.findViewById(R.id.jad_close);
        ImageView logoView = adView.findViewById(R.id.jad_logo);
        Bitmap logo = JADNativeWidget.getJDLogo(mContext);
        if (logo != null) {
            logoView.setImageBitmap(logo);
        }
        mShakeAnimationView = JADNativeWidget.getShakeAnimationView(mContext, "点击或摇一摇"); //
        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(ScreenUtils.dip2px(mContext,
                100), ScreenUtils.dip2px(mContext, 100));
        lp1.gravity = Gravity.CENTER;
        mShakeAnimationView.setLayoutParams(lp1);
        FrameLayout adContain = new FrameLayout(mContext);
        adContain.setLayoutParams(new FrameLayout.LayoutParams(ScreenUtils.dip2px(mContext,
                mAdSlot.getAdImageWidth()), ScreenUtils.dip2px(mContext,
                mAdSlot.getAdImageHeight())));
        adContain.addView(adView);
        adContain.addView(mShakeAnimationView);
        titleView.setText(data.getTitle());
        if (data.getImageUrls() != null && !data.getImageUrls().isEmpty()) {
            ImageLoader.loadImage(mContext, data.getImageUrls().get(0), imageView
                    , true);
        }
        List<View> clickList = new ArrayList<>();
        clickList.add(imageView);
        // 摇一摇组件，返回View，大小为
        // (100dp,100dp) 当View attachedToWindow时，动画start,detachedFromWindow时，动画end
        clickList.add(mShakeAnimationView);
        List<View> closeList = new ArrayList<>();
        closeList.add(closeView);
        /*
         * Step4: 注册需要监听的视图，包括整体的广告View、点击视图列表、关闭视图列表
         * 这里非常重要，不要在View的listener中做点击操作，否则影响计费
         */
        mJADNative.registerNativeView(mContext, adContain, clickList, closeList,
                new JADNativeInteractionListener() {

                    @Override
                    public void onExposure() {
                        logI(mContext.getString(R.string.ad_exposure, AD_TAG) + ", slotId:" + mAdSlot.getSlotID());
                    }

                    @Override
                    public void onClick(View view) {
                        logI(mContext.getString(R.string.ad_click, AD_TAG) + ", slotId:" + mAdSlot.getSlotID());
                    }

                    @Override
                    public void onClose(View view) {
                        logI(mContext.getString(R.string.ad_dismiss, AD_TAG) + ", slotId:" + mAdSlot.getSlotID());
                        //Step5:在回调中进行相应点击和关闭的操作
                        if (view != null && view.getId() == R.id.jad_close) {
                            ViewParent parent = adContain.getParent();
                            if (parent instanceof ViewGroup) {
                                ((ViewGroup) parent).removeView(adContain);
                            }
                        }
                    }

                });
        return adContain;
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
    private View getVideoAdView(JADMaterialData data) {
        final ViewGroup adView =
                (ViewGroup) mContext.getLayoutInflater().inflate(R.layout.demo_layout_native_video,
                        null);
        View video = adView.findViewById(R.id.fl_ad);
        ViewGroup.LayoutParams layoutParams = video.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.width = ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dip2px(mContext, 24);
        if (data.getMediaSpecSetType() == JADSlot.MediaSpecSetType.MEDIA_SPEC_SET_TYPE_FEED16_9_SINGLE_VIDEO) {
            layoutParams.height = layoutParams.width * 9 / 16;
        } else if (data.getMediaSpecSetType() == JADSlot.MediaSpecSetType.MEDIA_SPEC_SET_TYPE_FEED9_16_SINGLE_VIDEO) {
            layoutParams.height = layoutParams.width * 16 / 9;
        }
        video.setLayoutParams(layoutParams);
        if (data != null) {
            bindCommonView(data, adView);
            View fl_ad_cover = adView.findViewById(R.id.fl_ad_cover);
            mVideoView = adView.findViewById(R.id.vv_video);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer = mp;
                    setVolumeMuted(isMuted);
                    if (mJADNative != null) {
                        mVideoView.seekTo(mDuration);
                        mDuration = mVideoView.getCurrentPosition();
                        logI("video_log onPrepared "+mDuration);
                        mJADNative.getJADVideoReporter().reportVideoStart(transferCurrentPosition());
                    }

                }
            });
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    fl_ad_cover.setVisibility(View.VISIBLE);
                    if (mJADNative != null) {
                        mDuration = mVideoView.getDuration();
                        logI("video_log onCompletion "+mDuration+", curPos:"+mVideoView.getCurrentPosition());
                        mJADNative.getJADVideoReporter().reportVideoCompleted(transferCurrentPosition());
                    }
                }
            });
            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    fl_ad_cover.setVisibility(View.VISIBLE);
                    if (mJADNative != null) {
                        if (mMediaPlayer != null) {
                            mDuration = mVideoView.getCurrentPosition();
                            logI("video_log onError "+mDuration);
                        }
                        mJADNative.getJADVideoReporter().reportVideoError(transferCurrentPosition(), what, extra);
                    }
                    return false;
                }
            });

            ImageView iv_play = adView.findViewById(R.id.iv_play);
            iv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDuration = 0;
                    mVideoView.setVideoPath(data.getVideoUrl());
                    mVideoView.start();
                    fl_ad_cover.setVisibility(View.GONE);
                    if (mJADNative != null) {
                        logI("video_log onClick "+mDuration);
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
            if (mContext != null && data.getImageUrls() != null && !data.getImageUrls().isEmpty()) {
                ImageLoader.loadImage(mContext, data.getImageUrls().get(0), iv_cover, true);
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
        mJADNative.registerNativeView(mContext, adView, clickList, closeList,
                new JADNativeInteractionListener() {

                    @Override
                    public void onExposure() {
                        logI(mContext.getString(R.string.ad_exposure, AD_TAG) + ", slotId:" + mAdSlot.getSlotID());
                    }

                    @Override
                    public void onClick(View view) {
                        logI(mContext.getString(R.string.ad_click, AD_TAG) + ", slotId:" + mAdSlot.getSlotID());
                    }

                    @Override
                    public void onClose(View view) {
                        logI(mContext.getString(R.string.ad_dismiss, AD_TAG) + ", slotId:" + mAdSlot.getSlotID());
                        //Step5:在回调中进行相应点击和关闭的操作
                        if (view != null && view.getId() == R.id.iv_close) {
                            ViewParent parent = adView.getParent();
                            if (parent != null && parent instanceof ViewGroup) {
                                ((ViewGroup) parent).removeView(adView);
                            }
                        }
                    }

                });
    }


    public void logI(String msg) {
        Logger.i(msg);
    }

    public void onResume() {
        if (mJADNative != null) {
            if (mVideoView != null) {
                mVideoView.start();
                mVideoView.seekTo(mDuration);
                logI("video_log onResume "+mDuration);

            }
            mJADNative.getJADVideoReporter().reportVideoResume(transferCurrentPosition());
            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });

        }
    }

    public void onPause() {
        if (mJADNative != null) {
            if (mVideoView != null) {
                mDuration = mVideoView.getCurrentPosition();
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }
                logI("video_log onPause "+mDuration);
            }
            mJADNative.getJADVideoReporter().reportVideoPause(transferCurrentPosition());
        }
    }

    /**
     * 单位转换为：秒
     *
     * @return
     */
    private float transferCurrentPosition() {
        return mDuration / 1000;
    }

}
