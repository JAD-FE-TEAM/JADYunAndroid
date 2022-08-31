package com.jd.ad.demo.expressad.feed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.demo.view.LoadMoreListener;
import com.jd.ad.demo.view.LoadMoreRecyclerView;
import com.jd.ad.demo.view.LoadMoreView;
import com.jd.ad.sdk.dl.model.JADSlot;

import java.util.ArrayList;
import java.util.List;

public class EFeedAdRecyclerShowActivity extends BaseActivity {
    private static final String AD_TAG = "Feed";

    private MyAdapter mAdapter;
    private LoadMoreRecyclerView mRecyclerView;
    private String mCodeID = "";
    private final List<FeedAdWrapper> mDataList = new ArrayList<>();
    private static final int LIST_ITEM_COUNT_VERTICAL = 15;
    private static final int LIST_ITEM_COUNT_HORIZONTAL = 4;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mRefreshLayout;

    private float mExpressViewWidthDp;
    private float mExpressViewHeightDp;
    private int mListCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_feed_recycler_show_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initParams();
        initView();
    }

    private void initParams() {
        Intent intent = getIntent();
        mExpressViewWidthDp = intent.getFloatExtra("expressViewWidthDp", 0);
        mExpressViewHeightDp = intent.getFloatExtra("expressViewHeightDp", 0);
        mCodeID = intent.getStringExtra("codeID");
        int mChooseManager = intent.getIntExtra("mChooseManager", 0);
        int mChooseOri = intent.getIntExtra("mChooseOri", 1);
        mListCount = mChooseOri == 0 ? LIST_ITEM_COUNT_HORIZONTAL : LIST_ITEM_COUNT_VERTICAL;
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        switch (mChooseManager) {
            case 1:
                mLayoutManager = new GridLayoutManager(this, 2, mChooseOri, false);
                headBarLayout.setTitle(R.string.e_feed_recyclerview_grid);
                break;
            case 2:
                mLayoutManager = new StaggeredGridLayoutManager(2, mChooseOri);
                headBarLayout.setTitle(R.string.e_feed_recyclerview_staggered);
                break;
            default:
                mLayoutManager = new LinearLayoutManager(this, mChooseOri, false);
                headBarLayout.setTitle(R.string.e_feed_recyclerview_linear);
                break;
        }
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadAdData();
            }
        });
        mRecyclerView.postDelayed(new Runnable() {
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
                mRecyclerView.setLoadingFinish();
                for (int i = 0; i < mListCount; i++) {
                    mDataList.add(null);
                }
                int count = mDataList.size();
                int random = (int) (Math.random() * mListCount) + count - mListCount;
                mDataList.set(random, feedAdWrapper);
                mAdapter.notifyItemChanged(mDataList.size());
            }

            @Override
            public void onLoadFailure(int code, String error) {
                mRecyclerView.setLoadingFinish();
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
                new DemoDialog(EFeedAdRecyclerShowActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {
                        finish();
                    }
                });
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int FOOTER_VIEW_COUNT = 1;
        private static final int ITEM_VIEW_TYPE_LOAD_MORE = -1;
        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_AD = 1;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            switch (viewType) {
                case ITEM_VIEW_TYPE_LOAD_MORE:
                    return new LoadMoreViewHolder(new LoadMoreView(viewGroup.getContext()));
                case ITEM_VIEW_TYPE_AD:
                    return new AdViewHolder(LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.demo_e_feed_list_ad_item, viewGroup, false));
                default:
                    return new NormalViewHolder(LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.demo_e_feed_list_normal_item, viewGroup, false));
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pos) {
            if (viewHolder instanceof AdViewHolder) {
                ((AdViewHolder) viewHolder).adContainer.removeAllViews();
                FeedAdWrapper feedAdWrapper = mDataList.get(pos);
                if (feedAdWrapper.getAdView().getParent() instanceof ViewGroup) {
                    ((ViewGroup) feedAdWrapper.getAdView().getParent()).removeView(feedAdWrapper.getAdView());
                }
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                ((AdViewHolder) viewHolder).adContainer.addView(feedAdWrapper.getAdView(), params);
            } else if (viewHolder instanceof LoadMoreViewHolder) {
                LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) viewHolder;

            } else {
                NormalViewHolder normalViewHolder = (NormalViewHolder) viewHolder;
                normalViewHolder.titleTv.setText("Normal Item " + pos);
            }

            if (viewHolder instanceof LoadMoreViewHolder) {
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
            } else if (viewHolder instanceof AdViewHolder) {
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
            } else {
                viewHolder.itemView.setBackgroundColor(getColorRandom());
            }
        }

        private int getColorRandom() {
            int a = Double.valueOf(Math.random() * 255).intValue();
            int r = Double.valueOf(Math.random() * 255).intValue();
            int g = Double.valueOf(Math.random() * 255).intValue();
            int b = Double.valueOf(Math.random() * 255).intValue();
            return Color.argb(a, r, g, b);
        }

        @Override
        public int getItemViewType(int position) {
            int count = mDataList.size();
            if (position >= count) {
                return ITEM_VIEW_TYPE_LOAD_MORE;
            }
            FeedAdWrapper feedAdWrapper = mDataList.get(position);
            if (feedAdWrapper == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_AD;
            }
        }

        @Override
        public int getItemCount() {
            int count = mDataList == null ? 0 : mDataList.size();
            return count + FOOTER_VIEW_COUNT;
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null && layoutManager instanceof GridLayoutManager) {
                final GridLayoutManager manager = (GridLayoutManager) layoutManager;
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int type = getItemViewType(position);
                        if (type == ITEM_VIEW_TYPE_LOAD_MORE) {
                            return manager.getSpanCount();
                        }
                        return 1;
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                int position = holder.getAdapterPosition();
                int type = getItemViewType(position);
                if (type == ITEM_VIEW_TYPE_LOAD_MORE) {
                    StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) params;
                    layoutParams.setFullSpan(true);
                }
            }
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

    private static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ProgressBar mProgressBar;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));

            mTextView = itemView.findViewById(R.id.tv_load_more_tip);
            mProgressBar = itemView.findViewById(R.id.pb_load_more_progress);
        }
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {
        ViewGroup adContainer;

        public AdViewHolder(View itemView) {
            super(itemView);
            adContainer = (ViewGroup) itemView;
        }
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv;

        public NormalViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title_tv);
        }
    }
}