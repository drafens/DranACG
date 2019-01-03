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
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComicImageVertical extends AppCompatActivity{

    private Book book;
    private List<Episode> episodeList;
    private List<String> imageUrlList;
    private List<String> nextList;
    private List<String> currentList;
    private List<String> lastList;
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
                FavouriteManager.update_favourite(book,Book.COMIC);
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
                if(i>=0) {
                    adapterPosition = i;
                    setDetailText();
                }
                if (j>0) {
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
                    currentList = sites != null ? sites.getImage(episodeList.get(episodePosition).getId()) : new ArrayList<String>();
                } catch (MyNetworkException e) {
                    currentList = new ArrayList<>();
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
                        lastList = new ArrayList<>();
                        nextList = new ArrayList<>();
                        imageUrlList = new ArrayList<>(currentList);
                        getTagList(0, imageUrlList.size());
                        adapter = new ImageVerticalAdapter(ComicImageVertical.this, imageUrlList);
                        recyclerView.setAdapter(adapter);
                        setDetailText();
                        getInitNextData();
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
                    final List<String> lastListBak = new ArrayList<>(lastList);
                    try {
                        Sites sites = Sites.getSites(book.getWebsite());
                        if (sites != null) {
                            lastList = new ArrayList<>(currentList);
                            currentList = new ArrayList<>(nextList);
                            if (episodePosition < episodeList.size() - 1) {
                                nextList = sites.getImage(episodeList.get(episodePosition + 1).getId());
                                imageUrlList.addAll(nextList);
                            }else {
                                nextList = new ArrayList<>();
                            }
                            imageUrlList.removeAll(lastListBak);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getTagList(1,nextList.size());
                                    adapter.setImageUrlList(imageUrlList,true, lastListBak.size());
                                }
                            });
                        }
                    } catch (MyNetworkException e) {
                        nextList = new ArrayList<>(currentList);
                        currentList = new ArrayList<>(lastList);
                        lastList = new ArrayList<>(lastListBak);
                    }
                }
            }).start();
        }
        threadPermit = true;
    }

    private void getInitNextData(){
        if (episodePosition < episodeList.size()-1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Sites sites = Sites.getSites(book.getWebsite());
                        if (sites != null) {
                            nextList = sites.getImage(episodeList.get(episodePosition + 1).getId());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageUrlList.addAll(nextList);
                                    getTagList(1, nextList.size());
                                    adapter.setImageUrlList(imageUrlList, true,0);
                                }
                            });
                        }
                    } catch (MyNetworkException e) {
                        e.printStackTrace();
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

    /*private void getNextData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                int i=0;
                nextPage += 1;
                final List<String> newList = new ArrayList<>();
                for (int j=0;j<CONTAINER_THRESHOLD;j++) {
                    tagList.remove(0);
                }
                for (int j=nextPage;j<image_url.size();j++,i++){
                    newList.add(image_url.get(j));
                    tagList.add(episodePosition + "#" + j +"#"+ image_url.size());
                    if (i >= CONTAINER_THRESHOLD-1){
                        nextPage = j;
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    Sites sites = Sites.getSites(book.getWebsite());
                    episodePosition += 1;
                    label:
                    while (true) {
                        try {
                            image_url = sites.getImage(episodeList.get(episodePosition).getId());
                            for (int j = 0; j < image_url.size(); j++, i++) {
                                tagList.add(episodePosition + "#" + j +"#"+ image_url.size());
                                newList.add(image_url.get(j));
                                if (i >= CONTAINER_THRESHOLD - 1) {
                                    nextPage = j;
                                    break label;
                                }
                            }
                            episodePosition += 1;
                        } catch (MyNetworkException e) {
                            e.printStackTrace();
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateList(newList,true,CONTAINER_THRESHOLD);
                        String[] arr = tagList.get(0).split("#");
                        lastChapter = Integer.parseInt(arr[0]);
                        lastPage = Integer.parseInt(arr[1]);
                        threadPermit = true;
                    }
                });
            }
        }).start();
    }

    private void getLastData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                int i = 0;
                lastPage -= 1;
                final List<String> newList = new ArrayList<>();
                for (int j = 0; j < CONTAINER_THRESHOLD; j++) {
                    tagList.remove(tagList.size() - 1);
                }
                for (int j = lastPage; j >= 0; j--, i++) {
                    newList.add(0, image_url.get(j));
                    tagList.add(0,lastChapter + "#" + j +"#"+ image_url.size());
                    if (i >= CONTAINER_THRESHOLD - 1) {
                        lastPage = j;
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    Sites sites = Sites.getSites(book.getWebsite());
                    lastChapter -= 1;
                    label:
                    while (true) {
                        try {
                            image_url = sites.getImage(episodeList.get(lastChapter).getId());
                            for (int j = image_url.size() - 1; j >= 0; j--, i++) {
                                tagList.add(0,lastChapter + "#" + j +"#"+ image_url.size());
                                newList.add(image_url.get(j));
                                if (i >= CONTAINER_THRESHOLD - 1) {
                                    lastPage = j;
                                    break label;
                                }
                            }
                            lastChapter -= 1;
                        } catch (MyNetworkException e) {
                            image_url = new ArrayList<>();
                            MyError.show(ComicImageVertical.this,MyError.MyNetworkException);
                            break;
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateList(newList, false, CONTAINER_THRESHOLD);
                        String[] arr = tagList.get(tagList.size() - 1).split("#");
                        episodePosition = Integer.parseInt(arr[0]);
                        nextPage = Integer.parseInt(arr[1]);
                        manager.scrollToPositionWithOffset(CONTAINER_THRESHOLD,1);
                        if (refreshLayout.isRefreshing()){
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        }).start();
    }*/

    private void getTagList(final int key,int size){
        List<Integer> list;
        for (int i=0;i<size;i++){
            list = Arrays.asList(episodePosition + key,i,size);
            if (key == -1) {
                tagList.add(i, list);
            }else if (key == 1 || key == 0){
                tagList.add(list);
            }
        }
        if (key == 1 || key ==-1) {
            for (int i = 0; i < tagList.size(); i++) {
                if (tagList.get(i).get(0) == (episodePosition - key - key)) {
                    tagList.remove(i);
                    i--;
                }
            }
        }
        Logger.d(tagList);
    }
}
