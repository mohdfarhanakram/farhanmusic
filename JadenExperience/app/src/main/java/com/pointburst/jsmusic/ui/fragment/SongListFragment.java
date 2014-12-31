package com.pointburst.jsmusic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.adapter.MediaListAdapter;
import com.pointburst.jsmusic.model.Media;

import java.util.ArrayList;

/**
 * Created by m.farhan on 12/30/14.
 */
public class SongListFragment extends BaseFragment{

    private View mView;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_song_list,container,false);
        mListView = (ListView)mView.findViewById(R.id.media_list_view);
        mListView.setAdapter(new MediaListAdapter(getActivity(),mMediaArrayList));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Media media = mMediaArrayList.get(position);
                mListener.onPlaySong(-1,position,media);
            }
        });
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
