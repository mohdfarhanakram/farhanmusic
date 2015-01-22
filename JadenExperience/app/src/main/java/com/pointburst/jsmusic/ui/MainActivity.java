package com.pointburst.jsmusic.ui;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.adapter.MediaListAdapter;
import com.pointburst.jsmusic.adapter.MediaPagerAdapter;
import com.pointburst.jsmusic.constant.Constants;
import com.pointburst.jsmusic.events.BindMusicService;
import com.pointburst.jsmusic.listener.JSMediaPlayerListener;
import com.pointburst.jsmusic.model.Album;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.model.Result;
import com.pointburst.jsmusic.network.ServiceResponse;
import com.pointburst.jsmusic.player.MediaPlayerService;
import com.pointburst.jsmusic.ui.widgets.SlideLayout;
import com.pointburst.jsmusic.utils.Logger;
import de.greenrobot.event.EventBus;

import java.net.URLDecoder;
import java.util.ArrayList;


/**
 * Created by FARHAN on 12/27/2014.
 */
public class MainActivity extends BaseActivity implements JSMediaPlayerListener{

    private MediaPlayer mediaPlayer;

    private ArrayList<Media> mediaArrayList = new ArrayList<Media>();
    private boolean musicBound = false;

    private MediaPlayerService mPlayerService;
    private int mCurrentMediaIndex;

    private RemoteViews remoteViews;
    private Notification mNotification;



    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.print("OnServiceConnected called");
            MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;
            //get service
            mPlayerService = binder.getMediaPlayerService(mediaArrayList,mCurrentMediaIndex,MainActivity.this);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_layout);

        Result result = (Result)getIntent().getSerializableExtra(Constants.RESULT_KEY);
        mediaArrayList = result.getAlbums().get(0).getMedias();
        mCurrentMediaIndex = getIntent().getIntExtra(Constants.CURRENT_MEDIA_INDEX,0);

        ((ViewPager)findViewById(R.id.media_view_pager)).setAdapter(new MediaPagerAdapter(getSupportFragmentManager(),this,mediaArrayList));
        ((ViewPager)findViewById(R.id.media_view_pager)).setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                updateLyric(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        onUpdateMpUi(mCurrentMediaIndex);
        setSongListAdapter();
    }

    private void setSongListAdapter(){
        ListView listView = (ListView)findViewById(R.id.media_list_view);
        listView.setAdapter(new MediaListAdapter(this, mediaArrayList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SlideLayout)findViewById(R.id.slider_layout)).closeBottomMenu(true);
                onUpdateMpUi(position);
            }
        });

    }

    @Override
    public void updateUi(ServiceResponse response) {
        super.updateUi(response);
    }




    public void onEvent(Media media){

    }

    public void onEvent(BindMusicService service){
        mCurrentMediaIndex = service.getCurrentMediaIndex();
        bindMusicService();
    }

    /*public void onEvent(ServiceClosedEvent event){
        Logger.print("Receive event from Media service that it was closed and has to be restarted");
        *//*if (musicBound) {
            Logger.print("unbinding");
            unbindService(musicConnection);
            musicBound = false;
        }*//*
        bindMusicService();
        EventBus.getDefault().register(this);
    }*/

    @Override
    public void onStop() {

        if (musicBound) {
            Logger.print("unbinding");
            unbindService(musicConnection);
            musicBound = false;
        }

        super.onStop();
    }

    private void bindMusicService() {

        Intent playIntent = new Intent(this, MediaPlayerService.class);

        if (iSMediaPlayerServiceRunning()) {
            //bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
        else {
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }



    }

    /** Determines if the MediaPlayerService is already running.
     * @return true if the service is running, false otherwise.
     */
    private boolean iSMediaPlayerServiceRunning() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.pointburst.jsmusic.player.MediaPlayerService".equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onUpdateMpUi(int index) {
        ((ViewPager)findViewById(R.id.media_view_pager)).setCurrentItem(index);
        updateLyric(index);
    }

    private void updateLyric(int index){
        String lyrics = mediaArrayList.get(index).getLyrics();
        try {
            lyrics = URLDecoder.decode(lyrics,"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.lyrics_txt_view)).setText(lyrics);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.play_img_btn:
                break;
            case R.id.next_img_btn:
                break;
            case R.id.prev_img_btn:
                break;
            case R.id.suffle_img_btn:
                break;
            case R.id.repeat_img_btn:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.print("onResume called");
        EventBus.getDefault().register(this);
        bindMusicService();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    private void createNotification(Media media, int index,boolean isPlaying) {

        remoteViews = new RemoteViews(getPackageName(), R.layout.song_notification);
        // Open NotificationView Class on Notification Click
        Intent intentStartActivity = new Intent(this, MainActivity.class);
        intentStartActivity.putExtra(Constants.CURRENT_MEDIA_INDEX,index);
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
        if (isPlaying)
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
        mPlayerService.startForeground(Constants.NOTIFICATION_ID, mNotification);
    }

    public void startNotification(Media media, int index,boolean isPlaying) {
        Logger.print("Start Notification");
        createNotification(media,index,isPlaying);
    }

    public void stopNotification() {
        Logger.print("Stopping Notification");
        mPlayerService.stopForeground(true);
        mNotification = null;
    }

    private Result getResultInstance(){

        Result result = new Result();
        Album album = new Album();
        album.setMedias(mediaArrayList);
        result.getAlbums().add(album);

        return result;
    }





}
