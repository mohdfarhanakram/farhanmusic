package com.pointburst.jsmusic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.ui.BaseActivity;
import com.pointburst.jsmusic.utils.PicassoEx;

import java.util.ArrayList;

/**
 * Created by m.farhan on 12/30/14.
 */
public class BaseFragment extends Fragment {
    public ArrayList<Media> mMediaArrayList;


    public Animation animShowFromTop;
    public Animation animHideToTop;
    public Animation animShowFromBottom;
    public Animation animHideToBottom;

    public int mCurrentIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setMediaList(ArrayList<Media> mediaList){
        mMediaArrayList = mediaList;
    }

    public void setCurrentMediaIndex(int index){
        mCurrentIndex = index;
    }

    public int getCurrentMediaIndex(){
        return mCurrentIndex;
    }

    /*public void setJSMediaPlayerListener(JSMediaPlayerListener listener){
        mListener = listener;
    }*/

    public void picassoLoad(String url, ImageView imageView) {

        PicassoEx.getPicasso(getActivity()).load(url).error(R.drawable.def).placeholder(R.drawable.def).fit().into(imageView);

    }

    public void showToast(String msg){
        ((BaseActivity)getActivity()).showToast(msg);
    }


    public void initAnimation(){
        animShowFromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down_from_top);
        animHideToTop = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_to_top);
        animShowFromBottom = AnimationUtils
                .loadAnimation(getActivity(), R.anim.slide_up_from_bottom);
        animHideToBottom = AnimationUtils
                .loadAnimation(getActivity(), R.anim.slide_down_to_bottom);
    }


}
