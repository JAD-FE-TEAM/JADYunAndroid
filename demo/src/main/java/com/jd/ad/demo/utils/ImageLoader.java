package com.jd.ad.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageLoader {
    /**
     * 加载图片
     *
     * @param context        上下文对象
     * @param url            图片地址
     * @param view           加载视图
     * @param needCenterCrop 是否需要居中等比例缩放
     */
    public static void loadImage(Context context, String url, ImageView view,
                                 boolean needCenterCrop) {
        if (context == null) {
            return ;
        }
        if (context instanceof Activity) {
            if (!ActivityUtils.isActivityAvailable((Activity) context)) {
                return ;
            }
        }
        if (needCenterCrop) {
            Glide.with(context).load(url).centerCrop().into(view);
        } else {
            Glide.with(context).load(url).into(view);
        }
    }

}
