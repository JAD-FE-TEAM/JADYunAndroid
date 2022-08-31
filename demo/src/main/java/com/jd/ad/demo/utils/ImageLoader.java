package com.jd.ad.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageLoader {
    public static void loadImage(Context context, String url, ImageView view) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity) {
            if (!ActivityUtils.isActivityAvailable((Activity) context)) {
                return;
            }
        }
        Glide.with(context).load(url).into(view);
    }
}
