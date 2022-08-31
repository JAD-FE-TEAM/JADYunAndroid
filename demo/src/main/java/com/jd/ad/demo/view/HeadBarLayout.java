package com.jd.ad.demo.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.utils.ScreenUtils;

public class HeadBarLayout extends FrameLayout {
    public HeadBarLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HeadBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeadBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private TextView titleTv;
    private View backBtn;

    private void init(Context context) {
        View child = LayoutInflater.from(context).inflate(R.layout.demo_head_bar_layout, null);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ScreenUtils.dip2px(56));
        addView(child, params);
        setPadding(0, getStatusBarHeight(context), 0, 0);
        setBackgroundResource(R.color.color_primary);

        backBtn = findViewById(R.id.head_back_btn);
        titleTv = findViewById(R.id.head_title);
    }

    public void setTitle(int title) {
        titleTv.setText(title);
    }

    public void setOnBackClickListener(OnClickListener listener) {
        backBtn.setOnClickListener(listener);
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
