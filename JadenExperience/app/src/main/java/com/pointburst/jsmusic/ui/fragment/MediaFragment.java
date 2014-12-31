package com.pointburst.jsmusic.ui.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.utils.Logger;
import com.pointburst.jsmusic.utils.PicassoEx;

/**
 * Created by m.farhan on 12/30/14.
 */
public class MediaFragment extends MediaBaseFragment{

    private View mView;
    private Media mMedia;
    private ImageView mMediaImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_media,container,false);
        mMediaImageView = (ImageView)mView.findViewById(R.id.media_img_view);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        picassoLoad(mMedia.getArtImageUrl(),mMediaImageView);
    }

    public void setMedia(Media media){
        mMedia = media;
    }




    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(mView!=null){
                mListener.onPlaySong(-1,-1,mMedia);

            }
        }
        else{
            if(mView!=null){
                mListener.onStopSong();
            }
        }
    }

}
