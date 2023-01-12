package com.jd.ad.demo.expressad.feed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

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
import com.jd.ad.demo.view.HeadBarLayout;

public class EFeedAdRecyclerActivity extends BaseActivity {

//    private static final String AD_ID = "8126";
    private static final String AD_ID = "884984585";

    private SeekBar mSeekWidthBar;  //高度，宽度的 seekBar
    private SeekBar mSeekHeightBar;

    private TextView mSeekWidthBarTv; //用于显示当前 SeekBar 进度情况
    private TextView mSeekHeightBarTv;

    private EditText mPlacementEt;

    private int mChooseOri = RecyclerView.VERTICAL;
    private int mChooseManager;
    private static final int CHOOSE_LINEAR_LAYOUT_MANAGER = 0;
    private static final int CHOOSE_GRID_LAYOUT_MANAGER = 1;
    private static final int CHOOSE_STAGGERED_GRID_LAYOUT_MANAGER = 2;

    private float expressViewWidthDp;
    private float expressViewHeightDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_feed_recycle_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initViews();
        initRadioGroup();
    }

    private void initRadioGroup() {
        RadioGroup radioGroupManager = findViewById(R.id.manager);
        RadioGroup radioGroupOri = findViewById(R.id.orientation);

        radioGroupManager.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.grid:
                        mChooseManager = CHOOSE_GRID_LAYOUT_MANAGER;
                        break;
                    case R.id.staggered:
                        mChooseManager = CHOOSE_STAGGERED_GRID_LAYOUT_MANAGER;
                        break;
                    default:
                        mChooseManager = CHOOSE_LINEAR_LAYOUT_MANAGER;
                        break;
                }
            }
        });

        radioGroupOri.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.horizontal) {
                    mChooseOri = RecyclerView.HORIZONTAL;
                } else {
                    mChooseOri = RecyclerView.VERTICAL;
                }
            }
        });
    }

    private void initViews() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_feed_recyclerview);
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

        Button adLoadBtn = findViewById(R.id.load_ad_btn);
        adLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "";
                Intent intent = new Intent(EFeedAdRecyclerActivity.this, EFeedAdRecyclerShowActivity.class);
                intent.putExtra("expressViewWidthDp", expressViewWidthDp);
                intent.putExtra("expressViewHeightDp", expressViewHeightDp);
                intent.putExtra("codeID", codeID);
                intent.putExtra("mChooseManager", mChooseManager);
                intent.putExtra("mChooseOri", mChooseOri);
                startActivity(intent);
            }
        });
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
        createScale();
    }

    private void createScale() {
        expressViewWidthDp = (int) (mSeekWidthBar.getProgress() * 1.0f / mSeekWidthBar.getMax()
                * ScreenUtils.getScreenWidthDip(this));
        expressViewHeightDp = (int) (mSeekHeightBar.getProgress() * 1.0f / mSeekHeightBar.getMax()
                * ScreenUtils.getScreenHeightDip(this));
    }

    // 信息流强制定位宽高区间，利用宽高比计算会产生区间错乱
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