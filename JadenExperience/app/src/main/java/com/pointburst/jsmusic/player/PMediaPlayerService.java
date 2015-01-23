package com.pointburst.jsmusic.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.pointburst.jsmusic.listener.PMediaPlayerListener;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.ui.MainActivity;
import com.pointburst.jsmusic.utils.Logger;

/**
 * Created by FARHAN on 1/22/2015.
 */

public class PMediaPlayerService extends Service implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private PMediaPlayer mMediaPlayer = new PMediaPlayer();
    private final Binder mBinder = new MediaPlayerBinder();
    private PMediaPlayerListener mClient;


    public class MediaPlayerBinder extends Binder {

        public PMediaPlayerService getService() {
            return PMediaPlayerService.this;
        }

    }

    public PMediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void initializePlayer(Media media) {
        mClient.onInitializePlayerStart("Connecting...");
        mMediaPlayer = new PMediaPlayer(media);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.prepareAsync();
    }

    /**
     * Initializes a StatefulMediaPlayer for streaming playback of the provided stream url
     * @param streamUrl The URL of the stream to play.
     */
    public void initializePlayer(String streamUrl) {

        mMediaPlayer = new PMediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(streamUrl);
        }
        catch (Exception e) {
            Logger.print("error setting data source");
            mMediaPlayer.setState(PMediaPlayer.MPStates.ERROR);
        }
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer player, int percent) {

    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        mMediaPlayer.reset();
        mClient.onError();
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        mClient.onInitializePlayerSuccess();
        startMediaPlayer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void pauseMediaPlayer() {
        Logger.print("pauseMediaPlayer() called");
        mMediaPlayer.pause();
        stopForeground(true);

    }

    public void setClient(PMediaPlayerListener client) {
        this.mClient = client;
    }

    /**
     * Starts the contained StatefulMediaPlayer and foregrounds the service to support
     * persisted background playback.
     */
    public void startMediaPlayer() {
        Context context = getApplicationContext();

        //set to foreground
        Notification notification = new Notification(android.R.drawable.ic_media_play, "MediaPlayerService",
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        CharSequence contentTitle = "MediaPlayerService Is Playing";
        CharSequence contentText = mMediaPlayer.getMedia().getTitle();
        notification.setLatestEventInfo(context, contentTitle,
                contentText, pendingIntent);
        startForeground(1, notification);

        Logger.print("startMediaPlayer() called");
        mMediaPlayer.start();
    }

    /**
     * Stops the contained StatefulMediaPlayer.
     */
    public void stopMediaPlayer() {
        stopForeground(true);
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    public void resetMediaPlayer() {
        stopForeground(true);
        mMediaPlayer.reset();
    }

}
