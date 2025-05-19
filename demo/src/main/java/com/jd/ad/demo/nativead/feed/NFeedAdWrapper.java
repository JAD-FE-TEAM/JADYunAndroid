package com.jd.ad.demo.nativead.feed;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.utils.ActivityUtils;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ImageLoader;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.TToast;
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

public class NFeedAdWrapper {
    private static final String AD_TAG = "Feed";
    private final Activity mContext;
    private final JADSlot mAdSlot;
    private View mAdView;
    private JADNative mJADNative;
    /**
     * 摇一摇动画组件
     */
    private View mShakeAnimationView;

    public NFeedAdWrapper(Activity context, JADSlot slot) {
        this.mContext = context;
        this.mAdSlot = slot;
    }

    public void loadAd(OnFeedAdListener onAdLoadListener) {
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

    public void loadAdInner(OnFeedAdListener onAdLoadListener) {
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

                if (mJADNative != null
                        && mJADNative.getDataList() != null
                        && !mJADNative.getDataList().isEmpty()
                        && mJADNative.getDataList().get(0) != null) {

                    //Step3:媒体创建媒体渲染视图
                    mAdView = inflateAdView(onAdLoadListener);
                    onAdLoadListener.onLoadSuccess(NFeedAdWrapper.this);
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

    public JADNative getJADNative() {
        return mJADNative;
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

    /**
     * 渲染广告视图
     *
     * @param onFeedAdListener
     * @return 广告视图
     */
    private View inflateAdView(OnFeedAdListener onFeedAdListener) {
        final ViewGroup adView =
                (ViewGroup) mContext.getLayoutInflater().inflate(R.layout.demo_layout_native_feed,
                        null);
        TextView titleView = adView.findViewById(R.id.jad_title);
//        TextView descView = adView.findViewById(R.id.tt_insert_ad_text);
        final ImageView imageView = adView.findViewById(R.id.jad_image);
        View closeView = adView.findViewById(R.id.jad_close);
        ImageView logoView = adView.findViewById(R.id.jad_logo);
        Bitmap logo = JADNativeWidget.getJDLogo(mContext);
        if (logo != null) {
            logoView.setImageBitmap(logo);
        }

        FrameLayout adContain = new FrameLayout(mContext);
        adContain.setLayoutParams(new FrameLayout.LayoutParams(ScreenUtils.dip2px(mContext,
                mAdSlot.getAdImageWidth()), ScreenUtils.dip2px(mContext,
                mAdSlot.getAdImageHeight())));
        adContain.addView(adView);

        if (mAdSlot.getEventInteractionType() == JADSlot.EventInteractionType.EVENT_INTERACTION_TYPE_SHAKE) {
            mShakeAnimationView = JADNativeWidget.getShakeAnimationView(mContext, "点击或摇一摇");
            FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(ScreenUtils.dip2px(mContext,
                    100), ScreenUtils.dip2px(mContext, 100));
            lp1.gravity = Gravity.CENTER;
            mShakeAnimationView.setLayoutParams(lp1);
            adContain.addView(mShakeAnimationView);
        }
        if (mJADNative != null
                && mJADNative.getDataList() != null
                && !mJADNative.getDataList().isEmpty()
                && mJADNative.getDataList().get(0) != null) {
            JADMaterialData data = mJADNative.getDataList().get(0);
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
                                    if (onFeedAdListener != null) {
                                        onFeedAdListener.onClose(NFeedAdWrapper.this);
                                    }

                                }

                            }
                        }

                    });
        }
        return adContain;
    }

    public void logD(String msg) {
        Logger.d(msg);
    }

    public void logI(String msg) {
        Logger.i(msg);
    }

    public void logE(String msg) {
        Logger.e(msg);
    }

    public void showToast(String msg) {
        TToast.show(mContext, msg);
    }

    public interface OnFeedAdListener {
        void onLoadSuccess(NFeedAdWrapper feedAdWrapper);

        void onLoadFailure(int code, String error);

        void onClose(NFeedAdWrapper feedAdWrapper);

        void onClick(NFeedAdWrapper feedAdWrapper);
    }

}
