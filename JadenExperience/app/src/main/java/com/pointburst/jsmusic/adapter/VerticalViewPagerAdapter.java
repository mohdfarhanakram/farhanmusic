package com.pointburst.jsmusic.adapter;


import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.pointburst.jsmusic.listener.JSMediaPlayerListener;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.ui.fragment.BaseFragment;
import com.pointburst.jsmusic.ui.fragment.MediaPlayerFragment;
import com.pointburst.jsmusic.ui.fragment.SongListFragment;

import java.util.ArrayList;

/**
 * Created by FARHAN on 12/30/2014.
 */
public class VerticalViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Media> mMediaArrayList;
    private JSMediaPlayerListener mListener;

    public VerticalViewPagerAdapter(FragmentManager fm,ArrayList<Media> mediaArrayList,JSMediaPlayerListener listener) {
        super(fm);
        mMediaArrayList = mediaArrayList;
        mListener = listener;
    }

    @Override
    public Fragment getItem(int i) {
        BaseFragment fragment = null;
        switch (i){
            case 0:
               fragment = new MediaPlayerFragment();

                break;
            case 1:
                fragment = new SongListFragment();

                break;
            default:
                fragment = new MediaPlayerFragment();
        }
        fragment.setMediaList(mMediaArrayList);
        fragment.setJSMediaPlayerListener(mListener);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
