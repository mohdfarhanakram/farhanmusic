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
import android.widget.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.adapter.MediaListAdapter;
import com.pointburst.jsmusic.adapter.MediaPagerAdapter;
import com.pointburst.jsmusic.constant.Constants;
import com.pointburst.jsmusic.events.BindMusicService;
import com.pointburst.jsmusic.listener.PMediaPlayerListener;
import com.pointburst.jsmusic.model.Album;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.model.Result;
import com.pointburst.jsmusic.network.ServiceResponse;
import com.pointburst.jsmusic.player.MediaPlayerService;
import com.pointburst.jsmusic.player.PMediaPlayer;
import com.pointburst.jsmusic.ui.widgets.SlideLayout;
import com.pointburst.jsmusic.utils.Logger;
import de.greenrobot.event.EventBus;

import java.net.URLDecoder;
import java.util.ArrayList;


/**
 * Created by FARHAN on 12/27/2014.
 */

public class MainActivity extends BaseActivity implements PMediaPlayerListener{

    private MediaPlayerService mPlayerService;
    private ArrayList<Media> mediaArrayList = new ArrayList<Media>();
    private boolean musicBound = false;
    private int mCurrentMediaIndex;


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
            Logger.print("onServiceDisconnected called");
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

        ((ViewPager)findViewById(R.id.media_view_pager)).setOffscreenPageLimit(mediaArrayList.size());
        onUpdateMpUi(mCurrentMediaIndex);
        setSongListAdapter();

        findViewById(R.id.playPauseButton).setOnClickListener(this);
        findViewById(R.id.nextButton).setOnClickListener(this);
        findViewById(R.id.previousButton).setOnClickListener(this);

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
            Logger.print("unboinding");
            unbindService(musicConnection);
            musicBound = false;
        }*//*
        bindMusicService();
        EventBus.getDefault().register(this);
    }*/



    private void bindMusicService() {

       if (musicBound) {
           Logger.print("un Bind from resume");
           unbindService(musicConnection);
           musicBound = false;
       }

        Intent playIntent = new Intent(this, MediaPlayerService.class);

        if (iSMediaPlayerServiceRunning() && !musicBound) {
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
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
    public void onInitializePlayerStart(String message) {
       findViewById(R.id.loader_layout).setVisibility(View.GONE);
        changePlayPauseBtn(false);
    }

    @Override
    public void onInitializePlayerSuccess() {
        findViewById(R.id.loader_layout).setVisibility(View.GONE);
        changePlayPauseBtn(true);
    }

    @Override
    public void onError() {
        findViewById(R.id.loader_layout).setVisibility(View.GONE);
        changePlayPauseBtn(false);
    }

    @Override
    public void onUpdateMpUi(int index) {
        mCurrentMediaIndex = index;

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
            case R.id.playPauseButton:
                /*if(mPlayerService.getMediaPlayer()==null)
                    return;
                if(mPlayerService.getMediaPlayer().isPlaying()) {
                    changePlayPauseBtn(false);
                    mPlayerService.getMediaPlayer().pause();
                }else {
                    changePlayPauseBtn(true);
                    mPlayerService.getMediaPlayer().start();
                }*/
                break;
            case R.id.nextButton:
                int ind  = mCurrentMediaIndex + 1;
                if(mediaArrayList.size()==ind)
                    ind = 0;
                onUpdateMpUi(ind);
                break;
            case R.id.previousButton:
                if(mCurrentMediaIndex == 0)
                    mCurrentMediaIndex = mediaArrayList.size()-1;
                onUpdateMpUi(mCurrentMediaIndex);
                break;
            case R.id.shuffleButton:
                break;
            case R.id.repeatButton:
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
       /*if(mPlayerService!=null){
            Media media = mediaArrayList.get(mCurrentMediaIndex);
            mPlayerService.startNotification(media);

       }*/
    }


    private void changePlayPauseBtn(boolean isPlaying) {
        if ( isPlaying)
            ((ImageButton) findViewById(R.id.playPauseButton)).setImageResource(R.drawable.ic_action_pause);
        else
            ((ImageButton) findViewById(R.id.playPauseButton)).setImageResource(R.drawable.ic_action_play);
    }

           @Override
    protected void onResume() {
        super.onResume();
        Logger.print("onResume called");
        EventBus.getDefault().register(this);
        bindMusicService();
    }




}
