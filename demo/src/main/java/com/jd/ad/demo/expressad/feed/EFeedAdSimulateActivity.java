package com.jd.ad.demo.expressad.feed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.dl.model.JADSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EFeedAdSimulateActivity extends BaseActivity {
    private final String mCodeID = "8126";
    private static final String AD_TAG = "Feed";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    private final List<FeedAdWrapper> mDataList = new ArrayList<>();
    private static final int GRID_ITEM_COUNT = 15;
    private MyAdapter mAdapter;
    private final static Map<String, String> BIG_PIC_CONTENT_MAP = new HashMap<>();
    private final static List<String> PIC_CONTENT_LIST = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_feed_simulate_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initViews();
    }

    private void initViews() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_feed_simulate);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BIG_PIC_CONTENT_MAP.put("我们从人造太阳里面获取了巨大的能量，从此保卫宇宙...", "M78星云");
        BIG_PIC_CONTENT_MAP.put("我的名字是工藤新一,原本是一名全国知名的高中生名侦探...", "名侦探柯南");
        BIG_PIC_CONTENT_MAP.put("一座千年古塔，竟然频频发出怪声，几番调查，究竟谁在作祟...", "走进科学");
        BIG_PIC_CONTENT_MAP.put("鸳鸯双栖蝶双飞，满园春色惹人醉，悄悄问圣僧,女儿美不美...", "西游记");

        PIC_CONTENT_LIST.add("价值观：客户为先");
        PIC_CONTENT_LIST.add("价值观：诚信");
        PIC_CONTENT_LIST.add("价值观：协作");
        PIC_CONTENT_LIST.add("价值观：感恩");
        PIC_CONTENT_LIST.add("价值观：拼搏");
        PIC_CONTENT_LIST.add("价值观：担当");

        loadItem();

        RadioGroup mRadioGroupManager = findViewById(R.id.layout_manager);
        mRadioGroupManager.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.grid_simulate:
                        mLayoutManager = new GridLayoutManager(EFeedAdSimulateActivity.this, 2, RecyclerView.VERTICAL, false);
                        break;
                    case R.id.waterfall_simulate:
                        mLayoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
                        break;
                    default:
                        mLayoutManager = new LinearLayoutManager(EFeedAdSimulateActivity.this, RecyclerView.VERTICAL, false);
                        break;
                }
                mDataList.clear();
                loadItem();
            }
        });
    }

    private void loadItem() {
        int widthDp = ScreenUtils.getScreenWidthDip(this);
        int height1 = (int) (widthDp * 1.0f / 1.5);
        int height2 = (int) (widthDp * 1.0f / 3.0);

        for (int i = 0; i < GRID_ITEM_COUNT; i++) {
            mDataList.add(null);
        }

        int random1 = (int) (Math.random() * 15);
        int random2 = (int) (Math.random() * 15);

        createADView(widthDp, height1, random1);
        createADView(widthDp, height2, random2);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(this, mDataList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private static class MyAdapter extends RecyclerView.Adapter {
        private final LayoutInflater layoutInflater;
        private final List<FeedAdWrapper> dataList;

        private static final int ITEM_VIEW_TYPE_BIG_PIC = 0;
        private static final int ITEM_VIEW_TYPE_AD = 1;
        private static final int ITEM_VIEW_TYPE_LEFT_PIC = 2;
        private static final int ITEM_VIEW_TYPE_RIGHT_PIC = 3;

        public MyAdapter(Context context, List<FeedAdWrapper> dataList) {
            this.layoutInflater = LayoutInflater.from(context);
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            switch (viewType) {
                case ITEM_VIEW_TYPE_AD:
                    return new AdViewHolder(layoutInflater.inflate(R.layout.demo_e_feed_list_ad_item, viewGroup, false));
                case ITEM_VIEW_TYPE_LEFT_PIC:
                    return new SmallPicLeftViewHolder(layoutInflater.inflate(R.layout.demo_ad_simulation_smallpic_left_item, viewGroup, false));
                case ITEM_VIEW_TYPE_RIGHT_PIC:
                    return new SmallPicRightViewHolder(layoutInflater.inflate(R.layout.demo_ad_simulation_smallpic_right_item, viewGroup, false));
                default:
                    return new BigPicViewHolder(layoutInflater.inflate(R.layout.demo_ad_simulation_bigpic_item, viewGroup, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pos) {
            String[] keys = BIG_PIC_CONTENT_MAP.keySet().toArray(new String[0]);
            if (viewHolder instanceof AdViewHolder) {
                ((AdViewHolder) viewHolder).adContainer.removeAllViews();
                FeedAdWrapper feedAdWrapper = dataList.get(pos);
                if (feedAdWrapper.getAdView().getParent() instanceof ViewGroup) {
                    ((ViewGroup) feedAdWrapper.getAdView().getParent()).removeView(feedAdWrapper.getAdView());
                }
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                ((AdViewHolder) viewHolder).adContainer.addView(feedAdWrapper.getAdView(), params);
            } else if (viewHolder instanceof SmallPicLeftViewHolder) {
                SmallPicLeftViewHolder smallPicLeftViewHolder = (SmallPicLeftViewHolder) viewHolder;
                int index1 = (int) (Math.random() * PIC_CONTENT_LIST.size());
                smallPicLeftViewHolder.content.setText(PIC_CONTENT_LIST.get(index1));
            } else if (viewHolder instanceof SmallPicRightViewHolder) {
                SmallPicRightViewHolder smallPicRightViewHolder = (SmallPicRightViewHolder) viewHolder;
                int index2 = (int) (Math.random() * PIC_CONTENT_LIST.size());
                smallPicRightViewHolder.content.setText(PIC_CONTENT_LIST.get(index2));
            } else if (viewHolder instanceof BigPicViewHolder) {
                BigPicViewHolder bigPicViewHolder = (BigPicViewHolder) viewHolder;
                String title = keys[(int) (Math.random() * keys.length)];
                bigPicViewHolder.tv_title.setText(title);
                bigPicViewHolder.tv_company.setText(BIG_PIC_CONTENT_MAP.get(title));
            }
        }

        @Override
        public int getItemViewType(int position) {
            FeedAdWrapper feedAdWrapper = dataList.get(position);
            if (feedAdWrapper != null) {
                return ITEM_VIEW_TYPE_AD;
            } else if (position % 3 == 0) {
                return ITEM_VIEW_TYPE_LEFT_PIC;
            } else if (position % 5 == 0) {
                return ITEM_VIEW_TYPE_RIGHT_PIC;
            } else {
                return ITEM_VIEW_TYPE_BIG_PIC;
            }
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
                        if (type == ITEM_VIEW_TYPE_LEFT_PIC || type == ITEM_VIEW_TYPE_RIGHT_PIC || type == ITEM_VIEW_TYPE_AD) {
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
                if (type == ITEM_VIEW_TYPE_LEFT_PIC || type == ITEM_VIEW_TYPE_RIGHT_PIC || type == ITEM_VIEW_TYPE_AD) {
                    StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) params;
                    layoutParams.setFullSpan(true);
                }
            }
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }

    }


    private JADSlot createJADSlot(float expressViewWidthDp, float expressViewHeightDp) {

        //创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink
        /**
         * 注意：
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、单位必须为dp
         */
        return new JADSlot.Builder()
                .setSlotID(mCodeID) //广告位ID 必须正确 否则无广告返回
                .setSize(expressViewWidthDp, expressViewHeightDp) //单位必须为dp
                .setCloseButtonHidden(false)//是否关闭 关闭 按钮
                .build();
    }

    private void createADView(float width, float height, int position) {

        new FeedAdWrapper(EFeedAdSimulateActivity.this, createJADSlot(width, height))
                .loadAd(new FeedAdWrapper.OnAdLoadListener() {
                    @Override
                    public void onLoadSuccess(FeedAdWrapper feedAdWrapper) {
                        mDataList.set(position, feedAdWrapper);
                    }

                    @Override
                    public void onLoadFailure(int code, String error) {
                        logD(getString(R.string.ad_render_failed, AD_TAG, code, error));
                        new DemoDialog(EFeedAdSimulateActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                            @Override
                            public void dismissCallback() {
                                // TODO: 2021/12/6 重新加载
                            }
                        });
                        showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                    }
                });
    }


    private static class AdViewHolder extends RecyclerView.ViewHolder {
        ViewGroup adContainer;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            adContainer = (ViewGroup) itemView;

        }
    }

    private static class BigPicViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_company;

        public BigPicViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_company = itemView.findViewById(R.id.tv_company);
        }
    }

    private static class SmallPicLeftViewHolder extends RecyclerView.ViewHolder {
        TextView content;

        public SmallPicLeftViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
        }
    }

    public static class SmallPicRightViewHolder extends RecyclerView.ViewHolder {
        TextView content;

        public SmallPicRightViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
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
}