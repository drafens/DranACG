package com.drafens.dranacg.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;
import java.util.SimpleTimeZone;

public class ImageManager {
    private static String TAG = "ImageManager";
    public static void getIcon(Context context, String url, ImageView imageView){
        if (!url.isEmpty()) {
            Log.d(TAG, "getIcon: "+url);
            Glide.with(context).load(url).into(imageView);
        }
    }

    public static void getImage(Context context, String url, ImageView imageView){
        if (!url.isEmpty()) {
            Glide.with(context).load(url).into(imageView);
        }
    }

    public static void loadImage(final Context context, final List<String> urlList){
    }

    public static void setBackground(Context context, String url, final View view) {
        Glide.with(context).load(url).into(new CustomViewTarget<View,Drawable>(view) {
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                view.setBackground(resource);
            }

            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {
            }
        });
    }
}
