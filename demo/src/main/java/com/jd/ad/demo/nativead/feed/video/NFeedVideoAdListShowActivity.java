package com.jd.ad.demo.nativead.feed.video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class NFeedVideoAdListShowActivity extends BaseActivity {

    private static final int LIST_ITEM_COUNT_VERTICAL = 15;
    private final List<NFeedVideoAdWrapper> mDataList = new ArrayList<>();
    private MyAdapter mAdapter;
    private LoadMoreRecyclerView mRecyclerView;
    private List<String> mCodeIDs = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mRefreshLayout;

    private float mExpressViewWidthDp;
    private float mExpressViewHeightDp;
    private final int mListCount = LIST_ITEM_COUNT_VERTICAL;
    private int mIndex = 0;

    private int currentPosition = 0; // 用于保存视频播放位置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_feed_recycler_show_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initParams();
        initView();
    }

    // 在活动生命周期中处理暂停和继续播放
    @Override
    protected void onPause() {
        super.onPause();

        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
        if (firstVisibleItemPosition >= 0 && lastVisibleItemPosition >= 0) {
            for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                View itemView = mLayoutManager.findViewByPosition(i);
                if (itemView != null) {
                    VideoView videoView = itemView.findViewById(R.id.vv_video);
                    if (videoView == null) {
                        continue;
                    }
                    Rect rect = new Rect();
                    videoView.getLocalVisibleRect(rect);
                    // 判断VideoView是否在屏幕上可见
                    if (rect.top >= 0 && rect.bottom <= mRecyclerView.getHeight()) {
                        // 这个VideoView在屏幕上可见
                        if (videoView.isPlaying()) {
                            currentPosition = videoView.getCurrentPosition();
                            videoView.pause();
                            return;
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
        if (firstVisibleItemPosition >= 0 && lastVisibleItemPosition >= 0) {
            for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                View itemView = mLayoutManager.findViewByPosition(i);
                if (itemView != null) {
                    VideoView videoView = itemView.findViewById(R.id.vv_video);
                    if (videoView == null) {
                        continue;
                    }
                    Rect rect = new Rect();
                    videoView.getLocalVisibleRect(rect);
                    // 判断VideoView是否在屏幕上可见
                    if (rect.top >= 0 && rect.bottom <= mRecyclerView.getHeight()) {
                        // 这个VideoView在屏幕上可见
                        if (!videoView.isPlaying()) {
                            if (currentPosition > 0) {
                                currentPosition = 0;
                                videoView.start();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void initParams() {
        Intent intent = getIntent();
        mExpressViewWidthDp = intent.getFloatExtra("expressViewWidthDp", 0);
        mExpressViewHeightDp = intent.getFloatExtra("expressViewHeightDp", 0);
        mCodeIDs = intent.getStringArrayListExtra("codeIDs");
        mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
    }

    private void initView() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.n_feed_listview);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        mRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
                if (holder instanceof AdViewHolder) {
                    VideoView videoView = ((AdViewHolder) holder).adContainer.findViewById(R.id.vv_video);
                    View videoCover = ((AdViewHolder) holder).adContainer.findViewById(R.id.fl_ad_cover);
                    if (videoView == null || videoCover == null) {
                        return;
                    }
                    videoView.pause();
                    int time = videoView.getCurrentPosition();
                    ((AdViewHolder) holder).time = time;
                    videoCover.setVisibility(View.VISIBLE);
                }
            }
        });

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
        if (mIndex > 2) {
            mIndex = 0;
        }
        String codeID = mCodeIDs.get(mIndex);
        mIndex++;

        //创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink
        /**
         * 注意：
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、单位必须为dp
         */
        return new JADSlot.Builder()
                .setSlotID(codeID)
                .setImageSize(mExpressViewWidthDp, mExpressViewHeightDp)
                .setAdType(JADSlot.AdType.FEED)
                .build();
    }

    private void loadAdData() {
        new NFeedVideoAdWrapper(this, createJADSlot()).loadAd(new NFeedVideoAdWrapper.OnAdLoadListener() {
            @Override
            public void onLoadSuccess(NFeedVideoAdWrapper feedAdWrapper) {
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
                new DemoDialog(NFeedVideoAdListShowActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {
                        finish();
                    }
                });
            }
        });
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
        public ViewGroup adContainer;
        public int time;

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
                NFeedVideoAdWrapper feedAdWrapper = mDataList.get(pos);
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
            NFeedVideoAdWrapper feedAdWrapper = mDataList.get(position);
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
}