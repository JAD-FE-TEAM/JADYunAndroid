package com.jd.ad.demo.tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.view.HeadBarLayout;

public class ToolsActivity extends BaseActivity {
    private LinearLayout mAdTypeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools_main_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initView();
    }

    private void initView() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.tools_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdTypeList = findViewById(R.id.item_container);
        LayoutInflater inflater = getLayoutInflater();

        bindButton(inflater, R.mipmap.ad_splash_ic, "基础信息", TInfrastructureActivity.class, true);
        bindButton(inflater, R.mipmap.ad_interstitial_ic, "权限配置", TAccessActivity.class, true);
        bindButton(inflater, R.mipmap.ad_feed_ic, "隐私配置", TPrivateActivity.class, true);
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

                startActivity(new Intent(ToolsActivity.this, clz));
            }
        });
        mAdTypeList.addView(btn);
    }

}
