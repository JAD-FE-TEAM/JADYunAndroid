package com.jd.ad.demo.expressad.feed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;

/**
 * 模板信息流 在RecyclerView 中展示样例。
 * 更多接入问题可查阅 https://help-sdk-doc.jd.com/ansdkDoc/jie-ru-wen-dang.html
 */
public class EFeedAdListActivity extends BaseActivity {
    private static final String AD_ID = "8126";

    /**
     * 设置宽度的seekBar
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
     * 当前选择的宽高比
     */
    private TextView mSelectWHRatioTv;

    /**
     * 广告ID输入控件
     */
    private EditText mPlacementEt;

    /**
     * 加载广告按钮
     */
    private Button mAdLoadBtn;

    /**
     * 选择广告的比例
     */
    private float mScale = 1.5f;

    /**
     * 广告的宽高比
     */
    private float whRation = 1.5f;

    /**
     * 广告比例选择器
     */
    private CheckBox mCheckbox;

    /**
     * 该比例是否被选择
     */
    private boolean mIsChecked = false;

    /**
     * 比例边界
     */
    private TextView mScaleTv1, mScaleTv2, mScaleTv3, mScaleTv4;

    /**
     * 素材尺寸
     */
    private View mScaleView1, mScaleView2;

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
        setContentView(R.layout.demo_e_feed_list_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initViews();
    }

