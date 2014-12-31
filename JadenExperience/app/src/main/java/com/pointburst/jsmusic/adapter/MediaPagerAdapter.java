package com.pointburst.jsmusic.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.pointburst.jsmusic.listener.JSMediaPlayerListener;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.ui.fragment.MediaFragment;
import com.pointburst.jsmusic.ui.fragment.MediaPlayerFragment;
import com.pointburst.jsmusic.ui.fragment.SongListFragment;

import java.util.ArrayList;

/**
 * Created by m.farhan on 12/30/14.
 */
public class MediaPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Media> mMediaArrayList;
    private Context mContext;
    private JSMediaPlayerListener mListener;

    public MediaPagerAdapter(FragmentManager fm,Context context,ArrayList<Media> mediaArrayList,JSMediaPlayerListener listener) {
        super(fm);
        mMediaArrayList = mediaArrayList;
        mContext = context;
        mListener = listener;
    }

    @Override
    public Fragment getItem(int i) {

        Media media = mMediaArrayList.get(i);
        MediaFragment fragment = new MediaFragment();
        fragment.setMedia(media);
        fragment.setJSMediaPlayerListener(mListener);
        return fragment;

    }

    @Override
    public int getCount() {
        return mMediaArrayList==null?0:mMediaArrayList.size();
    }
}
