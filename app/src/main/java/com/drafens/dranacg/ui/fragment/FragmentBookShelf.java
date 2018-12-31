package com.drafens.dranacg.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.R;
import com.drafens.dranacg.Sites;
import com.drafens.dranacg.error.MyError;
import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.error.MyJsonEmptyException;
import com.drafens.dranacg.error.MyJsonFormatException;
import com.drafens.dranacg.error.MyJsoupResolveException;
import com.drafens.dranacg.tools.FavouriteManager;
import com.drafens.dranacg.ui.adapter.FavouriteAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentBookShelf extends Fragment {

    private TextView textNonFavourite;

    private FavouriteAdapter adapter;
    private List<Book> bookList;
    private int updateSize;

    private boolean isFirstRun = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_shelf, container,false);
        textNonFavourite = view.findViewById(R.id.text_non_favourite);
        getUpdateList();
        initView(view);
        initUpdateList();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isFirstRun){
            getUpdateList();
            adapter.updateData(updateSize, bookList);
        }else {
            isFirstRun = false;
        }
    }

    void initView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);

        adapter = new FavouriteAdapter(getContext(),Book.COMIC,bookList,updateSize);
        recyclerView.setAdapter(adapter);
    }

    //联网检查更新
    private void initUpdateList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Book> bookListUpdate = new ArrayList<>();
                List<Book> bookListNoUpdate = new ArrayList<>();
                for (int i = 0; i < bookList.size(); i++) {
                    Book book = bookList.get(i);
                    try {
                        Sites sites = Sites.getSites(book.getWebsite());
                        if (sites != null) {
                            book = sites.getBook(book);
                        }
                        FavouriteManager.update_favourite(book, Book.COMIC);
                    } catch (MyJsoupResolveException e) {
                        e.printStackTrace();
                        MyError.show(getContext(), MyError.MyJsoupResolveException);
                    } catch (MyFileWriteException e) {
                        e.printStackTrace();
                        MyError.show(getContext(), MyError.MyFileWriteException);
                    }
                    if (FavouriteManager.isUpdate(book)) {
                        bookListUpdate.add(book);
                    } else {
                        bookListNoUpdate.add(book);
                    }
                }
                final int updateSize = bookListUpdate.size();
                bookListUpdate.addAll(bookListNoUpdate);
                bookList = new ArrayList<>(bookListUpdate);
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(updateSize, bookList);
                    }
                });
            }
        }).start();
    }

    //本地检查更新
    void getUpdateList(){
        try {
            bookList = new ArrayList<>(FavouriteManager.getBookList(Book.COMIC));
        } catch (MyJsonEmptyException e) {
            bookList = new ArrayList<>();
            textNonFavourite.setVisibility(View.VISIBLE);
        } catch (MyJsonFormatException e) {
            e.printStackTrace();
            MyError.show(getContext(),MyError.MyJsonFormatException);
        }
        List<Book> bookListUpdate = new ArrayList<>();
        List<Book> bookListNoUpdate = new ArrayList<>();
        for (int i = 0; i < bookList.size(); i++) {
            Book book = bookList.get(i);
            if (FavouriteManager.isUpdate(book)) {
                bookListUpdate.add(book);
            } else {
                bookListNoUpdate.add(book);
            }
        }
        updateSize = bookListUpdate.size();
        bookListUpdate.addAll(bookListNoUpdate);
        bookList = new ArrayList<>(bookListUpdate);
    }
}
