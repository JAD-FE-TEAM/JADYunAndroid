package com.jd.ad.demo.nativead.splash;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.nativead.splash.video.NSplashVideoAdActivity;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;
import com.jd.ad.sdk.dl.model.JADSlot;

/**
 * 本页面主要用于媒体渲染开屏参数配置。
 * 媒体渲染开屏广告具体加载，请参看 {@link NSplashAdActivity}
 */
public class NSplashAdManagerActivity extends BaseActivity {
    private static final String AD_ID = "1213178878";
    private static final String VIDEO_AD_ID = "2642042645";

    private int mRealScreenHeightDp;
    private SeekBar mSeekBarHeight;
    private TextView mSeedBarHeightTv;
    private EditText mPlacementEt;
    private EditText mVideoPlacementEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_n_splash_manager_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 延伸显示区域到刘海
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        ScreenUtils.screenAdapt(this);
        mRealScreenHeightDp = ScreenUtils.getRealScreenHeight(this);

        initViews();
    }

    private void initViews() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.n_splash_title);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPlacementEt = findViewById(R.id.et_code);
        mPlacementEt.setText(AD_ID);

        mVideoPlacementEt = findViewById(R.id.et_video_code);
        mVideoPlacementEt.setText(VIDEO_AD_ID);

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
                String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "1213178878";
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

        Button videoAdLoadBtn = findViewById(R.id.load_video_ad_btn);
        videoAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeID = !TextUtils.isEmpty(mVideoPlacementEt.getText()) ? mVideoPlacementEt.getText().toString() : VIDEO_AD_ID;
                int widthDp = ScreenUtils.getScreenWidthDip(getApplicationContext());
                int heightDp = (int) (mSeekBarHeight.getProgress() * 1.0f / mSeekBarHeight.getMax() * mRealScreenHeightDp);

                JADSlot slot = new JADSlot.Builder()
                        .setSlotID(codeID)
                        .setImageSize(widthDp, heightDp)
                        .setSkipTime(5)
                        .build();

                NSplashVideoAdActivity.startActivity(NSplashAdManagerActivity.this, slot);
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