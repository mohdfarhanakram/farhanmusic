package com.pointburst.jsmusic.ui;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.adapter.VerticalViewPagerAdapter;
import com.pointburst.jsmusic.constant.Constants;
import com.pointburst.jsmusic.listener.JSMediaPlayerListener;
import com.pointburst.jsmusic.model.Result;
import com.pointburst.jsmusic.ui.widgets.VerticalViewPager;
import android.support.v4.app.FragmentManager;
import com.pointburst.jsmusic.utils.Logger;


/**
 * Created by FARHAN on 12/27/2014.
 */
public class MainActivity extends BaseActivity implements JSMediaPlayerListener {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Result result = (Result)getIntent().getSerializableExtra(Constants.RESULT_KEY);

        ((VerticalViewPager) findViewById(R.id.vert_view_pager)).setAdapter(new VerticalViewPagerAdapter(getSupportFragmentManager(),result.getAlbums().get(0).getMedias(),this));
        ((VerticalViewPager) findViewById(R.id.vert_view_pager)).setOverScrollMode(View.OVER_SCROLL_ALWAYS);
    }

    @Override
    public void onPlaySong(int albumIndex,int mediaIndex,Object object) {
        playAudio("");
    }

    @Override
    public void onStopSong() {
        destroyMediaPlayer();
    }


    private void playAudio(String url){
        url = "https://pub2-o.secure.miisolutions.net/mi/pointburst/Stoopid Rich Produced by Tone Jonez_audio_128K.mp3";
        try{
            /*((BaseActivity)getActivity()).showProgressDialog();
            ((Button)mView.findViewById(R.id.btn_play_audio)).setEnabled(false);*/

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // Listen for if the audio file can't be prepared
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    /*((BaseActivity)getActivity()).removeProgressDialog();
                    ((Button)mView.findViewById(R.id.btn_play_audio)).setEnabled(true);*/
                    // ... react appropriately ...
                    // The MediaPlayer has moved to the Error state, must be reset!
                    showToast("Error...");
                    return false;
                }
            });
            // Attach to when audio file is prepared for playing
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    /*((BaseActivity)getActivity()).removeProgressDialog();
                    ((Button)mView.findViewById(R.id.btn_play_audio)).setEnabled(false);*/
                    showToast("Song is started");
                    mediaPlayer.start();
                }

            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //((Button)mView.findViewById(R.id.btn_play_audio)).setEnabled(true);
                    showToast("Song is completed");
                }
            });

            // Set the data source to the remote URL
            mediaPlayer.setDataSource(url);
            // Trigger an async preparation which will file listener when completed
            mediaPlayer.prepareAsync();


        }catch(Exception e){
            Logger.print(e.getMessage());
            e.printStackTrace();
        }


    }


    private void destroyMediaPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

}
