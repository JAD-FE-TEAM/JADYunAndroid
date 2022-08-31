package com.jd.ad.demo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jd.ad.demo.utils.Logger;
import com.jd.ad.demo.utils.TToast;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.LogD(getClass().getSimpleName() + " : onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.LogD(getClass().getSimpleName() + " : onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.LogD(getClass().getSimpleName() + " : onDestroy");
    }

    public void showToast(String msg) {
        TToast.show(this, msg);
    }

    public void showToastAndLogD(String msg) {
        showToast(msg);
        Logger.LogD(msg);
    }

    public void showToastAndLogE(String msg) {
        showToast(msg);
        Logger.LogE(msg);
    }

    public void logE(String msg) {
        Logger.LogE(msg);
    }

    public void logD(String msg) {
        Logger.LogD(msg);
    }
}
