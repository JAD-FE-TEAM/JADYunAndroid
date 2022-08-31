package com.jd.ad.demo.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DemoDialog {

    @SuppressLint("SetTextI18n")
    public DemoDialog(Context context, String title, String msg, final dialogCallback dialogCallback) {
        TextView tv_title = new TextView(context);
        tv_title.setText(title);
        tv_title.setPadding(0, 25, 0, 0);
        tv_title.setGravity(Gravity.CENTER);
        tv_title.setTextSize(18);
        tv_title.setTextColor(Color.BLACK);

        TextView message = new TextView(context);
        message.setText(msg);
        message.setPadding(10, 10, 10, 10);
        message.setGravity(Gravity.LEFT);
        message.setTextColor(Color.BLACK);
        message.setTextSize(16);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCustomTitle(tv_title);
        builder.setView(message);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialogCallback.dismissCallback();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button mNeutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mNeutralButton.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mNeutralButton.setTextColor(Color.BLACK);
        mNeutralButton.setLayoutParams(params);
    }

    public interface dialogCallback {
        void dismissCallback();
    }
}
