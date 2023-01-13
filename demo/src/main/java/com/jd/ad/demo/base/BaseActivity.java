package com.jd.ad.demo.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jd.ad.demo.utils.TToast;
import com.jd.ad.sdk.logger.Logger;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(getClass().getSimpleName() + " : onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d(getClass().getSimpleName() + " : onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(getClass().getSimpleName() + " : onDestroy");
    }

    public void showToast(String msg) {
        TToast.show(this, msg);
    }

    public void showToastAndLogD(String msg) {
        showToast(msg);
        Logger.d(msg);
    }

    public void showToastAndLogE(String msg) {
        showToast(msg);
        Logger.e(msg);
    }

    public void logE(String msg) {
        Logger.e(msg);
    }

    public void logD(String msg) {
        Logger.d(msg);
    }
}
