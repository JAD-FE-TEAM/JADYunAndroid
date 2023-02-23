package com.jd.ad.demo.tool;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.bl.initsdk.JADYunSdk;

public class TPrivateActivity extends BaseActivity {
    private LinearLayout mInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools_ad_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initView();
    }

    private void initView() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.tools_private_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mInfoList = findViewById(R.id.item_container);
        LayoutInflater inflater = getLayoutInflater();

        // isCanUseLocation 隐私
        if (JADYunSdk.getPrivateController().isCanUseLocation()) {
            bindText(inflater, "isCanUseLocation：✅");
        } else {
            bindText(inflater, "isCanUseLocation：❎");
        }
        // getLocation 获取的位置是否可用
        if (JADYunSdk.getPrivateController().getLocation().isValid()) {
            bindText(inflater, "getLocation：✅");
        } else {
            bindText(inflater, "getLocation：❎");
        }
        // isCanUsePhoneState 隐私
        if (JADYunSdk.getPrivateController().isCanUsePhoneState()) {
            bindText(inflater, "isCanUsePhoneState：✅");
        } else {
            bindText(inflater, "isCanUsePhoneState：❎");
        }
        // getImei 获取的信息是否可用
        if (JADYunSdk.getPrivateController().getImei().equals("")) {
            bindText(inflater, "getImei：❎");
        } else {
            bindText(inflater, "getImei：✅");
        }
        // getOaid 获取的信息是否可用
        if (JADYunSdk.getPrivateController().getOaid().equals("")) {
            bindText(inflater, "getOaid：❎");
        } else {
            bindText(inflater, "getOaid：✅");
        }
        // isCanUseIP 隐私
        if (JADYunSdk.getPrivateController().isCanUseIP()) {
            bindText(inflater, "isCanUseIP：✅");
        } else {
            bindText(inflater, "isCanUseIP：❎");
        }
        //
        if (JADYunSdk.getPrivateController().getIP().equals("0.0.0.0")) {
            bindText(inflater, "getIP：❎");
        } else {
            bindText(inflater, "getIP：✅");
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
