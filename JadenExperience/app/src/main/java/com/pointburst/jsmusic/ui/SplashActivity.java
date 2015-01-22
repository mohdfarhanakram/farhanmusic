package com.pointburst.jsmusic.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.constant.ApiEvent;
import com.pointburst.jsmusic.constant.Constants;
import com.pointburst.jsmusic.constant.ServerApi;
import com.pointburst.jsmusic.model.Result;
import com.pointburst.jsmusic.network.ServiceResponse;
import com.pointburst.jsmusic.utils.StringUtils;

/**
 * Created by FARHAN on 12/27/2014.
 */


public class SplashActivity extends BaseActivity {

    private long duration = 3000;;

    private int mCurrentMediaIndex = 0;

    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hitRequestForAlbum();
    }
    @Override
    public void updateUi(ServiceResponse response) {
        findViewById(R.id.loader).setVisibility(View.GONE);
        super.updateUi(response);
        Result result = (Result)response.getResponse();
        if(result!=null){
            navigateToHomeScreen(result);
        }else{
            promptError(response.getErrorMessage());
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

    private void promptError(String msg){

        if(StringUtils.isNullOrEmpty(msg))
            msg = "Some thing went wrong";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(msg+". Do you want to retry?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                hitRequestForAlbum();
            }
        });
        builder.setNegativeButton("No", null);
        builder.create().show();
        return;
    }

    private void hitRequestForAlbum(){
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
        fetchData(ServerApi.ALBUM_URL, ApiEvent.GET_ALBUM_EVENT, null, null, this, this, this, false);
    }
}

