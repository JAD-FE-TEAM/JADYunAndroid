package com.jd.ad.demo.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jd.ad.demo.R;


public class LoadMoreView extends FrameLayout {
    private ProgressBar mProgressBar;
    private TextView mTextView;

    public LoadMoreView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LoadMoreView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.demo_load_more_view, this, true);

        mProgressBar = findViewById(R.id.pb_load_more_progress);
        mTextView = findViewById(R.id.tv_load_more_tip);
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setText(String tip) {
        if (mTextView != null) {
            mTextView.setText(String.valueOf(tip));
        }
    }
}
