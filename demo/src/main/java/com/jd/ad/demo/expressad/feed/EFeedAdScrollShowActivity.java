package com.jd.ad.demo.expressad.feed;

import static com.jd.ad.demo.R.color.color_primary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.feed.JADFeed;
import com.jd.ad.sdk.feed.JADFeedListener;

public class EFeedAdScrollShowActivity extends BaseActivity {
    private static final String AD_TAG = "Feed";
    private JADFeed mJADFeed;

    private float mExpressViewWidthDp;
    private float mExpressViewHeightDp;

    private int mChooseOri;

    private String mCodeID;

    private HorizontalScrollView mHorizontalScrollView;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_feed_ad_scroll_show_activity);

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
        mChooseOri = intent.getIntExtra("mChooseOri", 0);
    }

    private void initViews() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_feed_scrollview);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mScrollView = findViewById(R.id.scroll_v);
        mHorizontalScrollView = findViewById(R.id.scroll_h);

        if (mChooseOri == 1) {
            mScrollView.setVisibility(View.GONE);
        } else {
            mHorizontalScrollView.setVisibility(View.GONE);
        }

        TextView tv_0_v = findViewById(R.id.tv_0_v);
        TextView tv_1_v = findViewById(R.id.tv_1_v);
        TextView tv_2_v = findViewById(R.id.tv_2_v);

        tv_0_v.setBackgroundColor(getColorRandom());
        tv_1_v.setBackgroundColor(getColorRandom());
        tv_2_v.setBackgroundColor(getColorRandom());

        TextView tv_0_h = findViewById(R.id.tv_0_h);
        TextView tv_1_h = findViewById(R.id.tv_1_h);
        TextView tv_2_h = findViewById(R.id.tv_2_h);

        tv_0_h.setBackgroundColor(getColorRandom());
        tv_1_h.setBackgroundColor(getColorRandom());
        tv_2_h.setBackgroundColor(getColorRandom());

        ViewGroup adContainer_v = findViewById(R.id.ad_container_v);
        ViewGroup adContainer_h = findViewById(R.id.ad_container_h);

        loadAdAndShow(adContainer_v);
        loadAdAndShow(adContainer_h);

        Button add_template = findViewById(R.id.add_template);
        add_template.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (mChooseOri == 1) {
                    LinearLayout view_all_h = findViewById(R.id.view_all_h);
                    TextView tv = new TextView(EFeedAdScrollShowActivity.this);
                    tv.setText("模板组件");
                    tv.setGravity(Gravity.CENTER);
                    tv.setMaxEms(1);
                    tv.setTextSize(25);
                    tv.setTextColor(EFeedAdScrollShowActivity.this.getResources().getColor(color_primary));
                    tv.setBackgroundColor(getColorRandom());
                    tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    view_all_h.addView(tv);

                    Handler handler = new Handler();  //
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mHorizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
                        }
                    });

                } else {
                    LinearLayout view_all_v = findViewById(R.id.view_all_v);
                    TextView tv = new TextView(EFeedAdScrollShowActivity.this);
                    tv.setText("模板组件");
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(25);
                    tv.setTextColor(EFeedAdScrollShowActivity.this.getResources().getColor(color_primary));
                    tv.setBackgroundColor(getColorRandom());
                    view_all_v.addView(tv);

                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            }
        });

        Button add_ad = findViewById(R.id.add_ad);
        add_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChooseOri == 1) {
                    LinearLayout view_all_h = findViewById(R.id.view_all_h);
                    FrameLayout adContainer = new FrameLayout(EFeedAdScrollShowActivity.this);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    adContainer.setLayoutParams(params);

                    view_all_h.addView(adContainer);
                    loadAdAndShow(adContainer);

                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mHorizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
                        }
                    });
                } else {
                    LinearLayout view_all_v = findViewById(R.id.view_all_v);
                    FrameLayout adContainer = new FrameLayout(EFeedAdScrollShowActivity.this);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    adContainer.setLayoutParams(params);
                    view_all_v.addView(adContainer);
                    loadAdAndShow(adContainer);

                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            }
        });
    }

    private void loadAdAndShow(ViewGroup adContainer) {
        if (ThreadChooseUtils.isMainThread(EFeedAdScrollShowActivity.this)) {
            loadAdAndShowInner(adContainer);
        } else {
            DemoExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    loadAdAndShowInner(adContainer);
                }
            });
        }
    }

    private void loadAdAndShowInner(ViewGroup adContainer) {
        //Step1: 创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink
        /**
         * 注意：
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、单位必须为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(mCodeID) //广告位ID 必须正确 否则无广告返回
                .setSize(mExpressViewWidthDp, mExpressViewHeightDp) //单位必须为dp 必须正确 否则无广告返回
                .setCloseButtonHidden(false) //是否关闭 关闭 按钮
                .build();
        //Step2: 创建 JADFeed，参数包括广告位参数和回调接口
        mJADFeed = new JADFeed(this, slot);
        //Step3: 加载 JADFeed
        mJADFeed.loadAd(new JADFeedListener() {
            @Override
            public void onLoadSuccess() {
                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                // 获取竞价价格
                if (mJADFeed != null) {
                    int price = mJADFeed.getExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }
            }

            @Override
            public void onLoadFailure(int code, @NonNull String error) {
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
                new DemoDialog(EFeedAdScrollShowActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {
                        finish();
                    }
                });
            }

            @Override
            public void onRenderSuccess(@NonNull View adView) {
                showToast(getString(R.string.ad_render_success, AD_TAG));
                logD(getString(R.string.ad_render_success, AD_TAG));
                //Step4: 在render成功之后调用, 将返回广告视图adView添加到自己广告容器adContainer视图中
                if (adView != null && !isFinishing()) {
                    adContainer.removeAllViews();
                    adContainer.addView(adView);
                }

            }

            @Override
            public void onRenderFailure(int code, @NonNull String error) {
                showToast(getString(R.string.ad_render_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_render_failed, AD_TAG, code, error));
            }

            @Override
            public void onExposure() {
                showToast(getString(R.string.ad_exposure, AD_TAG));
                logD(getString(R.string.ad_exposure, AD_TAG));
            }

            @Override
            public void onClick() {
                showToast(getString(R.string.ad_click, AD_TAG));
                logD(getString(R.string.ad_click, AD_TAG));
            }

            @Override
            public void onClose() {
                showToast(getString(R.string.ad_dismiss, AD_TAG));
                logD(getString(R.string.ad_dismiss, AD_TAG));
            }
        });
    }

    private int getColorRandom() {
        int a = Double.valueOf(Math.random() * 255).intValue();
        int r = Double.valueOf(Math.random() * 255).intValue();
        int g = Double.valueOf(Math.random() * 255).intValue();
        int b = Double.valueOf(Math.random() * 255).intValue();
        return Color.argb(a, r, g, b);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Step5: 在页面销毁时close广告，来销毁其中使用到的资源
        if (mJADFeed != null) {
            mJADFeed.destroy();
            mJADFeed = null;
        }
    }
}