package com.drafens.dranacg.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.Episode;
import com.drafens.dranacg.R;
import com.drafens.dranacg.ui.activity.ComicImageHorizon;
import com.drafens.dranacg.ui.activity.MainActivity;

import java.io.Serializable;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder>{
    private List<Episode> episodeList;
    private Book book;
    private Context context;
    private int searchItem;
    private int recentPosition;

    public EpisodeAdapter(Context context,List<Episode> episodeList,Book book,int searchItem, int recentPosition){
        this.episodeList = episodeList;
        this.book = book;
        this.context = context;
        this.searchItem = searchItem;
        this.recentPosition = recentPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.episodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Intent intent;
                switch (searchItem){
                    case Book.COMIC:
                        recentPosition = position;
                        intent = new Intent(context, ComicImageHorizon.class);
                        intent.putExtra("episode",(Serializable) episodeList);
                        intent.putExtra("book",book);
                        intent.putExtra("episode_position", position);
                        break;
                    default:
                        intent = new Intent(context,MainActivity.class);
                        break;
                }
                ((Activity) context).startActivityForResult(intent, 1);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Episode episode = episodeList.get(position);
        String displayString = " "+ (position+1) + "  " + episode.getName();
        String displayString_recent = " *"+ (position+1) + "  " + episode.getName();
        if (recentPosition == position){
            holder.name.setText(displayString_recent);
        }else {
            holder.name.setText(displayString);
        }
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View episodeView;
        TextView name;

        ViewHolder(View view){
            super(view);
            episodeView = view;
            name = view.findViewById(R.id.tv_name);
        }
    }

    public void setRecentPosition(int recentPosition) {
        this.recentPosition = recentPosition;
        notifyDataSetChanged();
    }
}
