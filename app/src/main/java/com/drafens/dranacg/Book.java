package com.drafens.dranacg;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Book implements Serializable {
    //searchItem
    public static final int ANIMATION = 0;
    public static final int COMIC = 1;
    //readMode:default=0
    public static final int HORIZON = 1;
    public static final int VERTICAL = 2;
    private String id;
    private String website;
    private String name="";
    private String author="";
    private String type="";
    private String icon="";
    private String briefInfo="";
    private String updateChapter="";
    private String updateChapter_id="";
    private String updateTime="";
    private String lastReadChapter="";
    private String lastReadChapter_id="";
    private String lastReadTime="";
    private String readPosition="";
    private int readMode=0;
    private boolean isSync=false;

    public Book(String website, String id){
        this.website = website;
        this.id=id;
    }

    public String getId() {
        return id;
    }
    public String getWebsite() {
        return website;
    }
    public String getName() {
        return name;
    }
    public String getAuthor() {
        return author;
    }
    public String getIcon() {
        return icon;
    }
    public String getType() {
        return type;
    }
    public String getUpdateChapter() {
        return updateChapter;
    }
    public String getUpdateChapter_id() {
        return updateChapter_id;
    }
    public String getUpdateTime() {
        return updateTime;
    }
    public String getLastReadChapter() {
        return lastReadChapter;
    }
    public String getLastReadChapter_id() {
        return lastReadChapter_id;
    }
    public String getLastReadTime() {
        return lastReadTime;
    }
    public String getBriefInfo() {
        return briefInfo;
    }
    public String getReadPosition() {
        return readPosition;
    }
    public int getReadMode() {
        return readMode;
    }
    public boolean getIsSync(){
        return isSync;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setUpdateChapter(String updateChapter) {
        this.updateChapter = updateChapter;
    }
    public void setUpdateChapter_id(String updateChapter_id) {
        this.updateChapter_id = updateChapter_id;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    public void setLastReadChapter(String lastReadChapter) {
        this.lastReadChapter = lastReadChapter;
    }
    public void setLastReadChapter_id(String lastReadChapter_id) {
        this.lastReadChapter_id = lastReadChapter_id;
    }
    public void setLastReadTime(String lastReadTime) {
        this.lastReadTime = lastReadTime;
    }
    public void setBriefInfo(String briefInfo) {
        this.briefInfo = briefInfo;
    }
    public void setReadMode(int readMode) {
        this.readMode = readMode;
    }
    public void setReadPosition(String readPosition) {
        this.readPosition = readPosition;
    }

    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }

    @NonNull
    @Override
    public String toString() {
        String string = "website:"+website;
        string += "\r\nid:"+id;
        string += "\r\nname:"+name;
        string += "\r\nauthor:"+author;
        string += "\r\ntype:"+type;
        string += "\r\nicon:"+icon;
        string += "\r\nbriefInfo:"+briefInfo;
        string += "\r\nupdateChapter:"+updateChapter;
        string += "\r\nupdateChapter_id:"+updateChapter_id;
        string += "\r\nupdateTime:"+updateTime;
        string += "\r\nlastReadChapter:"+lastReadChapter;
        string += "\r\nlastReadChapter_id:"+lastReadChapter_id;
        string += "\r\nlastReadTime:"+lastReadTime;
        string += "\r\nreadPosition"+readPosition;
        string += "\r\nreadMode:"+readMode;
        string += "\r\nisSync:"+isSync;
        return string;
    }
}
