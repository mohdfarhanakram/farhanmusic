package com.pointburst.jsmusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.*;
import android.util.Log;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.ui.MainActivity;
import com.pointburst.jsmusic.utils.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by FARHAN on 1/1/2015.
 */
public class PlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener
{
    private final static int NOTIFICATION_ID = 22;


    public enum Notify
    {
        STARTED,
        STOPPED,
        CHANGED
    }

    public final static String PARAM_OBJECT_TRACK = "track";
    public final static String PARAM_INTEGER_MILLIS = "millis";

    public class PlayerServiceBinder extends Binder {
        public PlayerService getPlayerService() {
            return PlayerService.this;
        }

        public void register(Messenger messenger)
        {
            clients.add(messenger);
        }

        public void unregister(Messenger messenger)
        {
            clients.remove(messenger);
        }
    }

    Binder playerServiceBinder = new PlayerServiceBinder();

    private final Set<Messenger> clients = new HashSet<Messenger>();

    @Override
    public IBinder onBind(Intent intent)
    {
        return playerServiceBinder;
    }

    private MediaPlayer mp = null; //use getMp to access plz
    private boolean isPlaying = false;
    private Media currMedia = null;

    private boolean initialized = false; //indicates the player is at least in Initialized mode


    @Override
    public void onCreate()
    {
        Logger.print("PlayerService.onCreate() called");

        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        Logger.print("PlayerService.onDestroy() called");
        super.onDestroy();
        release();
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        Logger.print("PlayerService.onUnbind() called");
        return super.onUnbind(intent);
    }


    public void change(Media m){
        if(m != null)
        {
            boolean wasPlaying = isPlaying;
            prepare(m);

            if(wasPlaying){
                playInternal();
            }
            notifyTrackChanged(m, getMp().getDuration());
        }
    }

    public void play(Media m)
    {
        if(m != null)
        {
            boolean wasPlaying = isPlaying;
            prepare(m);
            playInternal();
            if(!wasPlaying)
            {
                notifyStarted();
            }
            notifyTrackChanged(m, getMp().getDuration());
        }
    }

    public void toggle()
    {
        if(initialized)
        {
            if(isPlaying)
            {
                pause();
            }
            else
            {
                play();
            }
        }
    }

    /**
     * @return true if this call had an effect
     */
    public boolean pause()
    {
        boolean stopped = pauseInternal();
        if(stopped)
        {
            notifyStopped();
        }
        return stopped;
    }

    public boolean pauseInternal()
    {
        if(initialized && isPlaying)
        {
            getMp().pause();
            isPlaying = false;
            return true;
        }
        return false;
    }

    /**
     * @return true if this call had an effect
     */
    public boolean play(){
        boolean started = playInternal();
        if(started)
        {
            notifyStarted();
        }
        return started;
    }

    /**
     * @return true if this call had an effect
     */
    public boolean playInternal(){
        if(!isPlaying && initialized)
        {
            getMp().start();
            isPlaying = true;
            return true;
        }
        return false;
    }


    public void goToMillis(int millis)
    {
        if(initialized)
        {
            getMp().seekTo(Math.max(Math.min(millis, getMp().getDuration()), 0));
        }
    }

    public int getCurrentMillis()
    {
        return initialized ? getMp().getCurrentPosition() : 0;
    }

    public Media getCurrMedia()
    {
        return currMedia;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener)
    {
        getMp().setOnCompletionListener(listener);
    }

    /**
     * @return MediaPlayer at least in idle state
     */
    private MediaPlayer getMp(){
        if(mp == null)
        {
            mp = new MediaPlayer();
            mp.reset();
        }
        return mp;
    }

    /**
     * releases the mp and kill the reference because the old instance is not usable anymore
     */
    public void release()
    {
        initialized = false;
        pause();
        setOnCompletionListener(null);
        getMp().release();
        mp = null;
    }

    private void prepare(Media m)
    {
        if (m != null)
        {
            try
            {
                final MediaPlayer mediaPlayer = getMp();
                mp.reset();
                mediaPlayer.setDataSource(m.getStreamUrl());

                mediaPlayer.prepareAsync();
                initialized = true;
                isPlaying = false;
                currMedia = m;
            }
            catch (IOException e)
            {
                Logger.print(e.getMessage());
            }
        }
    }

    //---------------------------------- Observable

    private void notifyTrackChanged(Media media, int lengthInMillis){
        if(isPlaying)
        {
            startForeground(NOTIFICATION_ID, getNotification());
        }
        Bundle params = new Bundle();
        params.putSerializable(PARAM_OBJECT_TRACK, media);
        params.putInt(PARAM_INTEGER_MILLIS, lengthInMillis);
        notifyClients(Notify.CHANGED, params);
    }

    private void notifyStarted(){
        startForeground(NOTIFICATION_ID, getNotification());
        notifyClients(Notify.STARTED, null);
    }

    private void notifyStopped(){
        stopForeground(true);
        notifyClients(Notify.STOPPED, null);
    }

    private Notification getNotification()
    {
        Notification note = new Notification(R.drawable.ic_launcher, getText(R.string.app_name), System.currentTimeMillis());
        Intent i=new Intent(this, MainActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi=PendingIntent.getActivity(this, 0,i, 0);

        note.setLatestEventInfo(this, getText(R.string.app_name),
                getCurrMedia() == null ? "" : getCurrMedia().getTitle(),
                pi);
        note.flags|=Notification.FLAG_NO_CLEAR;
        return note;
    }

    private void notifyClients(Notify notification, Bundle params)
    {
        final Set<Messenger> clientsToRemove = new HashSet<Messenger>();
        for(Messenger client : clients)
        {
            try {
                Message msg = Message.obtain(null, notification.ordinal());
                msg.setData(params);
                client.send(msg);
            } catch (RemoteException e) {
                // If we get here, the client is dead, and we should remove it from the list
                Logger.print("Client does not respond, remove it");
                clientsToRemove.add(client);
            }
        }
        for(Messenger client : clientsToRemove)
        {
            clients.remove(client);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }



    public void createMediaPlayerInstance(){
        mp = new MediaPlayer();
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
        Logger.print("Media Player Instance is created");
    }

}

