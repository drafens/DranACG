package com.drafens.dranacg.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drafens.dranacg.R;
import com.drafens.dranacg.tools.ImageManager;

import java.util.List;

public class ImageHorizonAdapter extends PagerAdapter {
    private Context context;
    private List<String> imageList;
    private SparseArray<View> cacheView;

    public ImageHorizonAdapter(Context context, List<String> imageList) {
        this.imageList = imageList;
        this.context = context;
        cacheView = new SparseArray<>(imageList.size());
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = cacheView.get(position);
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_image_horizon,container,false);
            view.setTag(position);
            ImageView imageView = view.findViewById(R.id.iv_comic);
            ImageManager.getImage(context,imageList.get(position),imageView);
            cacheView.put(position,view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
