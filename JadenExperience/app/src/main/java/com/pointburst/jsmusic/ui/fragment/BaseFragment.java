package com.pointburst.jsmusic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.utils.PicassoEx;

import java.util.ArrayList;

/**
 * Created by m.farhan on 12/30/14.
 */
public class BaseFragment extends Fragment{
    public ArrayList<Media> mMediaArrayList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setMediaList(ArrayList<Media> mediaList){
        mMediaArrayList = mediaList;
    }

    public void picassoLoad(String url, ImageView imageView) {

        PicassoEx.getPicasso(getActivity()).load(url).fit().into(imageView);

    }
}