    private void initViews() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_feed_listview);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mPlacementEt = findViewById(R.id.et_code);
        mPlacementEt.setText(AD_ID);

        mSeekWidthBar = findViewById(R.id.seek_width_bar);
        mSeekWidthBarTv = findViewById(R.id.seek_width_bar_progress);

        mSelectWHRatioTv = findViewById(R.id.width_div_height);

        mSeekHeightBar = findViewById(R.id.seek_height_bar);
        mSeekHeightBarTv = findViewById(R.id.seek_height_bar_progress);

        mScaleView1 = findViewById(R.id.scale1);
        mScaleView2 = findViewById(R.id.scale2);

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

        mScaleView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initScaleView(0);
            }
        });
        mScaleView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initScaleView(1);
            }
        });

        mCheckbox = findViewById(R.id.checkbox);
        mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsChecked = isChecked;
                if (!isChecked) {
                    mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv1.setClickable(false);
                    mScaleTv2.setClickable(false);
                    mScaleTv3.setClickable(false);
                    mScaleTv4.setClickable(false);
                    mSeekHeightBar.setEnabled(true);
                    mSeekWidthBar.setEnabled(true);
                    mScaleView1.setEnabled(true);
                    mScaleView2.setEnabled(true);
                    initScaleView(0);
                } else {
                    mScaleTv1.setClickable(true);
                    mScaleTv2.setClickable(true);
                    mScaleTv3.setClickable(true);
                    mScaleTv4.setClickable(true);
                    mSeekHeightBar.setEnabled(false);
                    mSeekWidthBar.setEnabled(false);
                    mScaleView1.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleView2.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleView1.setEnabled(false);
                    mScaleView2.setEnabled(false);
                    mScale = whRation;
                    createScale();
                }
            }
        });

        mScaleTv1 = findViewById(R.id.tv_scale1);
        mScaleTv2 = findViewById(R.id.tv_scale2);
        mScaleTv3 = findViewById(R.id.tv_scale3);
        mScaleTv4 = findViewById(R.id.tv_scale4);

        mScaleTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 1.2f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
                createScale();
            }
        });
        mScaleTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 1.8f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
                createScale();
            }
        });
        mScaleTv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 2.8f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
                createScale();
            }
        });
        mScaleTv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_clicked);
                mScale = 3.2f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
                createScale();
            }
        });

        mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
        if (mIsChecked) {
            mScaleTv1.setClickable(true);
            mScaleTv2.setClickable(true);
            mScaleTv3.setClickable(true);
            mScaleTv4.setClickable(true);
        } else {
            mScaleTv1.setClickable(false);
            mScaleTv2.setClickable(false);
            mScaleTv3.setClickable(false);
            mScaleTv4.setClickable(false);
        }

        mAdLoadBtn = findViewById(R.id.load_ad_btn);
        mAdLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "";
                Intent intent = new Intent(EFeedAdListActivity.this, EFeedAdListShowActivity.class);
                intent.putExtra("expressViewWidthDp", mExpressViewWidthDp);
                intent.putExtra("expressViewHeightDp", mExpressViewHeightDp);
                intent.putExtra("codeID", codeID);
                startActivity(intent);
            }
        });
    }

    public float[] getProgressWH(float scale) {
        float screenWidthDp = ScreenUtils.getScreenWidthDip(this);

        int proH = (int) (screenWidthDp / scale);
        float proW = proH * scale;
        float[] proWH = new float[]{proW, proH};
        return proWH;
    }


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

        int wProgress = mSeekWidthBar.getProgress();
        int wMax = mSeekWidthBar.getMax();
        float widthDp = wProgress * 1.0f / wMax * ScreenUtils.getScreenWidthDip(this);

        int hProgress = mSeekHeightBar.getProgress();
        if (hProgress == 0) {
            new DemoDialog(EFeedAdListActivity.this, "Error", "0不可以做分母", new DemoDialog.dialogCallback() {
                @Override
                public void dismissCallback() {
                    initScaleView(0);
                }
            });
        }
        int hMax = mSeekHeightBar.getMax();
        float heightDp = hProgress * 1.0f / hMax * ScreenUtils.getScreenHeightDip(this);
        whRation = widthDp / heightDp;

        @SuppressLint("DefaultLocale")
        String whRatio = String.format("选择的宽高比 = %.2f", whRation);
        mSelectWHRatioTv.setText(whRatio);

        // 信息流广告的宽高比(开发者更具自己情况具体设置)，符合宽高比区间： [1.2 - 1.8] 或 [2.8 - 3.2]
        boolean isPic1 = whRation >= 1.2 && whRation <= 1.8;   //对应图片尺寸 1280 * 720
        boolean isPic2 = whRation >= 2.8 && whRation <= 3.2;  //对应图片尺寸 480 * 320

        if (isPic1) {
            mScaleView1.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            mScaleView1.setBackgroundResource(R.drawable.btn_border_normal);
        }

        if (isPic2) {
            mScaleView2.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            mScaleView2.setBackgroundResource(R.drawable.btn_border_normal);
        }

        if (isPic1 || isPic2) {
            mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
        } else {
            mSelectWHRatioTv.setTextColor(Color.RED);
        }

        createScale();
    }

    private void createScale() {
        if (mIsChecked) {
            float[] proWH = getProgressWH(mScale);
            mExpressViewWidthDp = proWH[0];
            mExpressViewHeightDp = proWH[1];
        } else {
            mExpressViewWidthDp = (int) (mSeekWidthBar.getProgress() * 1.0f / mSeekWidthBar.getMax()
                    * ScreenUtils.getScreenWidthDip(this));
            mExpressViewHeightDp = (int) (mSeekHeightBar.getProgress() * 1.0f / mSeekHeightBar.getMax()
                    * ScreenUtils.getScreenHeightDip(this));
        }
    }


    // 信息流强制定位宽高区间，利用宽高比计算会产生区间错乱
    private void initScaleView(int choosePic) {
        mSeekWidthBar.setProgress(100);
        int screenWidthDp = ScreenUtils.getScreenWidthDip(this);
        if (choosePic == 1) {
            int validHeightDp = (int) (screenWidthDp * 1.0f / 3.0);
            int initHeightProgress1 = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress1);
            mScaleView2.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            int validHeightDp = (int) (screenWidthDp * 1.0f / 1.5);
            int initHeightProgress = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress);
            mScaleView1.setBackgroundResource(R.drawable.btn_border_clicked);
            @SuppressLint("DefaultLocale")
            String whRatio = String.format("选择的宽高比 = %.2f", whRation);
            mSelectWHRatioTv.setText(whRatio);
        }
    }
}
