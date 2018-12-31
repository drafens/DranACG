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
import com.drafens.dranacg.error.ErrorActivity;
import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.error.MyNetworkException;
import com.drafens.dranacg.tools.FavouriteManager;
import com.drafens.dranacg.ui.adapter.ImageHorizonAdapter;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComicImageHorizon extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private Book book;
    private List<Episode> episodeList;
    private List<String> imageUrlList;
    private int episodePosition;
    private int pagePosition;

    private ViewPager viewPager;
    private TextView textDetail;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Sites sites = Sites.getSites(book.getWebsite());
                    imageUrlList = sites != null ? sites.getImage(episodeList.get(episodePosition).getId()) : new ArrayList<String>();
                } catch (MyNetworkException e) {
                    imageUrlList = new ArrayList<>();
                    ErrorActivity.startActivity(ComicImageHorizon.this,ErrorActivity.MyNetworkException);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageHorizonAdapter adapter = new ImageHorizonAdapter(ComicImageHorizon.this,imageUrlList);
                        viewPager.setAdapter(adapter);
                        setDetailText();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(FavouriteManager.isFavourite(book.getWebsite(),book.getId(),Book.COMIC)!=-1){
            book.setLastReadChapter(episodeList.get(episodePosition).getName());
            book.setLastReadChapter_id(episodeList.get(episodePosition).getId());
            //获取当前时间
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour_int = calendar.get(Calendar.HOUR_OF_DAY);
            String hour;
            if (hour_int<10) hour = "0"+hour_int;
            else hour = ""+hour_int;
            int minute = calendar.get(Calendar.MINUTE);
            book.setLastReadTime("阅读于："+year+"-"+month+"-"+day+" "+hour+":"+minute);
            try {
                FavouriteManager.update_favourite(book,Book.COMIC);
            } catch (MyFileWriteException e) {
                ErrorActivity.startActivity(ComicImageHorizon.this,ErrorActivity.MyFileWriteException);
            }
        }
    }

    private void initView() {
        viewPager = findViewById(R.id.view_pager);
        textDetail = findViewById(R.id.tv_detail);
        viewPager.addOnPageChangeListener(this);
    }

    private void setDetailText(){
        String string = " "+book.getName()+" "+episodeList.get(episodePosition).getName()+" "+(pagePosition+1) + "/" + (imageUrlList.size())+" ";
        textDetail.setText(string);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        pagePosition=i;
        setDetailText();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}