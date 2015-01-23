package com.pointburst.jsmusic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.events.MediaEvent;
import com.pointburst.jsmusic.model.Media;
import de.greenrobot.event.EventBus;

/**
 * Created by m.farhan on 12/30/14.
 */
public class MediaFragment extends MediaBaseFragment {

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
        ((TextView)mView.findViewById(R.id.tv_title)).setText(mMedia.getTitle());
        picassoLoad(mMedia.getArtImageUrl(), mMediaImageView);
    }

    private void setBusEvent(int index){
        MediaEvent mediaEvent = new MediaEvent();
        mediaEvent.setIndex(index);
        mediaEvent.setType(MediaEvent.PLAY_SONG_AT_INDEX);
        EventBus.getDefault().postSticky(mediaEvent);

    }

    public void setMedia(Media media){
        mMedia = media;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Object o){

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setBusEvent(getCurrentMediaIndex());
        }
        else{

        }
    }




}
