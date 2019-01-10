package com.drafens.dranacg.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class ImageManager {
    public static void getIcon(String url, ImageView imageView){
        if (!url.isEmpty()) {
            Picasso.get().load(url).into(imageView);
        }
    }

    public static void getImage(String url, ImageView imageView){
        if (!url.isEmpty()) {
            Picasso.get().load(url).into(imageView);
        }
    }

    public static void loadImage(final List<String> urlList){
    }

    public static void setBackground(final Context context, String url, final View view) {
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
                view.setBackground(drawable);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
}
