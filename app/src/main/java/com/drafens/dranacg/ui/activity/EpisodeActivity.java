package com.drafens.dranacg.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.Episode;
import com.drafens.dranacg.R;
import com.drafens.dranacg.Sites;
import com.drafens.dranacg.error.ErrorActivity;
import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.error.MyJsonFormatException;
import com.drafens.dranacg.error.MyJsoupResolveException;
import com.drafens.dranacg.tools.FavouriteManager;
import com.drafens.dranacg.tools.ImageManager;
import com.drafens.dranacg.ui.adapter.EpisodeAdapter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EpisodeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EpisodeActivity";
    private RecyclerView recyclerView;
    private TextView textNonEpisode;
    private FloatingActionButton fabFavourite;
    private  FloatingActionButton fabLastRead;

    private Book book;
    private List<Episode> episodeList;
    private int recentEpisodePosition;

    private int isFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
        book = (Book) getIntent().getSerializableExtra("book");
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        textNonEpisode = findViewById(R.id.text_non_episode);
        fabFavourite = findViewById(R.id.fab_favourite);
        fabLastRead = findViewById(R.id.fab_last_read);

        toolbar.setTitle(book.getName());
        ImageManager.setBackground(EpisodeActivity.this,book.getIcon(), appBarLayout);
        setSupportActionBar(toolbar);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);
        setEpisodeList();
        setFab();
    }

    private void setFab() {
        isFavourite = FavouriteManager.isFavourite(book.getWebsite(),book.getId(),Book.COMIC);
        if (isFavourite!=-1) {
            fabFavourite.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
        } else {
            fabFavourite.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
        }
        fabFavourite.setOnClickListener(this);
        fabLastRead.setOnClickListener(this);
    }

    private void setEpisodeList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Sites sites = Sites.getSites(book.getWebsite());
                try {
                    if (sites != null){
                        episodeList = sites.getEpisode(book.getId());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (episodeList.size()>0) { //判断是否有章节
                                    textNonEpisode.setVisibility(View.GONE);
                                    if (isFavourite != -1) { //判断是否是搜藏
                                        try {
                                            recentEpisodePosition = FavouriteManager.getEpisodePosition(book.getLastReadChapter_id(), episodeList);
                                        } catch (MyJsonFormatException e) {
                                            recentEpisodePosition = 0;
                                            ErrorActivity.startActivity(EpisodeActivity.this,ErrorActivity.MyJsoupResolveException);
                                        }
                                    } else {
                                        recentEpisodePosition = 0;
                                    }
                                    EpisodeAdapter adapter = new EpisodeAdapter(EpisodeActivity.this, episodeList, book, Book.COMIC, recentEpisodePosition);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.addItemDecoration(new DividerItemDecoration(EpisodeActivity.this, DividerItemDecoration.VERTICAL));
                                    recyclerView.scrollToPosition(recentEpisodePosition);

                                } else {
                                    textNonEpisode.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                } catch (MyJsoupResolveException e) {
                    e.printStackTrace();
                    ErrorActivity.startActivity(EpisodeActivity.this,ErrorActivity.MyJsoupResolveException);
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_episode, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_favourite:
                if (isFavourite  != -1){
                    Snackbar.make(v, "取消收藏", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    fabFavourite.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
                    try {
                        FavouriteManager.delete_favourite(isFavourite,Book.COMIC);
                    } catch (MyFileWriteException e) {
                        ErrorActivity.startActivity(EpisodeActivity.this,ErrorActivity.MyFileWriteException);
                    } catch (MyJsonFormatException e){
                        ErrorActivity.startActivity(EpisodeActivity.this,ErrorActivity.MyJsonFormatException);
                    }
                    isFavourite = -1;
                }else {
                    Snackbar.make(v, "收藏成功", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    fabFavourite.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
                    try {
                        FavouriteManager.add_favourite(book,Book.COMIC);
                        isFavourite = FavouriteManager.isFavourite(book.getWebsite(),book.getId(),Book.COMIC);
                    } catch (MyFileWriteException e) {
                        ErrorActivity.startActivity(EpisodeActivity.this,ErrorActivity.MyFileWriteException);
                    } catch (MyJsonFormatException e){
                        ErrorActivity.startActivity(EpisodeActivity.this,ErrorActivity.MyJsonFormatException);
                    }
                }
                break;
            case R.id.fab_last_read:
                Intent intent = new Intent(EpisodeActivity.this, ComicImageHorizon.class);
                intent.putExtra("episode",(Serializable) episodeList);
                intent.putExtra("book",book);
                intent.putExtra("episode_position", recentEpisodePosition);
                startActivity(intent);
        }
    }
}
