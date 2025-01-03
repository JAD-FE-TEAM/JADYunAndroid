package com.jd.ad.demo.expressad.feed;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.jd.ad.demo.R;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.TToast;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.feed.JADFeed;
import com.jd.ad.sdk.feed.JADFeedListener;
import com.jd.ad.sdk.logger.Logger;

public class FeedAdWrapper {
    private static final String AD_TAG = "Feed";
    private final Context mContext;
    private final JADSlot mAdSlot;
    private JADFeed mJADFeed;
    private View mAdView;

    public FeedAdWrapper(Context context, JADSlot slot) {
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
        //创建 JADFeed，参数包括广告位参数和回调接口
        mJADFeed = new JADFeed(mContext, mAdSlot);
        //加载 JADFeed
        mJADFeed.loadAd(new JADFeedListener() {

            @Override
            public void onLoadSuccess() {
                showToast(mContext.getString(R.string.ad_load_success, AD_TAG));
                logI(mContext.getString(R.string.ad_load_success, AD_TAG));
                // 获取竞价价格
                if (mJADFeed != null) {
                    int price = mJADFeed.getExtra().getPrice();
                    logI(mContext.getString(R.string.ad_data_price, AD_TAG, price));
                }
            }

            @Override
            public void onLoadFailure(int code, @NonNull String error) {
                showToast(mContext.getString(R.string.ad_load_failed, AD_TAG, code, error));
                logI(mContext.getString(R.string.ad_load_failed, AD_TAG, code, error));
                onAdLoadListener.onLoadFailure(code, error);
            }

            @Override
            public void onRenderSuccess(@NonNull View view) {
                showToast(mContext.getString(R.string.ad_render_success, AD_TAG));
                logI(mContext.getString(R.string.ad_render_success, AD_TAG));
                //Step4: 在render成功之后调用, 将返回广告视图adView添加到自己广告容器adContainer视图中
                mAdView = view;
                onAdLoadListener.onLoadSuccess(FeedAdWrapper.this);
            }

            @Override
            public void onRenderFailure(int code, @NonNull String error) {
                showToast(mContext.getString(R.string.ad_render_failed, AD_TAG, code, error));
                logI(mContext.getString(R.string.ad_render_failed, AD_TAG, code, error));
                onAdLoadListener.onLoadFailure(code, error);
            }

            @Override
            public void onClick() {
                onAdLoadListener.onAdClick(FeedAdWrapper.this);
                showToast(mContext.getString(R.string.ad_click, AD_TAG));
                logI(mContext.getString(R.string.ad_click, AD_TAG));
            }

            @Override
            public void onExposure() {
                showToast(mContext.getString(R.string.ad_exposure, AD_TAG));
                logI(mContext.getString(R.string.ad_exposure, AD_TAG));
            }

            @Override
            public void onClose() {
                showToast(mContext.getString(R.string.ad_dismiss, AD_TAG));
                logI(mContext.getString(R.string.ad_dismiss, AD_TAG));
            }
        });

    }

    public View getAdView() {
        return mAdView;
    }

    public JADFeed getJADFeed() {
        return mJADFeed;
    }

    public void destroy() {
        if (mJADFeed != null) {
            mJADFeed.destroy();
        }
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

    public interface OnAdLoadListener {
        void onLoadSuccess(FeedAdWrapper feedAdWrapper);

        void onLoadFailure(int code, String error);

        void onAdClick(FeedAdWrapper feedAdWrapper);
    }
}
