package com.drafens.dranacg;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import java.util.List;

public class ImageManager {
    private static String TAG = "ImageManager";
    static void getIcon(Context context, String url, ImageView imageView){
        if (!url.isEmpty()) {
            url = url.replace("http://","https://");
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
}
