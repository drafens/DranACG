package com.drafens.dranacg.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.Episode;
import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.error.MyJsonEmptyException;
import com.drafens.dranacg.error.MyJsonFormatException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavouriteManager {

    public static void add_favourite(Context context, Book book, int searchItem) throws MyFileWriteException, MyJsonFormatException {
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            String string = FileManager.readFiles("files/favourite_" + getJasonSearchItem(searchItem) + ".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
        }catch (Exception e){
            jsonArray = new JSONArray();
            jsonObject = new JSONObject();
        }
        try {
            JSONObject object = JsonManger.bookToJson(book);
            jsonArray.put(object);
            jsonObject.put("book",jsonArray);
            jsonObject.put("size",jsonArray.length());
        } catch (Exception e) {
            throw new MyJsonFormatException();
        }
        FileManager.writeFiles("favourite_"+getJasonSearchItem(searchItem)+".json",jsonObject.toString());
        Webdav.putFile(context, JsonManger.getSimpleJson(book));
    }

    public static void delete_favourite(Context context, int i, int searchItem) throws MyFileWriteException, MyJsonFormatException {
        JSONObject jsonObject;
        JSONArray jsonArray;
        String fileName = "";
        try{
            String string = FileManager.readFiles("files/favourite_"+getJasonSearchItem(searchItem)+".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
            fileName = Webdav.getFileName(jsonArray.getJSONObject(i));
            jsonArray.remove(i);
        }catch (Exception e){
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();
        }
        try{
            jsonObject.put("book",jsonArray);
            jsonObject.put("size",jsonArray.length());
        }catch (Exception e){
            e.printStackTrace();
            throw new MyJsonFormatException();
        }
        FileManager.writeFiles("favourite_"+getJasonSearchItem(searchItem)+".json",jsonObject.toString());
        if (!fileName.isEmpty()) {
            Webdav.delFile(context, fileName);
        }
    }

    /**
     *
     * @return isFavourite 收藏返回position，非收藏返回-1
     */
    public static int isFavourite(String book_siteItem, String book_id, int searchItem){
        JSONObject jsonObject;
        JSONArray jsonArray;
        int isFavourite = -1;
        try{
            String string = FileManager.readFiles("files/favourite_" + getJasonSearchItem(searchItem) + ".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
            JSONObject object;
            for (int i=0;i<jsonArray.length();i++){
                object = jsonArray.getJSONObject(i);
                String id = JsonManger.getString(object,"id");
                String siteItem = JsonManger.getString(object,"webSite");
                if(siteItem.equals(book_siteItem) && id.equals(book_id)){
                    isFavourite=i;
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return isFavourite;
    }

    public static boolean isUpdate(Book book){
        return !book.getLastReadChapter_id().equals(book.getUpdateChapter_id());
    }

    public static void update_favourite(boolean syncPermit, Context context, Book book,int searchItem) throws MyFileWriteException {
        int position = isFavourite(book.getWebsite(),book.getId(),searchItem);
        if (position!=-1) {
            JSONObject jsonObject;
            JSONArray jsonArray;
            try {
                String string = FileManager.readFiles("files/favourite_" + getJasonSearchItem(searchItem) + ".json");
                jsonObject = new JSONObject(string);
                jsonArray = jsonObject.getJSONArray("book");
                JSONObject object = JsonManger.bookToJson(book);
                jsonArray.put(position, object);
            } catch (Exception e) {
                jsonObject = new JSONObject();
                jsonArray = new JSONArray();
            }
            try {
                jsonObject.put("book", jsonArray);
                jsonObject.put("size", jsonArray.length());
                FileManager.writeFiles("favourite_" + getJasonSearchItem(searchItem) + ".json", jsonObject.toString());
                if (syncPermit) {
                    Webdav.putFile(context, JsonManger.getSimpleJson(book));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new MyFileWriteException();
            }
        }
    }

    public static void syncFavourite(Context context){
        List<Book> bookList;
        try {
            bookList = getBookList(Book.COMIC);
        } catch (Exception e) {
            bookList = new ArrayList<>();
        }
        Webdav.syncFile(context,bookList);
    }

    public static List<Book> getBookList(int searchItem) throws MyJsonEmptyException, MyJsonFormatException {
        JSONObject jsonObject;
        List<Book> bookList=new ArrayList<>();
        String string = FileManager.readFiles("files/favourite_"+getJasonSearchItem(searchItem)+".json");
        try{
            jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("book");
            JSONObject object;
            for (int i=0;i<jsonArray.length();i++){
                object = jsonArray.getJSONObject(i);
                Book book = JsonManger.jsonToBook(object);
                bookList.add(book);
            }
        } catch (Exception e){
            throw new MyJsonFormatException();
        }
        return bookList;
    }

    public static int getEpisodePosition(String lastReadEpisode_id, List<Episode> episodeList) throws MyJsonFormatException {
        int i;
        if (lastReadEpisode_id.isEmpty()) return 0;
        for (i=0;i<episodeList.size();i++){
            if(lastReadEpisode_id.equals(episodeList.get(i).getId())){
                break;
            }
        }
        if (i<episodeList.size())  return i;
        else throw new MyJsonFormatException();
    }

    public static int getReadModeDefault(Context context){
        int def;
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        def = preferences.getInt("read_mode",0);
        return def;
    }

    private static String getJasonSearchItem(int searchItem){
        String tab="";
        switch (searchItem){
            case Book.ANIMATION:
                tab = "animation";
                break;
            case Book.COMIC:
                tab = "comic";
                break;
        }
        return tab;
    }
}
