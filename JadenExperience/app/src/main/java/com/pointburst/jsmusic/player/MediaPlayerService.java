package com.pointburst.jsmusic.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.constant.Constants;
import com.pointburst.jsmusic.events.MediaEvent;
import com.pointburst.jsmusic.listener.JSMediaPlayerListener;
import com.pointburst.jsmusic.model.Album;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.model.Result;
import com.pointburst.jsmusic.ui.MainActivity;
import com.pointburst.jsmusic.utils.Logger;
import com.pointburst.jsmusic.utils.StringUtils;
import de.greenrobot.event.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by FARHAN on 1/2/2015.
 */
public class MediaPlayerService extends BaseService {

    private MediaPlayer mp;
    private Notification mNotification;

    private ArrayList<Media> mMediaArrayList = new ArrayList<Media>();
    private int mCurrentMediaIndex = 0;
    private boolean isShuffle;
    private boolean isMute;
    private boolean playerInBackGround;

    private JSMediaPlayerListener mListener;

    Binder playerServiceBinder = new MediaPlayerBinder();



    @Override
    public IBinder onBind(Intent intent) {
        Logger.print("Bind service");

        return playerServiceBinder;
    }


    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getMediaPlayerService(ArrayList<Media> mediaArrayList,int currentMediaIndex,JSMediaPlayerListener listener) {
            mMediaArrayList = mediaArrayList;
            mListener = listener;
            mCurrentMediaIndex = currentMediaIndex;
            Logger.print("Musics Player Binder get service");
            playSong(mCurrentMediaIndex);
            return MediaPlayerService.this;
        }
    }

    public void setJSMediaPlayerListener(JSMediaPlayerListener listener){
        mListener = listener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mp == null) {
            createMediaPlayerInstance();

        }

        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(Constants.ACTION_PLAY_PAUSE)) {
                   /* if (mp.isPlaying())
                        isManualPause = mp.isPlaying();*/
                    togglePlayPause();


                } else if (action.equals(Constants.ACTION_PREV)) {
                    playPrev();
                    updatePlayerOnUI();
                } else if (action.equals(Constants.ACTION_NEXT)) {
                    //isManualPause = false;
                    playNext();
                    updatePlayerOnUI();
                } else if (action.equals(Constants.ACTION_CLOSE)) {

                    /*ServiceClosedEvent event = new ServiceClosedEvent();
                    EventBus.getDefault().post(event);*/

                    mp.release();
                    stopNotification();
                    stopSelf();
                }


            }
        }





        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        //if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Logger.print("Song is completed");
       // check for repeat is ON or OFF
        if (isShuffle) {
            // shuffle is on - play a random song
            playSong(doShuffle());

        } else {
            //TODO need to handle repeat
            Logger.print("Play next song.");
            // no repeat or shuffle OFF - play next song
            if (mCurrentMediaIndex < (mMediaArrayList.size() - 1)) {
                playSong(mCurrentMediaIndex + 1);
            } else {
                // play first song
                playSong(0);
            }
            updatePlayerOnUI();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if(mp!=null) {
            mp.reset();
            mp=null;
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
      play();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
         Logger.print("Percentage Mp3 Download "+percent);
    }

    public void onEvent(MediaEvent mediaEvent){
        switch (mediaEvent.getType()) {
            case MediaEvent.PLAY_PAUSE:
                togglePlayPause();
                break;
            case MediaEvent.PLAY_NEXT:
                playNext();
                break;
            case MediaEvent.PLAY_PREV:
                playPrev();
                break;
            case MediaEvent.MUTE:
                toggleVolume();
                break;
            case MediaEvent.SHUFFLE:
                toggleShuffle();
                break;
            case MediaEvent.PLAY_SONG_AT_INDEX:
                playSong(mediaEvent.getIndex());
                break;


        }

    }


    public void toggleShuffle() {

        Logger.print("MediaPlayerService: toggle shuffle");
        isShuffle = !isShuffle;
    }

    public void playNext() {
        Logger.print("MediaPlayerService: playNext");
        if (isShuffle) {
            playSong(doShuffle());
        }

        if ((mCurrentMediaIndex + 1) > (mMediaArrayList.size() - 1))
            playSong(0);
        else
            playSong(mCurrentMediaIndex + 1);
    }

    private int doShuffle() {
        Random rand = new Random();
        return rand.nextInt((mMediaArrayList.size() - 1) + 1);
    }

    public void playPrev() {
        Logger.print("MediaPlayerService: playPrev");
        if ((mCurrentMediaIndex - 1) < 0)
            playSong(mMediaArrayList.size() - 1);
        else
            playSong(mCurrentMediaIndex - 1);
    }

    public void togglePlayPause() {
        Logger.print("MediaPlayerService: Toggleplaypause");

        if (mp.isPlaying()) {
            mp.pause();
        } else {
            mp.start();
        }

        startNotification(mMediaArrayList.get(mCurrentMediaIndex));
    }

    public void toggleVolume() {
        Logger.print("MediaPlayerService: togglevolume");
        if (mp != null) {
            if (isMute)
                mp.setVolume(1.0f, 1.0f);
            else
                mp.setVolume(0.0f, 0.0f);

            isMute = !isMute;
        }
    }

    public void resetPlayer(){
        if(mp.isPlaying()){
            mp.stop();
            mp.reset();
        }
    }

    public void playSong(int currentMediaIndex){

        if(mCurrentMediaIndex>=0 && mCurrentMediaIndex<mMediaArrayList.size()){

            if(mp.isPlaying()&& currentMediaIndex==mCurrentMediaIndex){
                Logger.print("Media Player is playing same song.");
                return;
            }


            //resetPlayer();

            mCurrentMediaIndex = currentMediaIndex;
            Media media = mMediaArrayList.get(mCurrentMediaIndex);

           if(!StringUtils.isNullOrEmpty(media.getStreamUrl())){   //if media instance is having stream URL;
               prepareMediaPlayer(media);
               startNotification(media);
           }else{
               fetchMedia(media.getMediaKey());  // hit request to get Stream URL;
           }



        }
    }

    public void createMediaPlayerInstance(){
        mp = new MediaPlayer();
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
        mp.setOnBufferingUpdateListener(this);
        Logger.print("Media Player Instance is created");
    }

    public void prepareMediaPlayer(Media media) {
        try {

            if(mp==null)
                createMediaPlayerInstance();

            Logger.print("Media Stream URL: " + media.getStreamUrl());
            mp.reset();
            mp.setDataSource(media.getStreamUrl());
            mp.prepareAsync();

        } catch (IllegalArgumentException e) {
            Logger.print("Media Player Exception: " + e.getMessage());
        } catch (IllegalStateException e) {
            Logger.print("Media Player Exception: " + e.getMessage());
        } catch (IOException e) {
            Logger.print("Media Player Exception: " + e.getMessage());
        }
    }

    public void play(){

        if(!mp.isPlaying())
            mp.start();

        startNotification(mMediaArrayList.get(mCurrentMediaIndex));

    }




    private void createNotification(Media media) {

        remoteViews = new RemoteViews(getPackageName(), R.layout.song_notification);
        // Open NotificationView Class on Notification Click
        Intent intentStartActivity = new Intent(this, MainActivity.class);
        intentStartActivity.putExtra(Constants.CURRENT_MEDIA_INDEX,mCurrentMediaIndex);
        intentStartActivity.putExtra(Constants.RESULT_KEY,getResultInstance());

        // Send data to NotificationView Class
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intentStartActivity,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_album_art, R.drawable.thejadenexperiencehighq);
        remoteViews.setTextViewText(R.id.tv_title, media.getTitle());

        //Previous button action
         Intent intentActionPrev = new Intent(Constants.ACTION_PREV);
         PendingIntent piActionPrev = PendingIntent.getService(getApplicationContext(),
                 Constants.REQUEST_CODE_ACTION, intentActionPrev,
                    PendingIntent.FLAG_UPDATE_CURRENT);
         remoteViews.setOnClickPendingIntent(R.id.iv_prev, piActionPrev);

        //PlayPause button action
        Intent intentActionPlayPause = new Intent(Constants.ACTION_PLAY_PAUSE);
        PendingIntent piActionPlayPause = PendingIntent.getService(getApplicationContext(),
                    Constants.REQUEST_CODE_ACTION, intentActionPlayPause,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, piActionPlayPause);
        if (mp.isPlaying())
            remoteViews.setImageViewResource(R.id.iv_play_pause, R.drawable.ic_pause);
        else
            remoteViews.setImageViewResource(R.id.iv_play_pause, R.drawable.ic_play);

        //Next button action
        Intent intentActionNext = new Intent(Constants.ACTION_NEXT);
        PendingIntent piActionNext = PendingIntent.getService(getApplicationContext(),
                    Constants.REQUEST_CODE_ACTION, intentActionNext,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.iv_next, piActionNext);

        //Close button action

        Intent intentActionClose = new Intent(Constants.ACTION_CLOSE);
        PendingIntent piActionClose = PendingIntent.getService(getApplicationContext(),
                    Constants.REQUEST_CODE_ACTION, intentActionClose,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.iv_close, piActionClose);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(media.getTitle());
        // Set Ticker Message
        builder.setTicker(media.getTitle());
        // Dismiss Notification
        builder.setAutoCancel(true);
        // Set PendingIntent into Notification
        builder.setContentIntent(pIntent);
        // Set RemoteViews into Notification
        builder.setContent(remoteViews);
        mNotification = builder.build();
        mNotification.flags = Notification.FLAG_NO_CLEAR
                | Notification.FLAG_ONGOING_EVENT;
        startForeground(Constants.NOTIFICATION_ID, mNotification);
    }

    public void startNotification(Media media) {
        Logger.print("Start Notification");
        createNotification(media);
    }

    public void stopNotification() {
        Logger.print("Stopping Notification");
        stopForeground(true);
        mNotification = null;
    }

    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                if (mp.isPlaying()) mp.pause();
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
//				if(mp!= null) mp.start();
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                if (mp.isPlaying()) mp.pause();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    @Override
    public void onDestroy() {onCreate();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mp.release();
        mp = null;

        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void onGetMedia(Media media) {

        int index = mMediaArrayList.indexOf(media);
        if (index != -1 && media != null) {
            mMediaArrayList.set(index, media);

        }
        if(index == mCurrentMediaIndex){
            playSong(index);
        }
    }

    public boolean isPlayerInBackGround() {
        return playerInBackGround;
    }

    public void setPlayerInBackGround(boolean playerInBackGround) {
        this.playerInBackGround = playerInBackGround;
    }


    private Result getResultInstance(){

        Result result = new Result();
        Album album = new Album();
        album.setMedias(mMediaArrayList);
        result.getAlbums().add(album);

        return result;
    }


    private void updatePlayerOnUI(){
        if(mListener!=null){
            mListener.onUpdateMpUi(mCurrentMediaIndex);
        }
    }

    /*private int getMediaStatus(){
        if(mp!=null){
            if(mp.isPlaying())
                return Constants.PLAYING;
            if(mp.isP)
        }else{

        }
    }*/
}
