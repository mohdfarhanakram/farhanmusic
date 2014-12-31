package com.pointburst.jsmusic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.adapter.MediaPagerAdapter;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.ui.MainActivity;

import java.util.ArrayList;

/**
 * Created by m.farhan on 12/30/14.
 */
public class MediaPlayerFragment extends BaseFragment{
    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_media_player,container,false);
        ((ViewPager)mView.findViewById(R.id.media_view_pager)).setAdapter(new MediaPagerAdapter(getChildFragmentManager(),getActivity(),mMediaArrayList));
        ((ViewPager)mView.findViewById(R.id.media_view_pager)).setOffscreenPageLimit(mMediaArrayList==null?0:mMediaArrayList.size());
        return mView;
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(mView!=null)
              mView.findViewById(R.id.player_view).setVisibility(View.VISIBLE);
        }
        else{
            if(mView!=null)
               mView.findViewById(R.id.player_view).setVisibility(View.GONE);
        }
    }
}
