package com.jd.ad.demo.nativead.feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.expressad.feed.EFeedAdActivity;
import com.jd.ad.demo.expressad.feed.EFeedAdListActivity;
import com.jd.ad.demo.expressad.feed.EFeedAdMultiProcessActivity;
import com.jd.ad.demo.expressad.feed.EFeedAdRecyclerActivity;
import com.jd.ad.demo.expressad.feed.EFeedAdScrollActivity;
import com.jd.ad.demo.expressad.feed.EFeedAdSimulateActivity;
import com.jd.ad.demo.view.HeadBarLayout;

public class NFeedManagerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_n_feed_manager_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.n_feed_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.feed_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NFeedManagerActivity.this, NFeedAdActivity.class));
            }
        });

        findViewById(R.id.feed_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NFeedManagerActivity.this, NFeedAdListActivity.class));

            }
        });
    }
}
