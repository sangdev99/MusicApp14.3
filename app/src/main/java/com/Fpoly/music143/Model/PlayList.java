package com.Fpoly.music143.Model;

import java.util.ArrayList;

public class PlayList {

    private ArrayList<String> songs = null;
    private String name;
    private String iD;
    private Boolean deleted;

    public PlayList() {
    }


    public PlayList(ArrayList<String> songs, String name, String iD, Boolean deleted) {
        super();
        this.songs = songs;
        this.name = name;
        this.iD = iD;
        this.deleted = deleted;
    }
    public ArrayList<String> getSongs() {
        return songs;
    }
    public void setSongs(ArrayList<String> songs) {
        this.songs = songs;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getID() {
        return iD;
    }
    public void setID(String iD) {
        this.iD = iD;
    }
    public Boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}