package com.jd.ad.demo.nativead.splash;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

    private static final String AD_ID = "2525";

    private int mRealScreenHeightDp;
    private SeekBar mSeekBarHeight;
    private TextView mSeedBarHeightTv;
    private EditText mPlacementEt;
    private int mTypeIndex = 0;
    private int mInteraction;

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
        initClickTypeView();
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

        RadioGroup rgInteraction = findViewById(R.id.interaction);
        rgInteraction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.shake:
                        mInteraction = JADSlot.InteractionType.SHAKE;
                        break;
                    case R.id.swipe:
                        mInteraction = JADSlot.InteractionType.SWIPE;
                        break;
                    default:
                        mInteraction = JADSlot.InteractionType.NORMAL;
                        break;
                }
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

                NSplashAdActivity.startActivity(NSplashAdManagerActivity.this, slot, mTypeIndex,
                        mInteraction);

            }
        });
    }

    private void initClickTypeView() {
        View view1 = findViewById(R.id.type1);
        View view2 = findViewById(R.id.type2);
        final List<View> list = new ArrayList<>(2);
        list.add(view1);
        list.add(view2);
        for (int i = 0; i < list.size(); i++) {
            View view = list.get(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < list.size(); j++) {
                        View tmp = list.get(j);
                        if (tmp.getId() == v.getId()) {
                            mTypeIndex = j;
                            tmp.setBackgroundResource(R.drawable.btn_border_clicked);
                        } else {
                            tmp.setBackgroundResource(R.drawable.btn_border_normal);
                        }
                    }
                }
            });
        }

        view1.setBackgroundResource(R.drawable.btn_border_clicked);
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