package com.pointburst.jsmusic.model;

import java.util.ArrayList;

/**
 * Created by FARHAN on 12/28/2014.
 */
public class Result {
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
