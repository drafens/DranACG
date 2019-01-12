package com.drafens.dranacg;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Episode implements Serializable {
    private String name;
    private String id;

    public Episode(String name, String id){
        this.name=name;
        this.id=id;
    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        String string = "id:"+id;
        string += "\r\nname:"+name;
        return string;
    }
}