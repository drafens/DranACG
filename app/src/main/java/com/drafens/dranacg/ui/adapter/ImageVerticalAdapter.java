package com.drafens.dranacg.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.drafens.dranacg.R;
import com.drafens.dranacg.tools.ImageManager;

import java.util.ArrayList;
import java.util.List;

public class ImageVerticalAdapter extends RecyclerView.Adapter<ImageVerticalAdapter.ViewHolder> {
    private Context context;
    private List<String> imageList;
    private int shiftSize;

    public ImageVerticalAdapter(Context context, List<String> imageList){
        this.imageList = new ArrayList<>(imageList);
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
        ImageManager.getImage(context, imageList.get(position), holder.image);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        ViewHolder(View view){
            super(view);
            image = view.findViewById(R.id.iv_comic);
        }
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull ViewHolder holder) {
        return false;
    }

    @Override
    public long getItemId(int position) {
        Log.d("TAG", position+" "+super.getItemId(position));
        return super.getItemId(position);
    }

    public void setImageList(List<String> imageList, int shiftSize) {
        this.imageList = imageList;
        this.shiftSize = shiftSize;
        if (shiftSize > 0) {
            notifyItemRangeRemoved(0,shiftSize);
            //notifyItemRangeChanged(0,shiftSize);
        }else if (shiftSize < 0){
            notifyItemRangeInserted(0,shiftSize);
            Log.d("TAG", "setImageList: "+shiftSize);
            //notifyItemRangeChanged(0,imageList.size());
        }
    }
}
