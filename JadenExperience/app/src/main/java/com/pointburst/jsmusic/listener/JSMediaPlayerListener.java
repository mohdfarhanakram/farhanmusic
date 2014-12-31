package com.pointburst.jsmusic.listener;

/**
 * Created by m.farhan on 12/31/14.
 */
public interface JSMediaPlayerListener {
    public void onPlaySong(int albumIndex,int mediaIndex,Object object);
    public void onStopSong();
}
