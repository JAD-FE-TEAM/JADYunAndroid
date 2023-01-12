package com.jd.ad.demo.nativead.splash;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.sdk.dl.model.JADSlot;

import java.util.ArrayList;
import java.util.List;

/**
 * 本页面主要用于自渲染开屏参数配置。
 * 自渲染开屏广告具体加载，请参看 {@link NSplashAdActivity}
 */
public class NSplashAdManagerActivity extends BaseActivity {

    private static final String AD_ID = "829117613";

    private int mRealScreenHeightDp;
    private SeekBar mSeekBarHeight;
    private TextView mSeedBarHeightTv;
    private EditText mPlacementEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_n_splash_manager_activity);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.n_splash_title);
        }

        mRealScreenHeightDp = ScreenUtils.getRealScreenHeight(this);

        initViews();
    }

    private void initViews() {

        mPlacementEt = findViewById(R.id.et_code);
        mPlacementEt.setText(AD_ID);

        mSeedBarHeightTv = findViewById(R.id.seek_progress_height);

        mSeekBarHeight = findViewById(R.id.seek_bar_height);
        mSeekBarHeight.setProgress(100);
        resetDes(mSeekBarHeight.getProgress(), mSeekBarHeight);
        mSeekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resetDes(progress, seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button adLoadBtn = findViewById(R.id.load_ad_btn);
        adLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "";
                int widthDp = ScreenUtils.getScreenWidthDip(getApplicationContext());
                int heightDp = (int) (mSeekBarHeight.getProgress() * 1.0f / mSeekBarHeight.getMax() * mRealScreenHeightDp);

                JADSlot slot = new JADSlot.Builder()
                        .setSlotID(codeID)
                        .setImageSize(widthDp, heightDp)
                        .setSkipTime(5)
                        .build();

                NSplashAdActivity.startActivity(NSplashAdManagerActivity.this, slot);

            }
        });
    }


    private void resetDes(int progress, SeekBar seekBar) {
        String format = "高度/总高度 = %ddp / %ddp = %.2f";
        float ratio = progress * 1.0f / seekBar.getMax();
        int height = (int) (ratio * mRealScreenHeightDp);

        @SuppressLint("DefaultLocale")
        String value = String.format(format, height, mRealScreenHeightDp, ratio);
        mSeedBarHeightTv.setText(value);
    }
}