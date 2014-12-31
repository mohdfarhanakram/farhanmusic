package com.pointburst.jsmusic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.constant.ApiEvent;
import com.pointburst.jsmusic.constant.Constants;
import com.pointburst.jsmusic.constant.ServerApi;
import com.pointburst.jsmusic.model.Result;
import com.pointburst.jsmusic.network.ServiceResponse;

/**
 * Created by FARHAN on 12/27/2014.
 */


public class SplashActivity extends BaseActivity {

    private long duration = 3000;

    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

       fetchData(ServerApi.ALBUM_URL, ApiEvent.GET_MEDIA_EVENT,null,null,this,this,this,false);
    }

    @Override
    public void updateUi(ServiceResponse response) {
        super.updateUi(response);
        Result result = (Result)response.getResponse();
        if(result!=null){
            navigateToHomeScreen(result);

        }else{
           showToast(response.getErrorMessage());
        }

    }

    private void navigateToHomeScreen(final Result result){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                mainIntent.putExtra(Constants.RESULT_KEY,result);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, duration);
    }
}

