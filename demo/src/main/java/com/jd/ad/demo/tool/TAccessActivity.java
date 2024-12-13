package com.jd.ad.demo.tool;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.PermissionUtils;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;

public class TAccessActivity extends BaseActivity {
    private LinearLayout mInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools_global_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initView();
    }

    private void initView() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.tools_access_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mInfoList = findViewById(R.id.item_container);
        LayoutInflater inflater = getLayoutInflater();

        // INTERNET 权限
        if (PermissionUtils.hasPermissions(this, Manifest.permission.INTERNET)) {
            bindText(inflater, "INTERNET：✅");
        } else {
            bindText(inflater, "INTERNET：❎");
        }
        // READ_PHONE_STATE 权限
        if (PermissionUtils.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
            bindText(inflater, "READ_PHONE_STATE：✅");
        } else {
            bindText(inflater, "READ_PHONE_STATE：❎");
        }
        // ACCESS_NETWORK_STATE 权限
        if (PermissionUtils.hasPermissions(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
            bindText(inflater, "ACCESS_NETWORK_STATE：✅");
        } else {
            bindText(inflater, "ACCESS_NETWORK_STATE：❎");
        }
        // WRITE_EXTERNAL_STORAGE 权限
        if (PermissionUtils.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            bindText(inflater, "WRITE_EXTERNAL_STORAGE：✅");
        } else {
            bindText(inflater, "WRITE_EXTERNAL_STORAGE：❎");
        }
        // ACCESS_WIFI_STATE 权限
        if (PermissionUtils.hasPermissions(this, Manifest.permission.ACCESS_WIFI_STATE)) {
            bindText(inflater, "ACCESS_WIFI_STATE：✅");
        } else {
            bindText(inflater, "ACCESS_WIFI_STATE：❎");
        }
        // ACCESS_COARSE_LOCATION 权限
        if (PermissionUtils.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            bindText(inflater, "ACCESS_COARSE_LOCATION：✅");
        } else {
            bindText(inflater, "ACCESS_COARSE_LOCATION：❎");
        }
        // ACCESS_FINE_LOCATION 权限
        if (PermissionUtils.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            bindText(inflater, "ACCESS_FINE_LOCATION：✅");
        } else {
            bindText(inflater, "ACCESS_FINE_LOCATION：❎");
        }
        // QUERY_ALL_PACKAGES 权限
        if (PermissionUtils.hasPermissions(this, Manifest.permission.QUERY_ALL_PACKAGES)) {
            bindText(inflater, "QUERY_ALL_PACKAGES：✅");
        } else {
            bindText(inflater, "QUERY_ALL_PACKAGES：❎");
        }


    }

    private void bindText(LayoutInflater inflater, String text) {
        TextView titleTv = (TextView) inflater.inflate(R.layout.demo_ad_type_list_head_item, null);
        titleTv.setText(text);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.leftMargin = ScreenUtils.dip2px(this, 20);
        mInfoList.addView(titleTv, params);
    }
}
