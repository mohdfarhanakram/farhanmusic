package com.pointburst.jsmusic.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by FARHAN on 12/28/2014.
 */
public class Result implements Serializable{
    private ArrayList<Album> albums;

    public ArrayList<Album> getAlbums() {
        if(albums==null)
            albums = new ArrayList<Album>();
        return albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }
}
