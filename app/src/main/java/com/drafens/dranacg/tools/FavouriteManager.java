package com.drafens.dranacg.tools;

import android.os.Environment;
import android.util.Log;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.error.MyJsonEmptyException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FavouriteManager {
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/drafens/";
    private static final String TAG = "FavouriteManager";

    public static void add_favourite(Book book, int searchItem) throws MyFileWriteException{
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            String string = readFiles("files/favourite_" + getJasonSearchItem(searchItem) + ".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
        }catch (Exception e){
            jsonArray = new JSONArray();
            jsonObject = new JSONObject();
        }
        JSONObject object = new JSONObject();
        try {
            object.put("webSite",book.getWebsite());
            object.put("id",book.getId());
            object.put("name",book.getName());
            object.put("updateChapter",book.getUpdateChapter());
            object.put("updateChapter_id",book.getUpdateChapter_id());
            object.put("updateTime",book.getUpdateTime());
            object.put("author",book.getAuthor());
            object.put("type",book.getType());
            object.put("icon",book.getIcon());
            object.put("lastReadChapter",book.getLastReadChapter());
            object.put("lastReadChapter_id",book.getLastReadChapter_id());
            object.put("lastReadTime",book.getLastReadTime());
            object.put("briefInfo",book.getBriefInfo());
            object.put("readMode",book.getReadMode());
            jsonArray.put(object);
            jsonObject.put("book",jsonArray);
            jsonObject.put("size",jsonArray.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeFiles("files/","favourite_"+getJasonSearchItem(searchItem)+".json",jsonObject.toString());
    }

    public static void delete_favourite(int i,int searchItem) throws MyFileWriteException{
        JSONObject jsonObject;
        JSONArray jsonArray;
        try{
            String string = readFiles("files/favourite_"+getJasonSearchItem(searchItem)+".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
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
        }
        writeFiles("files/","favourite_"+getJasonSearchItem(searchItem)+".json",jsonObject.toString());
    }

    private static void writeFiles(String catalog, String fileName, String data) throws MyFileWriteException {
        try {
            File files = new File(PATH+catalog);
            if(!files.exists()) {
                boolean flag = files.mkdirs();
                if(!flag){
                    throw new MyFileWriteException();
                }
            }
            File file = new File(PATH+catalog+fileName);
            if (!file.exists()){
                if(!file.createNewFile()){
                    throw new MyFileWriteException();
                }
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyFileWriteException();
        }
    }

    private static String readFiles(String fileName) throws MyJsonEmptyException {
        String path = PATH + fileName;
        String string;
        try{
            FileInputStream inputStream = new FileInputStream(path);
            int length = inputStream.available();
            byte [] buffer = new byte[length];
            int flag = inputStream.read(buffer);
            if (flag == -1){
                throw new MyJsonEmptyException();
            }
            string = new String(buffer, "UTF-8");
            inputStream.close();
        }
        catch(Exception e){
            e.printStackTrace();
            throw new MyJsonEmptyException();
        }
        return string;
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

    /**
     *
     * @return isFavourite 收藏返回position，非收藏返回-1
     */
    public static int isFavourite(String book_siteItem, String book_id, int searchItem){
        JSONObject jsonObject;
        JSONArray jsonArray;
        int isFavourite = -1;
        try{
            String string = readFiles("files/favourite_" + getJasonSearchItem(searchItem) + ".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
            JSONObject object;
            for (int i=0;i<jsonArray.length();i++){
                object = jsonArray.getJSONObject(i);
                String id = getString(object,"id");
                String siteItem = getString(object,"webSite");
                Log.d(TAG, siteItem+"*"+book_siteItem+"*"+id+"*"+book_id);
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

    public static void update_favourite(int i,Book book,int searchItem){
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            String string = readFiles("files/favourite_" + getJasonSearchItem(searchItem) + ".json");
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray("book");
            JSONObject object = new JSONObject();
            object.put("id", book.getId());
            object.put("name", book.getName());
            object.put("updateChapter", book.getUpdateChapter());
            object.put("updateChapter_id", book.getUpdateChapter_id());
            object.put("updateTime", book.getUpdateTime());
            object.put("author", book.getAuthor());
            object.put("type", book.getType());
            object.put("icon", book.getIcon());
            object.put("webSite", book.getWebsite());
            object.put("lastReadChapter", book.getLastReadChapter());
            object.put("lastReadChapter_id", book.getLastReadChapter_id());
            object.put("lastReadTime", book.getLastReadTime());
            object.put("briefInfo", book.getBriefInfo());
            object.put("readMode",book.getReadMode());
            jsonArray.put(i,object);
        }catch (Exception e){
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();
        }try{
            jsonObject.put("book",jsonArray);
            jsonObject.put("size",jsonArray.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            writeFiles("files/","favourite_"+getJasonSearchItem(searchItem)+".json",jsonObject.toString());
        } catch (MyFileWriteException e) {
            e.printStackTrace();
        }
    }

    private static String getString(JSONObject object,String Tag){
        String string;
        try{
            string = object.getString(Tag);
        }catch (Exception e){
            string = "";
        }
        return string;
    }

}
