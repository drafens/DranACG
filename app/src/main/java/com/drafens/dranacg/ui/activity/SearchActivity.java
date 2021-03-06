package com.drafens.dranacg.ui.activity;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.R;
import com.drafens.dranacg.Sites;
import com.drafens.dranacg.error.MyError;
import com.drafens.dranacg.error.MyJsoupResolveException;
import com.drafens.dranacg.error.MyNetworkException;
import com.drafens.dranacg.ui.adapter.SearchResultAdapter;

import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener{
    private RecyclerView recyclerView;
    private EditText editText;
    private FloatingActionButton button;
    private TextView reminderLoading;
    private TextView reminderNonResult;
    private String siteItem = Sites.COMIC_GROUP[0];
    private String searchContent;
    private List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String s = bundle.getString("site_item");
            if (s != null) {
                siteItem = s;
            }
        }
        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        editText = findViewById(R.id.et_search);
        reminderLoading = findViewById(R.id.reminder_loading);
        reminderNonResult = findViewById(R.id.reminder_non_result);
        button = findViewById(R.id.btn_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("搜索");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editText.setOnEditorActionListener(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchResult();
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId==EditorInfo.IME_ACTION_SEARCH){
            getSearchResult();
        }
        return false;
    }

    private void getSearchResult() {
        searchContent = editText.getText().toString();
        if (!searchContent.isEmpty()) {
            reminderLoading.setVisibility(View.VISIBLE);
            reminderNonResult.setVisibility(View.GONE);
            button.setClickable(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Sites sites = Sites.getSites(siteItem);
                    try {
                        if (sites != null) {
                            bookList = sites.getSearch(searchContent);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    reminderLoading.setVisibility(View.GONE);
                                    if (bookList.size()<=0){
                                        reminderNonResult.setVisibility(View.VISIBLE);
                                    }
                                    button.setClickable(true);
                                    SearchResultAdapter adapter = new SearchResultAdapter(SearchActivity.this, bookList);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.addItemDecoration(new DividerItemDecoration(SearchActivity.this, DividerItemDecoration.VERTICAL));
                                }
                            });
                        }else {
                            Toast.makeText(SearchActivity.this,"请选择网站",Toast.LENGTH_SHORT).show();
                        }
                    }catch (MyNetworkException e){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyError.show(SearchActivity.this,MyError.MyNetworkException);
                                reminderLoading.setVisibility(View.GONE);
                                reminderNonResult.setVisibility(View.GONE);
                                button.setClickable(true);
                            }
                        });
                    }catch (MyJsoupResolveException e){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyError.show(SearchActivity.this,MyError.MyJsoupResolveException);
                                reminderLoading.setVisibility(View.GONE);
                                reminderNonResult.setVisibility(View.GONE);
                                button.setClickable(true);
                            }
                        });
                    }
                }
            }).start();
            //收起输入法
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }else {
            Toast.makeText(SearchActivity.this,"请输入搜索内容",Toast.LENGTH_SHORT).show();
        }
    }
}
