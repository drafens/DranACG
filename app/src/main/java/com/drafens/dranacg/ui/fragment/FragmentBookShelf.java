package com.drafens.dranacg.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.R;
import com.drafens.dranacg.Sites;
import com.drafens.dranacg.error.ErrorActivity;
import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.error.MyJsonEmptyException;
import com.drafens.dranacg.error.MyJsonFormatException;
import com.drafens.dranacg.error.MyJsoupResolveException;
import com.drafens.dranacg.tools.FavouriteManager;
import com.drafens.dranacg.ui.adapter.FavouriteAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentBookShelf extends Fragment {

    private TextView textNonFavourite;

    private List<Book> bookList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_shelf, container,false);
        try {
            bookList = FavouriteManager.getBookList(Book.COMIC);
        } catch (MyJsonEmptyException e) {
            bookList = new ArrayList<>();
            textNonFavourite.setVisibility(View.VISIBLE);
        } catch (MyJsonFormatException e) {
            e.printStackTrace();
            ErrorActivity.startActivity(getContext(),ErrorActivity.MyJsonFormatException);
        }
        initView(view);
        return view;
    }

    void initView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        textNonFavourite = view.findViewById(R.id.text_non_favourite);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);

        int updateSize = getUpdateList();
        FavouriteAdapter adapter = new FavouriteAdapter(getContext(),Book.COMIC,bookList,updateSize);
        recyclerView.setAdapter(adapter);
    }

    private int getUpdateList() {//检查更新
        List<Book> bookListUpdate = new ArrayList<>();
        List<Book> bookListNoUpdate = new ArrayList<>();
        for (int i=0;i<bookList.size();i++) {
            Book book = bookList.get(i);
            if(FavouriteManager.isUpdate(book)){
                Sites sites = Sites.getSites(book.getWebsite());
                try {
                    if (sites != null) {
                        book = sites.getBook(book);
                    }
                } catch (MyJsoupResolveException e) {
                    e.printStackTrace();
                    ErrorActivity.startActivity(getContext(),ErrorActivity.MyJsoupResolveException);
                }
                try {
                    FavouriteManager.update_favourite(book,Book.COMIC);
                } catch (MyFileWriteException e) {
                    e.printStackTrace();
                }
                bookListUpdate.add(book);
            }else{
                bookListNoUpdate.add(book);
            }
        }
        int updateSize = bookListUpdate.size();
        bookListUpdate.addAll(bookListNoUpdate);
        bookList = new ArrayList<>(bookListUpdate);
        return updateSize;
    }
}
