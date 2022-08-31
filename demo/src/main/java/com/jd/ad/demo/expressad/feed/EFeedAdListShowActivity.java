package com.jd.ad.demo.expressad.feed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.demo.view.LoadMoreListView;
import com.jd.ad.demo.view.LoadMoreListener;
import com.jd.ad.sdk.dl.model.JADSlot;

import java.util.ArrayList;
import java.util.List;

public class EFeedAdListShowActivity extends BaseActivity {
    private static final String AD_TAG = "Feed";

    private static final int LIST_ITEM_COUNT = 15;
    private float mExpressViewWidthDp;
    private float mExpressViewHeightDp;
    private MyAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private final List<FeedAdWrapper> mDataList = new ArrayList<>();
    private String mCodeID;
    private LoadMoreListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_feed_list_show_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initParams();
        initViews();
    }

    private void initParams() {
        Intent intent = getIntent();
        mExpressViewWidthDp = intent.getFloatExtra("expressViewWidthDp", 0);
        mExpressViewHeightDp = intent.getFloatExtra("expressViewHeightDp", 0);
        mCodeID = intent.getStringExtra("codeID");
    }

    private void initViews() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_feed_listview);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListView = findViewById(R.id.list_view);
        mAdapter = new MyAdapter(this, mDataList);
        mListView.setAdapter(mAdapter);
        mListView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadAdData();
            }
        });
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadAdData();
            }
        }, 1000);

        mRefreshLayout = findViewById(R.id.layout_swipe_refresh);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_primary));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mDataList == null) {
                    mRefreshLayout.setRefreshing(false);
                    return;
                }
                for (int i = 0; i < mDataList.size(); i++) {
                    if (mDataList.get(i) == null) {
                        continue;
                    }
                    mDataList.get(i).destroy();
                }
                mDataList.clear();
                mAdapter.notifyDataSetChanged();
                loadAdData();
                mRefreshLayout.setRefreshing(false);
            }
        });


    }

    private JADSlot createJADSlot() {

        //创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink
        /**
         * 注意：
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、单位必须为dp
         */
        return new JADSlot.Builder()
                .setSlotID(mCodeID) //广告位ID 必须正确 否则无广告返回
                .setSize(mExpressViewWidthDp, mExpressViewHeightDp) //单位必须为dp
                .setCloseButtonHidden(false)//是否关闭 关闭 按钮
                .build();
    }

    private void loadAdData() {
        new FeedAdWrapper(this, createJADSlot()).loadAd(new FeedAdWrapper.OnAdLoadListener() {
            @Override
            public void onLoadSuccess(FeedAdWrapper feedAdWrapper) {
                mListView.setLoadingFinish();
                for (int i = 0; i < LIST_ITEM_COUNT; i++) {
                    mDataList.add(null);
                }
                int count = mDataList.size();
                int random = (int) (Math.random() * LIST_ITEM_COUNT) + count - LIST_ITEM_COUNT;
                mDataList.set(random, feedAdWrapper);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadFailure(int code, String error) {
                mListView.setLoadingFinish();
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
                new DemoDialog(EFeedAdListShowActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {
                        finish();
                    }
                });
            }

        });
    }

    private static class MyAdapter extends BaseAdapter {

        private final Context mContext;
        private final List<FeedAdWrapper> datalist;
        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_AD = 1;

        public MyAdapter(Context context, List<FeedAdWrapper> datalist) {
            this.mContext = context;
            this.datalist = datalist;
        }

        @Override
        public int getCount() {
            return datalist == null ? 0 : datalist.size();
        }

        @Override
        public FeedAdWrapper getItem(int position) {
            return datalist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            FeedAdWrapper feedAdWrapper = getItem(position);
            if (feedAdWrapper == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_AD;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeedAdWrapper feedAdWrapper = getItem(position);
            if (getItemViewType(position) == ITEM_VIEW_TYPE_AD) {
                return getAdView(convertView, parent, feedAdWrapper);
            } else {
                return getNormalView(convertView, parent, position);
            }
        }

        private View getAdView(View convertView, ViewGroup parent, FeedAdWrapper feedAdWrapper) {
            AdViewHolder adViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.demo_e_feed_list_ad_item, parent, false);
                adViewHolder = new AdViewHolder(convertView);
                convertView.setTag(adViewHolder);
            } else {
                adViewHolder = (AdViewHolder) convertView.getTag();
            }
            (adViewHolder).adContainer.removeAllViews();
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            (adViewHolder).adContainer.addView(feedAdWrapper.getAdView(), params);
            return convertView;
        }

        @SuppressLint("SetTextI18n")
        private View getNormalView(View convertView, ViewGroup parent, int position) {
            NormalViewHolder normalViewHolder;
            if (convertView == null) {
                normalViewHolder = new NormalViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.demo_e_feed_list_normal_item, parent, false);
                normalViewHolder.titleTv = convertView.findViewById(R.id.title_tv);
                convertView.setTag(normalViewHolder);
            } else {
                normalViewHolder = (NormalViewHolder) convertView.getTag();
            }
            normalViewHolder.titleTv.setText("Normal Item " + position);
            normalViewHolder.titleTv.setBackgroundColor(getColorRandom());
            return convertView;
        }

        private int getColorRandom() {
            int a = Double.valueOf(Math.random() * 255).intValue();
            int r = Double.valueOf(Math.random() * 255).intValue();
            int g = Double.valueOf(Math.random() * 255).intValue();
            int b = Double.valueOf(Math.random() * 255).intValue();
            return Color.argb(a, r, g, b);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在页面销毁时close广告，来销毁其中使用到的资源
        if (mDataList == null) {
            return;
        }
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i) == null) {
                continue;
            }
            mDataList.get(i).destroy();
        }
    }

    private static class NormalViewHolder {
        TextView titleTv;
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {
        ViewGroup adContainer;

        public AdViewHolder(View itemView) {
            super(itemView);
            adContainer = (ViewGroup) itemView;
        }
    }
}