package com.drafens.dranacg;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Book implements Serializable {
    public static final int ANIMATION = 0;
    public static final int COMIC = 1;
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
    private String readMode="";

    Book(String website,String id){
        this.website = website;
        this.id=id;
    }

    String getId() {
        return id;
    }
    String getWebsite() {
        return website;
    }
    String getName() {
        return name;
    }
    String getAuthor() {
        return author;
    }
    String getIcon() {
        return icon;
    }
    String getType() {
        return type;
    }
    String getUpdateChapter() {
        return updateChapter;
    }
    String getUpdateChapter_id() {
        return updateChapter_id;
    }
    String getUpdateTime() {
        return updateTime;
    }
    String getLastReadChapter() {
        return lastReadChapter;
    }
    String getLastReadChapter_id() {
        return lastReadChapter_id;
    }
    String getLastReadTime() {
        return lastReadTime;
    }
    String getBriefInfo() {
        return briefInfo;
    }
    String getReadPosition() {
        return readPosition;
    }
    String getReadMode() {
        return readMode;
    }

    void setName(String name) {
        this.name = name;
    }
    void setAuthor(String author) {
        this.author = author;
    }
    void setIcon(String icon) {
        this.icon = icon;
    }
    void setType(String type) {
        this.type = type;
    }
    void setUpdateChapter(String updateChapter) {
        this.updateChapter = updateChapter;
    }
    void setUpdateChapter_id(String updateChapter_id) {
        this.updateChapter_id = updateChapter_id;
    }
    void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    void setLastReadChapter(String lastReadChapter) {
        this.lastReadChapter = lastReadChapter;
    }
    void setLastReadChapter_id(String lastReadChapter_id) {
        this.lastReadChapter_id = lastReadChapter_id;
    }
    void setLastReadTime(String lastReadTime) {
        this.lastReadTime = lastReadTime;
    }
    void setBriefInfo(String briefInfo) {
        this.briefInfo = briefInfo;
    }
    void setReadMode(String readMode) {
        this.readMode = readMode;
    }
    void setReadPosition(String readPosition) {
        this.readPosition = readPosition;
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
        return string;
    }
}
