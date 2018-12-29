package com.drafens.dranacg;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "SearchActivity";
    private RecyclerView recyclerView;
    private EditText editText;
    private FloatingActionButton button;
    private TextView reminderLoading;
    private TextView reminderNonResult;
    private String siteItem=Sites.GUFENG;
    private String searchContent;
    private List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void getSearchResult() {
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
                                BookAdapter adapter = new BookAdapter(SearchActivity.this, bookList);
                                recyclerView.setAdapter(adapter);
                                recyclerView.addItemDecoration(new DividerItemDecoration(SearchActivity.this, DividerItemDecoration.VERTICAL));
                                Log.d(TAG, "run: ");
                            }
                        });
                    }else {
                        Toast.makeText(SearchActivity.this,"请选择网站",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
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
        manager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search:
                searchContent = editText.getText().toString();
                if (searchContent.length()>0) {
                    getSearchResult();
                    //收起输入法
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }else {
                    Toast.makeText(SearchActivity.this,"请输入搜索内容",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
