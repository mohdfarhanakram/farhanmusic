package com.pointburst.jsmusic.listener;

import android.widget.ScrollView;

/**
 * Created by FARHAN on 1/3/2015.
 */
public interface ScrollViewListener {
    void onScrollChanged(ScrollView scrollView,
                         int x, int y, int oldx, int oldy);
}
