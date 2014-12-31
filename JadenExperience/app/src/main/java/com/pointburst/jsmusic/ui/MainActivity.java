package com.pointburst.jsmusic.ui;

import android.os.Bundle;
import android.view.View;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.adapter.VerticalViewPagerAdapter;
import com.pointburst.jsmusic.constant.Constants;
import com.pointburst.jsmusic.model.Result;
import com.pointburst.jsmusic.ui.widgets.VerticalViewPager;
import android.support.v4.app.FragmentManager;


/**
 * Created by FARHAN on 12/27/2014.
 */
public class MainActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Result result = (Result)getIntent().getSerializableExtra(Constants.RESULT_KEY);

        ((VerticalViewPager) findViewById(R.id.vert_view_pager)).setAdapter(new VerticalViewPagerAdapter(getSupportFragmentManager(),result.getAlbums().get(0).getMedias()));
        ((VerticalViewPager) findViewById(R.id.vert_view_pager)).setOverScrollMode(View.OVER_SCROLL_ALWAYS);
    }
}
