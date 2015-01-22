package com.pointburst.jsmusic.listener;

import android.media.MediaPlayer;
import com.pointburst.jsmusic.model.Media;

/**
 * Created by FARHAN on 1/1/2015.
 */
public interface Output {

    void change(Media media);

    void play(Media media);

    void toggle();

    /**
     * @return true if this call had an effect
     */
    boolean pause();

    /**
     * @return true if this call had an effect
     */
    boolean play();

    void goToMillis(int millis);

    int getCurrentMillis();

    Media getCurrMedia();

    void setOnCompletionListener(MediaPlayer.OnCompletionListener listener);
}
