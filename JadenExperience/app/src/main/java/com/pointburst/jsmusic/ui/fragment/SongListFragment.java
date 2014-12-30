package com.pointburst.jsmusic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.pointburst.jsmusic.R;

/**
 * Created by m.farhan on 12/30/14.
 */
public class SongListFragment extends BaseFragment{

    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_song_list,container,false);
        return mView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Toast.makeText(getActivity(), "Song list is visible", Toast.LENGTH_SHORT).show();
        }
        else{

        }
    }
}
