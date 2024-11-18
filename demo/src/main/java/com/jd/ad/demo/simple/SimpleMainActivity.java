package com.jd.ad.demo.simple;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.tool.ToolsActivity;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;

public class SimpleMainActivity extends BaseActivity {

    private LinearLayout mAdTypeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_simple_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initView();
    }

    private void initView() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.app_name);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdTypeList = findViewById(R.id.item_container);
        LayoutInflater inflater = getLayoutInflater();

        bindItemHead(inflater, "京东渲染广告");
        bindButton(inflater, R.mipmap.ad_splash_ic, "模板-开屏", ESplashCaseActivity.class, true);
        bindButton(inflater, R.mipmap.ad_feed_ic, "模板-信息流", EFeedCaseActivity.class, true);
        bindItemHead(inflater, "媒体渲染广告");
        bindButton(inflater, R.mipmap.ad_splash_ic, "媒体渲染-开屏", NSplashCaseActivity.class, true);
        bindButton(inflater, R.mipmap.ad_feed_ic, "媒体渲染-信息流", NFeedCaseActivity.class, true);
        bindItemHead(inflater, "SDK 自测工具");
        bindButton(inflater, R.mipmap.ad_interstitial_ic, "SDK 自测工具", ToolsActivity.class, true);
    }

    private void bindButton(LayoutInflater inflater, int iconId, String name,
                            final Class<? extends Activity> clz, boolean showDivider) {
        View btn = inflater.inflate(R.layout.demo_ad_type_list_item, null);
        TextView textView = btn.findViewById(R.id.item_name);
        textView.setText(name);
        ImageView icon = btn.findViewById(R.id.left_icon);
        icon.setImageResource(iconId);
        View divider = btn.findViewById(R.id.divider);
        divider.setVisibility(showDivider ? View.VISIBLE : View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SimpleMainActivity.this, clz));
            }
        });
        mAdTypeList.addView(btn);
    }

    private void bindItemHead(LayoutInflater inflater, String title) {
        TextView titleTv = (TextView) inflater.inflate(R.layout.demo_ad_type_list_head_item, null);
        titleTv.setText(title);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.leftMargin = ScreenUtils.dip2px(this, 20);
        mAdTypeList.addView(titleTv, params);
    }
}
