package com.drafens.dranacg.tools;

import com.drafens.dranacg.Book;
import com.drafens.dranacg.error.MyJsonFormatException;

import org.json.JSONObject;

class JsonManger {

    static JSONObject getSimpleJson(Book book) throws MyJsonFormatException {
        JSONObject object = new JSONObject();
        try{
            object.put("webSite", book.getWebsite());
            object.put("id", book.getId());
            object.put("name", book.getName());
            object.put("author", book.getAuthor());
            object.put("type", book.getType());
            object.put("icon", book.getIcon());
            object.put("lastReadChapter", book.getLastReadChapter());
            object.put("lastReadChapter_id", book.getLastReadChapter_id());
            object.put("lastReadTime", book.getLastReadTime());
            object.put("readMode",book.getReadMode());
            object.put("readPosition",book.getReadPosition());
        } catch (Exception e){
            throw new MyJsonFormatException();
        }
        return object;
    }

    static JSONObject bookToJson(Book book) throws MyJsonFormatException {
        JSONObject object = new JSONObject();
        try{
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
            object.put("readPosition",book.getReadPosition());
            object.put("isSync",book.getIsSync());
        } catch (Exception e){
            throw new MyJsonFormatException();
        }
        return object;
    }
    static Book jsonToBook(JSONObject object){
        Book book = new Book(getString(object,"webSite"),getString(object,"id"));
        book.setName(getString(object,"name"));
        book.setAuthor(getString(object,"author"));
        book.setType(getString(object,"type"));
        book.setIcon(getString(object,"icon"));
        book.setUpdateChapter(getString(object,"updateChapter"));
        book.setUpdateChapter_id(getString(object,"updateChapter_id"));
        book.setUpdateTime(getString(object,"updateTime"));
        book.setLastReadChapter(getString(object,"lastReadChapter"));
        book.setLastReadChapter_id(getString(object,"lastReadChapter_id"));
        book.setLastReadTime(getString(object,"lastReadTime"));
        book.setBriefInfo(getString(object,"briefInfo"));
        book.setReadMode(getInt(object,"readMode"));
        book.setReadPosition(getString(object,"readPosition"));
        book.setIsSync(getBol(object,"isSync"));
        return book;
    }

    static String getString(JSONObject object, String Tag){
        String string;
        try{
            string = object.getString(Tag);
        }catch (Exception e){
            string = "";
        }
        return string;
    }

    private static int getInt(JSONObject object,String Tag){
        int i;
        try{
            i = object.getInt(Tag);
        }catch (Exception e){
            i = 0;
        }
        return i;
    }

    private static boolean getBol(JSONObject object,String Tag){
        boolean bol;
        try{
            bol = object.getBoolean(Tag);
        }catch (Exception e){
            bol = false;
        }
        return bol;
    }
}
