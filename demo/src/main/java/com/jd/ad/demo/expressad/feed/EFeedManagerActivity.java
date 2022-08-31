package com.jd.ad.demo.expressad.feed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.view.HeadBarLayout;

public class EFeedManagerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_feed_manager_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_feed_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.feed_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EFeedManagerActivity.this, EFeedAdActivity.class));
            }
        });

        findViewById(R.id.feed_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EFeedManagerActivity.this, EFeedAdListActivity.class));

            }
        });

        findViewById(R.id.feed_recyclerview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EFeedManagerActivity.this, EFeedAdRecyclerActivity.class));
            }
        });
        findViewById(R.id.feed_scrollview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EFeedManagerActivity.this, EFeedAdScrollActivity.class));
            }
        });
        findViewById(R.id.feed_simulate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EFeedManagerActivity.this, EFeedAdSimulateActivity.class));
            }
        });
    }
}
