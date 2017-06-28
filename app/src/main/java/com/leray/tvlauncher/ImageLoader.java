package com.leray.tvlauncher;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by leray on 2017/4/4.
 */

public class ImageLoader {
    private static final String TAG = "ImageLoader";

    public static void loadImage(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url) || imageView == null) {
            return;
        }
//        url = "file:///android_asset/images/d1.jpg";
        Glide.with(imageView.getContext())
                .load(url)
                .asBitmap()
//                .load(R.drawable.ic_launcher)
                .override(480, 480)
                .into(imageView);
    }

    public static void loadImageCrossFade(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url) || imageView == null) {
            return;
        }
        Glide.with(imageView.getContext())
                .load(url)
                .override(500, 500)
                .crossFade()
                .into(imageView);
    }

    public static void loadImage(int resId, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        Glide.with(imageView.getContext())
                .load(resId)
                .into(imageView);
    }

    public static void loadImage(Drawable drawable, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        imageView.setImageDrawable(drawable);
//        Glide.with(imageView.getContext())
//                .load(drawable)
//                .into(imageView);
    }
}
