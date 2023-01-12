package com.jd.ad.demo.expressad.feed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;


/**
 * 模板信息流 样例。
 * 更多接入问题可查阅 https://help-sdk-doc.jd.com/ansdkDoc/jie-ru-wen-dang.html
 */
public class EFeedAdActivity extends BaseActivity {

    //默认广告位id
//    private static final String AD_ID = "2528";
    private static final String AD_ID = "884984585";
    /**
     * 开发者提供的信息流广告容器，用于广告渲染成功后，把广告视图添加到此容器
     */
    private ViewGroup mAdContainer;

    /**
     * 设置高度的seekBar
     */
    private SeekBar mSeekWidthBar;

    /**
     * 设置高度的seekBar
     */
    private SeekBar mSeekHeightBar;

    /**
     * 用于显示当前 SeekBar宽度进度情况
     */
    private TextView mSeekWidthBarTv;

    /**
     * 用于显示当前 SeekBar高度进度情况
     */
    private TextView mSeekHeightBarTv;

    /**
     * 广告ID输入控件
     */
    private EditText mPlacementEt; //广告 ID 输入控件

    /**
     * 加载广告按钮
     */
    private Button mAdLoadBtn;

    /**
     * 广告尺寸：宽
     */
    private float mExpressViewWidthDp;

    /**
     * 广告尺寸：高
     */
    private float mExpressViewHeightDp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_feed_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initViews();
    }

    /**
     * 初始化页面视图
     */
    private void initViews() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_feed_single);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPlacementEt = findViewById(R.id.et_code);
        mPlacementEt.setText(AD_ID);

        mAdContainer = findViewById(R.id.ad_container);

        mSeekWidthBar = findViewById(R.id.seek_width_bar);
        mSeekWidthBarTv = findViewById(R.id.seek_width_bar_progress);

        mSeekHeightBar = findViewById(R.id.seek_height_bar);
        mSeekHeightBarTv = findViewById(R.id.seek_height_bar_progress);

        mSeekWidthBar.setProgress(100);
        mSeekWidthBar.setMax(100);

        initScaleView(0); //默认选择 1280 * 720 的广告图片

        resetDes(mSeekWidthBar.getProgress(), mSeekWidthBar, mSeekWidthBarTv, true);
        resetDes(mSeekHeightBar.getProgress(), mSeekHeightBar, mSeekHeightBarTv, false);

        mSeekWidthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resetDes(progress, seekBar, mSeekWidthBarTv, true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekHeightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resetDes(progress, seekBar, mSeekHeightBarTv, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mAdLoadBtn = findViewById(R.id.load_ad_btn);
        mAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "";
                Intent intent = new Intent(EFeedAdActivity.this, EFeedAdShowActivity.class);
                intent.putExtra("expressViewWidthDp", mExpressViewWidthDp);
                intent.putExtra("expressViewHeightDp", mExpressViewHeightDp);
                intent.putExtra("codeID", codeID);
                startActivity(intent);
            }
        });
    }

    /**
     * 刷新页面
     */
    private void resetDes(int progress, SeekBar seekBar, TextView desTv, boolean isWidth) {
        String format = "宽度/总宽度 = %ddp / %ddp = %.2f";
        int totalSize = ScreenUtils.getScreenWidthDip(this);
        if (!isWidth) {
            format = "高度/总高度 = %ddp / %ddp = %.2f";
            totalSize = ScreenUtils.getScreenHeightDip(this);
        }

        float ratio = progress * 1.0f / seekBar.getMax();
        int selectSize = (int) (ratio * totalSize);

        @SuppressLint("DefaultLocale")
        String value = String.format(format, selectSize, totalSize, ratio);
        desTv.setText(value);
        createScale();
    }

    /**
     * 根据比例初始化广告尺寸
     */
    private void createScale() {
        mExpressViewWidthDp = (int) (mSeekWidthBar.getProgress() * 1.0f / mSeekWidthBar.getMax()
                * ScreenUtils.getScreenWidthDip(this));
        mExpressViewHeightDp = (int) (mSeekHeightBar.getProgress() * 1.0f / mSeekHeightBar.getMax()
                * ScreenUtils.getScreenHeightDip(this));
    }

    /**
     * 信息流强制定位宽高区间，利用宽高比计算会产生区间错乱
     */
    private void initScaleView(int choosePic) {
        mSeekWidthBar.setProgress(100);
        int screenWidthDp = ScreenUtils.getScreenWidthDip(this);
        if (choosePic == 1) {
            int validHeightDp = (int) (screenWidthDp * 1.0f / 3.0);
            int initHeightProgress1 = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress1);
        } else {
            int validHeightDp = (int) (screenWidthDp * 1.0f / 1.5);
            int initHeightProgress = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress);
        }
    }
}
