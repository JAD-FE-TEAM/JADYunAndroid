package com.jd.ad.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jd.ad.demo.base.BasePermissionActivity;
import com.jd.ad.demo.expressad.banner.EBannerAdActivity;
import com.jd.ad.demo.expressad.feed.EFeedManagerActivity;
import com.jd.ad.demo.expressad.interstitial.EInterstitialAdActivity;
import com.jd.ad.demo.expressad.splash.ESplashManagerActivity;
import com.jd.ad.demo.nativead.banner.NBannerAdActivity;
import com.jd.ad.demo.nativead.feed.NFeedAdActivity;
import com.jd.ad.demo.nativead.interstitial.NInterstitialAdActivity;
import com.jd.ad.demo.nativead.splash.NSplashAdManagerActivity;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.bl.initsdk.JADYunSdk;

public class DemoMainActivity extends BasePermissionActivity {

    private LinearLayout mAdTypeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_main_activity);
        Toolbar mToolbar = findViewById(R.id.tool_bar);

        setSupportActionBar(mToolbar);

        initView();
    }

    private void initView() {
        mAdTypeList = findViewById(R.id.item_container);
        LayoutInflater inflater = getLayoutInflater();

        bindItemHead(inflater, "模板广告");

        bindButton(inflater, R.mipmap.ad_splash_ic, "开屏", ESplashManagerActivity.class, true);
        bindButton(inflater, R.mipmap.ad_banner_ic, "横幅", EBannerAdActivity.class, true);
        bindButton(inflater, R.mipmap.ad_interstitial_ic, "插屏", EInterstitialAdActivity.class, true);
        bindButton(inflater, R.mipmap.ad_feed_ic, "信息流", EFeedManagerActivity.class, false);


        bindItemHead(inflater, "自渲染广告");

        bindButton(inflater, R.mipmap.ad_splash_ic, "开屏", NSplashAdManagerActivity.class, true);
        bindButton(inflater, R.mipmap.ad_banner_ic, "横幅", NBannerAdActivity.class, true);
        bindButton(inflater, R.mipmap.ad_interstitial_ic, "插屏", NInterstitialAdActivity.class, true);
        bindButton(inflater, R.mipmap.ad_feed_ic, "信息流", NFeedAdActivity.class, false);

        // sdk version
        bindSdkVersion();

        bindItemHead(inflater, "设置初始化和请求广告所在的线程");
        setThreadChooseLayout(inflater);
    }

    private void setThreadChooseLayout(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.demo_thread_choose_layout, null);
        RadioGroup rgInteraction = view.findViewById(R.id.threadInteraction);
        RadioButton main = view.findViewById(R.id.main);
        RadioButton child = view.findViewById(R.id.child);
        if (ThreadChooseUtils.isMainThread(DemoMainActivity.this)) {
            main.setChecked(true);
        } else {
            child.setChecked(true);
        }
        rgInteraction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.child:
                        ThreadChooseUtils.save(DemoMainActivity.this, "child");
                        break;
                    case R.id.main:
                    default:
                        ThreadChooseUtils.save(DemoMainActivity.this, "main");
                        break;
                }
            }
        });
        mAdTypeList.addView(view);
    }

    @SuppressLint("SetTextI18n")
    private void bindSdkVersion() {
        TextView tv = new TextView(this);
        tv.setText("SDK 版本 : " + JADYunSdk.getSDKVersion());
        tv.setPadding(0, ScreenUtils.dip2px(this, 10), 0, ScreenUtils.dip2px(this, 10));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mAdTypeList.addView(tv, params);
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

                startActivity(new Intent(DemoMainActivity.this, clz));
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
