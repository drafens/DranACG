package com.drafens.dranacg.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import java.util.List;

public class ComicImageHorizon extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private String TAG = "ComicImageHorizon";

    private Book book;
    private List<Episode> episodeList;
    private List<String> imageUrlList;
    private List<String> lastList;
    private List<String> currentList;
    private List<String> nextList;
    private int episodePosition;
    private int pagePosition;

    private ViewPager viewPager;
    private TextView textDetail;
    private ImageHorizonAdapter adapter;

    private boolean threadPermmit = false;

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

    private void initView() {
        viewPager = findViewById(R.id.view_pager);
        textDetail = findViewById(R.id.tv_detail);
        viewPager.addOnPageChangeListener(this);
    }

    private void setDetailText(){
        String string = " "+book.getName()+" "+episodeList.get(episodePosition).getName()+" "+(pagePosition+1) + "/" + (currentList.size())+" ";
        textDetail.setText(string);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i>=lastList.size()+currentList.size()){
            episodePosition += 1;
            pagePosition=(i-lastList.size()-currentList.size());
            getNextData();
            threadPermmit = false;
        }else if (i>=lastList.size()){
            pagePosition=i-lastList.size();
        }else {
            pagePosition=i;
            episodePosition -= 1;
            getLastData();
            threadPermmit = false;
        }
        setDetailText();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

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

    private void getInitCurrentData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Sites sites = Sites.getSites(book.getWebsite());
                    currentList = sites != null ? sites.getImage(episodeList.get(episodePosition).getId()) : new ArrayList<String>();
                } catch (MyNetworkException e) {
                    currentList = new ArrayList<>();
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
                        viewPager.setAdapter(adapter);
                        setDetailText();
                        getInitNextData();
                    }
                });
            }
        }).start();
    }

    private void getLastData(){
        Log.d(TAG, "getLastData: ");
        if(episodePosition>0 && threadPermmit) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<String> nextListBak = new ArrayList<>(nextList);
                    try {
                        Sites sites = Sites.getSites(book.getWebsite());
                        if (sites != null) {
                            nextList = new ArrayList<>(currentList);
                            currentList = new ArrayList<>(lastList);
                            lastList = sites.getImage(episodeList.get(episodePosition - 1).getId());
                            imageUrlList.addAll(0, lastList);
                            imageUrlList.removeAll(nextList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setImageList(imageUrlList, lastList.size());
                                    threadPermmit = true;
                                }
                            });
                        }
                    } catch (MyNetworkException e) {
                        lastList = new ArrayList<>(currentList);
                        currentList = new ArrayList<>(nextList);
                        nextList = new ArrayList<>(nextListBak);
                        threadPermmit = true;
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void getNextData() {
        if (episodePosition < episodeList.size() - 1 && threadPermmit) {
            Log.d(TAG, "getNextData: ");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<String> lastListBak = new ArrayList<>(lastList);
                    try {
                        Sites sites = Sites.getSites(book.getWebsite());
                        if (sites != null) {
                            lastList = new ArrayList<>(currentList);
                            currentList = new ArrayList<>(nextList);
                            nextList = sites.getImage(episodeList.get(episodePosition + 1).getId());
                            imageUrlList.addAll(nextList);
                            imageUrlList.removeAll(lastList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setImageList(imageUrlList, -lastList.size());
                                    threadPermmit = true;
                                }
                            });
                        }
                    } catch (MyNetworkException e) {
                        nextList = new ArrayList<>(currentList);
                        currentList = new ArrayList<>(lastList);
                        lastList = new ArrayList<>(lastListBak);
                        threadPermmit = true;
                    }
                }
            }).start();
        }
    }

    private void getInitLastData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Sites sites = Sites.getSites(book.getWebsite());
                    if (sites != null) {
                        if (episodePosition > 0) {
                            lastList = sites.getImage(episodeList.get(episodePosition - 1).getId());
                            imageUrlList.addAll(0, lastList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setImageList(imageUrlList,lastList.size());
                                    threadPermmit = true;
                                }
                            });
                        }
                    }
                } catch (MyNetworkException e) {
                    e.printStackTrace();
                    threadPermmit = true;
                }
            }
        }).start();
    }

    private void getInitNextData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Sites sites = Sites.getSites(book.getWebsite());
                    if (sites != null) {
                        if (episodePosition < episodeList.size()-1) {
                            nextList = sites.getImage(episodeList.get(episodePosition + 1).getId());
                            imageUrlList.addAll(nextList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setImageList(imageUrlList,0);
                                    getInitLastData();
                                }
                            });
                        }
                    }
                } catch (MyNetworkException e) {
                    e.printStackTrace();
                    threadPermmit = true;
                }
            }
        }).start();
    }
}
