package com.drafens.dranacg.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.drafens.dranacg.tools.ImageManager;
import com.drafens.dranacg.R;

import java.util.List;
import java.util.logging.Logger;

public class ImageVerticalAdapter extends RecyclerView.Adapter<ImageVerticalAdapter.ViewHolder> {
    private Context context;
    private List<String> imageUrlList;

    public ImageVerticalAdapter(Context context, List<String> imageUrlList){
        this.imageUrlList = imageUrlList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ImageManager.getImage(context, imageUrlList.get(position), holder.image);
    }

    @Override
    public int getItemCount() {
        return imageUrlList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        ViewHolder(View view){
            super(view);
            image = view.findViewById(R.id.iv_comic);
        }
    }

    public void setImageUrlList(List<String> newList,boolean isNext) {
        if (isNext) {
            this.imageUrlList.addAll(newList);
        }else {
            this.imageUrlList.addAll(0,newList);
        }
    }
}
