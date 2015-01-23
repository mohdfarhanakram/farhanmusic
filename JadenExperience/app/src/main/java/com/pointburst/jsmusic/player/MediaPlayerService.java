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
import android.widget.Toast;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.constant.Constants;
import com.pointburst.jsmusic.events.MediaEvent;
import com.pointburst.jsmusic.listener.PMediaPlayerListener;
import com.pointburst.jsmusic.model.Album;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.model.Result;
import com.pointburst.jsmusic.network.ServiceResponse;
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

    private PMediaPlayer mediaPlayer;
    private Notification mNotification;
    private RemoteViews remoteViews;
    private ArrayList<Media> mMediaArrayList = new ArrayList<Media>();
    private int mCurrentMediaIndex = 0;
    private boolean isShuffle;
    private boolean isMute;
    private boolean playerInBackGround;
    private PMediaPlayerListener mListener;

    Binder playerServiceBinder = new MediaPlayerBinder();



    @Override
    public IBinder onBind(Intent intent) {
        Logger.print("Bind service");
        return playerServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.print("onUnbind service");
        startNotification(mMediaArrayList.get(mCurrentMediaIndex));
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Logger.print("Re Bind service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createMediaPlayerInstance();

    }

    public class MediaPlayerBinder extends Binder {

        public MediaPlayerService getMediaPlayerService(ArrayList<Media> mediaArrayList,int currentMediaIndex,PMediaPlayerListener listener) {

            mMediaArrayList = mediaArrayList;
            mListener = listener;
            mCurrentMediaIndex = currentMediaIndex;
            Logger.print("Musics Player Binder get service");
            //playSong(mCurrentMediaIndex);
            stopNotification();
            return MediaPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(Constants.ACTION_PLAY_PAUSE)) {

                    togglePlayPause();

                } else if (action.equals(Constants.ACTION_PREV)) {
                    playPrev();
                } else if (action.equals(Constants.ACTION_NEXT)) {
                    //isManualPause = false;
                    playNext();
                } else if (action.equals(Constants.ACTION_CLOSE)) {
                    mediaPlayer.release();
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
        if(mListener!=null)
           mListener.onError();

        if(mp!=null) {
            mp.reset();
            mp=null;
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(mListener!=null)
           mListener.onInitializePlayerSuccess();

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

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }


    }

    public void toggleVolume() {
        Logger.print("MediaPlayerService: togglevolume");
        if (mediaPlayer != null) {
            if (isMute)
                mediaPlayer.setVolume(1.0f, 1.0f);
            else
                mediaPlayer.setVolume(0.0f, 0.0f);

            isMute = !isMute;
        }
    }

    public void resetPlayer(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    public void playSong(int currentMediaIndex){
        if(mListener!=null)
            mListener.onInitializePlayerStart("");
        if(mCurrentMediaIndex>=0 && mCurrentMediaIndex<mMediaArrayList.size()){

           /* if(mp.isPlaying()&& currentMediaIndex==mCurrentMediaIndex){
                Logger.print("Media Player is playing same song.");
                return;
            }*/



            //resetPlayer();

            mCurrentMediaIndex = currentMediaIndex;
            Media media = mMediaArrayList.get(mCurrentMediaIndex);

           if(!StringUtils.isNullOrEmpty(media.getStreamUrl())){   //if media instance is having stream URL;
               prepareMediaPlayer(media);
               //startNotification(media);

           }else{
               fetchMedia(media.getMediaKey());  // hit request to get Stream URL;
           }

        }
    }

    public void createMediaPlayerInstance(){
        mediaPlayer = new PMediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        Logger.print("Media Player Instance is created");
    }

    public void prepareMediaPlayer(final Media media) {

            Logger.print("Media Stream URL_1: " + media.getStreamUrl());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(media.getStreamUrl());
                        mediaPlayer.prepareAsync();
                    } catch (IllegalArgumentException e) {
                        Logger.print("Media Player Exception: " + e.getMessage());
                    } catch (IllegalStateException e) {
                        Logger.print("Media Player Exception: " + e.getMessage());
                    } catch (IOException e) {
                        Logger.print("Media Player Exception: " + e.getMessage());
                    }
                }
            }).start();

            Logger.print("Media Stream URL_2: " + media.getStreamUrl());

    }

    public void play(){

        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }

        //startNotification(mMediaArrayList.get(mCurrentMediaIndex));


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
        if (mediaPlayer.isPlaying())
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
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
  			     if(mediaPlayer!= null) mediaPlayer.start();
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //if (mediaPlayer.isPlaying()) mp.pause();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    @Override
    public void onDestroy() {onCreate();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mediaPlayer.release();
        mediaPlayer = null;

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

    public void stopMediaPlayer() {
        stopForeground(true);
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void resetMediaPlayer() {
        stopForeground(true);
        mediaPlayer.reset();
    }



    @Override
    public void onResponse(Object response) {
        ServiceResponse serviceResponse = (ServiceResponse)response;
        if(serviceResponse.isSuccess()){
            onGetMedia((Media)serviceResponse.getResponse());
        }else{
            Toast.makeText(this, serviceResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
            if(mListener!=null)
                mListener.onError();
        }

    }

    public void setJSMediaPlayerListener(PMediaPlayerListener listener){

        mListener = listener;
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
