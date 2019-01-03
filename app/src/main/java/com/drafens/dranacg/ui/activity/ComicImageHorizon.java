package com.drafens.dranacg.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.Episode;
import com.drafens.dranacg.R;
import com.drafens.dranacg.Sites;
import com.drafens.dranacg.error.MyError;
import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.error.MyNetworkException;
import com.drafens.dranacg.tools.FavouriteManager;
import com.drafens.dranacg.tools.Tools;
import com.drafens.dranacg.ui.adapter.ImageHorizonAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComicImageHorizon extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private Book book;
    private List<Episode> episodeList;
    private List<String> imageUrlList;

    private List<String> lastList;
    private List<String> currentList;
    private List<String> nextList;
    private List<List<Integer>> tagList = new ArrayList<>();
    private int episodePosition;
    private int adapterPosition;

    private ViewPager viewPager;
    private TextView textDetail;
    private ImageHorizonAdapter adapter;

    private boolean threadPermit = false;


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_image_horizon);

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
                MyError.show(ComicImageHorizon.this,MyError.MyFileWriteException);
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
        viewPager = findViewById(R.id.view_pager);
        textDetail = findViewById(R.id.tv_detail);
        viewPager.addOnPageChangeListener(this);
    }

    private void setDetailText(){
        String string = " "+book.getName()+" "+episodeList.get(tagList.get(adapterPosition).get(0)).getName()+" "+(tagList.get(adapterPosition).get(1)+1) + "/" + (tagList.get(adapterPosition).get(2))+" ";
        textDetail.setText(string);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        Log.d("TAG", "onPageSelected: "+i);
        adapterPosition = i;
        if (tagList.get(adapterPosition).get(0) == episodePosition+1) {
            episodePosition += 1;
            getNextData();
        } else if (tagList.get(adapterPosition).get(0) == episodePosition-1) {
            episodePosition -= 1;
            getLastData();
        }
        setDetailText();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private void getInitCurrentData(){
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
                            MyError.show(ComicImageHorizon.this,MyError.MyNetworkException);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageUrlList = new ArrayList<>(currentList);
                        lastList = new ArrayList<>();
                        nextList = new ArrayList<>();
                        adapter = new ImageHorizonAdapter(ComicImageHorizon.this,imageUrlList);
                        getTagList(0,currentList.size());
                        viewPager.setAdapter(adapter);
                        setDetailText();
                        getInitNextData();
                    }
                });
            }
        }).start();
    }

    private void getLastData(){
        if(threadPermit) {
            Log.d("TAG", "getLastData: ");
            threadPermit = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<String> nextListBak = new ArrayList<>(nextList);
                    try {
                        Sites sites = Sites.getSites(book.getWebsite());
                        if (sites != null) {
                            nextList = new ArrayList<>(currentList);
                            currentList = new ArrayList<>(lastList);
                            if(episodePosition>0) {
                                lastList = sites.getImage(episodeList.get(episodePosition - 1).getId());
                                imageUrlList.addAll(0, lastList);
                            }else {
                                lastList = new ArrayList<>();
                            }
                            imageUrlList.removeAll(nextListBak);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getTagList(-1, lastList.size());
                                    Log.d("TAG", "overL");
                                    adapter.setImageList(imageUrlList, lastList.size());
                                }
                            });
                        }
                    } catch (MyNetworkException e) {
                        lastList = new ArrayList<>(currentList);
                        currentList = new ArrayList<>(nextList);
                        nextList = new ArrayList<>(nextListBak);
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        threadPermit = true;
    }

    private void getNextData() {
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
                                    Log.d("TAG", "overN");
                                    adapter.setImageList(imageUrlList, -lastListBak.size());
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

    private void getInitLastData() {
        if (episodePosition > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Sites sites = Sites.getSites(book.getWebsite());
                        if (sites != null) {
                            lastList = sites.getImage(episodeList.get(episodePosition - 1).getId());
                            imageUrlList.addAll(0, lastList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getTagList(-1, lastList.size());
                                    adapter.setImageList(imageUrlList, lastList.size());
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

    private void getInitNextData(){
        if (episodePosition < episodeList.size()-1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Sites sites = Sites.getSites(book.getWebsite());
                        if (sites != null) {
                            nextList = sites.getImage(episodeList.get(episodePosition + 1).getId());
                            imageUrlList.addAll(nextList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getTagList(1, nextList.size());
                                    adapter.setImageList(imageUrlList, 0);
                                    getInitLastData();
                                }
                            });
                        }
                    } catch (MyNetworkException e) {
                        e.printStackTrace();
                        threadPermit = true;
                    }
                }
            }).start();
        }
    }

    //key接收 -1 Last;0 Current +1 Next
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
    }
}