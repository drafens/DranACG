package com.drafens.dranacg.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.tools.FavouriteManager;
import com.drafens.dranacg.tools.Tools;
import com.drafens.dranacg.ui.adapter.ImageVerticalAdapter;
import com.drafens.dranacg.Book;
import com.drafens.dranacg.Episode;
import com.drafens.dranacg.error.MyError;
import com.drafens.dranacg.error.MyNetworkException;
import com.drafens.dranacg.R;
import com.drafens.dranacg.Sites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComicImageVertical extends AppCompatActivity{

    private Book book;
    private List<Episode> episodeList;
    private List<String> imageUrlList;
    private List<String> newList;
    private List<List<Integer>> tagList = new ArrayList<>();
    private int episodePosition;
    private int adapterPosition = 0;

    private SwipeRefreshLayout refreshLayout;
    private TextView textDetail;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ImageVerticalAdapter adapter;

    private boolean threadPermit = false;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_image_vertical);

        //状态栏沉浸
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Intent intent = getIntent();
        episodeList = (List<Episode>) intent.getSerializableExtra("episode");
        book = (Book) getIntent().getSerializableExtra("book");
        episodePosition = intent.getIntExtra("episode_position",0);
        initView();
        getInitCurrentData();
    }

    @Override
    protected void onStop() {
        if(FavouriteManager.isFavourite(book.getWebsite(),book.getId(),Book.COMIC)!=-1){
            book.setLastReadChapter(episodeList.get(episodePosition).getName());
            book.setLastReadChapter_id(episodeList.get(episodePosition).getId());
            book.setLastReadTime(Tools.getCurrentTime());
            try {
                FavouriteManager.update_favourite(true,ComicImageVertical.this,book,Book.COMIC);
            } catch (MyFileWriteException e) {
                MyError.show(ComicImageVertical.this,MyError.MyFileWriteException);
            }
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        setResult(episodePosition);
        super.onBackPressed();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        textDetail=findViewById(R.id.tv_detail);
        refreshLayout = findViewById(R.id.refresh_layout);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLastData();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i = manager.findFirstVisibleItemPosition();
                int j = manager.findLastVisibleItemPosition();
                Log.d("TAG", i+"*"+j);
                if(i>=0 && i<tagList.size()) {
                    adapterPosition = i;
                    setDetailText();
                }
                if (j>=0 && j<tagList.size()) {
                    if (tagList.get(j).get(0) == episodePosition+1) {
                        episodePosition += 1;
                        getNextData();
                    }
                }
            }
        });
    }

    private void setDetailText(){
        String string = " "+book.getName()+" "+episodeList.get(tagList.get(adapterPosition).get(0)).getName()+" "+(tagList.get(adapterPosition).get(1)+1) + "/" + (tagList.get(adapterPosition).get(2))+" ";
        textDetail.setText(string);
    }

    private void getInitCurrentData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Sites sites = Sites.getSites(book.getWebsite());
                    imageUrlList = sites != null ? sites.getImage(episodeList.get(episodePosition).getId()) : new ArrayList<String>();
                } catch (MyNetworkException e) {
                    imageUrlList = new ArrayList<>();
                    threadPermit = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyError.show(ComicImageVertical.this, MyError.MyNetworkException);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i=0;i<imageUrlList.size();i++){
                            List<Integer> list = Arrays.asList(episodePosition,i,imageUrlList.size());
                            tagList.add(list);
                        }
                        adapter = new ImageVerticalAdapter(ComicImageVertical.this, imageUrlList);
                        recyclerView.setAdapter(adapter);
                        setDetailText();
                        threadPermit = true;
                        getNextData();
                    }
                });
            }
        }).start();
    }

    private void getNextData(){
        if (threadPermit) {
            Log.d("TAG", "getNextData: ");
            threadPermit = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Sites sites = Sites.getSites(book.getWebsite());
                        if (sites != null) {
                            if (episodePosition < episodeList.size() - 1) {
                                newList = sites.getImage(episodeList.get(episodePosition + 1).getId());
                            }else {
                                newList = new ArrayList<>();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getTagList(true);
                                    adapter.setImageUrlList(newList,true);
                                }
                            });
                        }
                    } catch (MyNetworkException e) {
                        newList = new ArrayList<>();
                    }
                }
            }).start();
        }
        threadPermit = true;
    }

    private void getLastData(){
        Log.d("TAG", "getLastData: ");
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
    }

    private void getTagList(boolean isNext){
        List<Integer> list;
        int episodeNextPosition = tagList.get(tagList.size()-1).get(0);
        int episodeLastPosition = tagList.get(0).get(0);
        if (isNext){
            imageUrlList.addAll(newList);
            for (int i=0;i<newList.size();i++){
                list = Arrays.asList(episodeNextPosition+1,i,newList.size());
                tagList.add(list);
            }
        }else {
            imageUrlList.addAll(0,newList);
            for (int i=0;i<newList.size();i++){
                list = Arrays.asList(episodeLastPosition-1,i,newList.size());
                tagList.add(i,list);
            }
        }
    }
}
