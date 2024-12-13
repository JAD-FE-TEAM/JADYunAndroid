package com.jd.ad.demo.nativead.feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.nativead.feed.video.NFeedVideoAdActivity;
import com.jd.ad.demo.nativead.feed.video.NFeedVideoAdListActivity;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;

import java.util.ArrayList;
import java.util.Collections;

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

        findViewById(R.id.feed_list_vertical).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 9：16的竖版单图示例
                 * 注意这里要用京媒平台上媒体渲染9：16单图的代码位
                 */
                final ArrayList<String> mAds = new ArrayList<>(Collections.singletonList("2627420285"));
                float width = ScreenUtils.getScreenWidthDip(NFeedManagerActivity.this);
                float height = width / 9 * 16;

                Intent intent = new Intent(NFeedManagerActivity.this, NFeedAdListShowActivity.class);
                intent.putExtra("expressViewWidthDp", width);
                intent.putExtra("expressViewHeightDp", height);
                intent.putStringArrayListExtra("codeIDs", mAds);
                startActivity(intent);
            }
        });

        findViewById(R.id.feed_video_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NFeedManagerActivity.this, NFeedVideoAdActivity.class));

            }
        });
        findViewById(R.id.feed_video_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NFeedManagerActivity.this, NFeedVideoAdListActivity.class));

            }
        });
    }
}
