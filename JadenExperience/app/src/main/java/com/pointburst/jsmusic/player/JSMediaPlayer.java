package com.pointburst.jsmusic.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.utils.Logger;

import java.io.IOException;

/**
 * Created by m.farhan on 1/22/15.
 */

public class JSMediaPlayer extends android.media.MediaPlayer {
    /**
     * Set of states for JSMediaPlayer:<br>
     * EMPTY, CREATED, PREPARED, STARTED, PAUSED, STOPPED, ERROR
     */
    public enum MPStates {
        EMPTY, CREATED, PREPARED, STARTED, PAUSED, STOPPED, ERROR
    }

    private MPStates mState;
    private Media mMedia;

    public Media getMedia() {
        return mMedia;
    }

    /**
     * Sets a JSMediaPlayer's data source as the provided media url
     */
    public void setMedia(Media media) {
       mMedia = media;
        try {
            setDataSource(mMedia.getStreamUrl());
            setState(MPStates.CREATED);
        }
        catch (Exception e) {
            Logger.print("setDataSource failed");
            setState(MPStates.ERROR);
        }
    }

    /**
     * Instantiates a JSMediaPlayer object.
     */
    public JSMediaPlayer() {
        super();
        setState(MPStates.CREATED);
    }

    /**
     * Instantiates a JSMediaPlayer object with the Audio Stream Type
     */
    public JSMediaPlayer(Media media) {
        super();
        this.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMedia = media;
        try {
            setDataSource(media.getStreamUrl());
            setState(MPStates.CREATED);
        }
        catch (Exception e) {
            Logger.print("setDataSourceFailed");
            setState(MPStates.ERROR);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.mState = MPStates.EMPTY;
    }

    @Override
    public void start() {
        super.start();
        setState(MPStates.STARTED);
    }

    @Override
    public void pause() {

        super.pause();
        setState(MPStates.PAUSED);

    }

    @Override
    public void stop() {
        super.stop();
        setState(MPStates.STOPPED);
    }

    @Override
    public void release() {
        super.release();
        setState(MPStates.EMPTY);
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
        setState(MPStates.PREPARED);
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        super.prepareAsync();
        setState(MPStates.PREPARED);
    }

    public MPStates getState() {
        return mState;
    }

    /**
     * @param state the state to set
     */
    public void setState(MPStates state) {
        this.mState = state;
    }

    public boolean isCreated() {
        return (mState == MPStates.CREATED);
    }

    public boolean isEmpty() {
        return (mState == MPStates.EMPTY);
    }

    public boolean isStopped() {
        return (mState == MPStates.STOPPED);
    }

    public boolean isStarted() {
        return (mState == MPStates.STARTED || this.isPlaying());
    }

    public boolean isPaused() {
        return (mState == MPStates.PAUSED);
    }

    public boolean isPrepared() {
        return (mState == MPStates.PREPARED);
    }
}
