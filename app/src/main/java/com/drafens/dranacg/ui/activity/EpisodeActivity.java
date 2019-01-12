package com.drafens.dranacg.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.Episode;
import com.drafens.dranacg.R;
import com.drafens.dranacg.Sites;
import com.drafens.dranacg.error.MyError;
import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.error.MyJsonFormatException;
import com.drafens.dranacg.error.MyJsoupResolveException;
import com.drafens.dranacg.tools.FavouriteManager;
import com.drafens.dranacg.tools.ImageManager;
import com.drafens.dranacg.tools.Tools;
import com.drafens.dranacg.ui.adapter.EpisodeAdapter;

import java.io.Serializable;
import java.util.List;

public class EpisodeActivity extends AppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener,RadioGroup.OnCheckedChangeListener {
    private RecyclerView recyclerView;
    private TextView textNonEpisode;
    private FloatingActionButton fabFavourite;
    private FloatingActionButton fabLastRead;
    private EpisodeAdapter adapter;
    private BottomSheetDialog bottomSheetDialog;

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
        toolbar.setOnMenuItemClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        setEpisodeList();
        setFab();
    }

    private void showDialog(){
        RadioGroup radioGroup;
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_setting);
        radioGroup = bottomSheetDialog.findViewById(R.id.radio_group);
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener(this);
            bottomSheetDialog.show();
        }
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
                                            MyError.show(EpisodeActivity.this,MyError.MyJsoupResolveException);
                                        }
                                    } else {
                                        recentEpisodePosition = 0;
                                    }
                                    adapter = new EpisodeAdapter(EpisodeActivity.this, episodeList, book, Book.COMIC, recentEpisodePosition);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.addItemDecoration(new DividerItemDecoration(EpisodeActivity.this, DividerItemDecoration.VERTICAL));
                                    recyclerView.scrollToPosition(recentEpisodePosition);
                                } else {
                                    textNonEpisode.setVisibility(View.VISIBLE);
                                    episodeList.add(new Episode("",""));
                                }
                            }
                        });
                    }
                } catch (MyJsoupResolveException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyError.show(EpisodeActivity.this,MyError.MyJsoupResolveException);
                        }
                    });
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
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.action_settings:
                showDialog();
                break;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.radio_horizon:
                book.setReadMode(Book.HORIZON);
                bottomSheetDialog.hide();
                break;
            case R.id.radio_vertical:
                book.setReadMode(Book.VERTICAL);
                bottomSheetDialog.hide();
                break;
        }
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
                        FavouriteManager.delete_favourite(EpisodeActivity.this,isFavourite,Book.COMIC);
                    } catch (MyFileWriteException e) {
                        MyError.show(EpisodeActivity.this,MyError.MyFileWriteException);
                    } catch (MyJsonFormatException e){
                        MyError.show(EpisodeActivity.this,MyError.MyJsonFormatException);
                    }
                    isFavourite = -1;
                }else {
                    Snackbar.make(v, "收藏成功", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    fabFavourite.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
                    try {
                        if (episodeList.size()>0) {
                            book.setLastReadChapter(episodeList.get(recentEpisodePosition).getName());
                            book.setLastReadChapter_id(episodeList.get(recentEpisodePosition).getId());
                        }
                        book.setLastReadTime(Tools.getCurrentTime());
                        FavouriteManager.add_favourite(EpisodeActivity.this,book,Book.COMIC);
                        isFavourite = FavouriteManager.isFavourite(book.getWebsite(),book.getId(),Book.COMIC);
                    } catch (Exception e) {
                        MyError.show(EpisodeActivity.this,MyError.MyFileWriteException);
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.fab_last_read:
                Intent intent;
                if (book.getReadMode()==Book.HORIZON){
                    intent = new Intent(EpisodeActivity.this, ComicImageHorizon.class);
                }else if (book.getReadMode()==Book.VERTICAL){
                    intent = new Intent(EpisodeActivity.this, ComicImageVertical.class);
                }else{
                    if (FavouriteManager.getReadModeDefault(EpisodeActivity.this)==Book.VERTICAL){
                        intent = new Intent(EpisodeActivity.this, ComicImageVertical.class);
                    }else {
                        intent = new Intent(EpisodeActivity.this, ComicImageHorizon.class);
                    }
                }
                intent.putExtra("episode",(Serializable) episodeList);
                intent.putExtra("book",book);
                intent.putExtra("episode_position", recentEpisodePosition);
                startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recentEpisodePosition = resultCode;
        adapter.setRecentPosition(recentEpisodePosition);
        recyclerView.scrollToPosition(recentEpisodePosition);
    }
}
