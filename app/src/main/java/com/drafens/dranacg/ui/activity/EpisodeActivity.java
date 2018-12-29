package com.drafens.dranacg.ui.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.R;
import com.drafens.dranacg.tools.ImageManager;

public class EpisodeActivity extends AppCompatActivity {
    private AppBarLayout appBarLayout;

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        book = (Book) getIntent().getSerializableExtra("book");

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_episode);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(book.getName());
        appBarLayout = findViewById(R.id.app_bar);
        ImageManager.setBackground(EpisodeActivity.this,book.getIcon(),appBarLayout);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "收藏成功", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fab.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_episode, menu);
        return true;
    }
}
