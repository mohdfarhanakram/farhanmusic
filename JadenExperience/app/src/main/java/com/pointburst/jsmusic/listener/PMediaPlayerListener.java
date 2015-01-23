package com.pointburst.jsmusic.listener;

/**
 * Created by FARHAN on 1/22/2015.
 */
public interface PMediaPlayerListener {
    public void onInitializePlayerStart(String message);
    public void onInitializePlayerSuccess();
    public void onError();
    public void onUpdateMpUi(int index);
}
