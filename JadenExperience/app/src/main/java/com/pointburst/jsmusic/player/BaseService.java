package com.pointburst.jsmusic.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;
import com.android.volley.*;
import com.pointburst.jsmusic.constant.ApiEvent;
import com.pointburst.jsmusic.constant.ServerApi;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.network.ServiceResponse;
import com.pointburst.jsmusic.network.VolleyGenericRequest;
import com.pointburst.jsmusic.network.VolleyHelper;
import com.pointburst.jsmusic.parser.BaseParser;
import com.pointburst.jsmusic.parser.IParser;
import com.pointburst.jsmusic.utils.Logger;
import com.pointburst.jsmusic.utils.Utils;

/**
 * Created by FARHAN on 1/2/2015.
 */
public class BaseService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,Response.Listener, Response.ErrorListener,MediaPlayer.OnBufferingUpdateListener {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void fetchMedia(String mediaKey){
        fetchData(ServerApi.GET_MEDIA_URL+"&dk="+mediaKey, ApiEvent.MEDIA_EVENT,mediaKey);
    }


    public boolean fetchData(String url, final int eventType,Object requestObject) {
        boolean returnVal = false;

        final IParser parser1 = new BaseParser();

            Logger.print("request " + url + "");

            if (Utils.isInternetAvailable(this)) {
                VolleyGenericRequest req = new VolleyGenericRequest(url, this, this, this);
                req.setRequestData(requestObject);
                req.setEvent(eventType);
                req.setParser(parser1);
                //TODO  req.setRequestTimeOut(Constants.API_TIMEOUT);
                VolleyHelper.getInstance(this).addRequestInQueue(req);
            }else {
                returnVal = true;
            }

        return returnVal;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ServiceResponse responseObj = new ServiceResponse();
        responseObj.setResponseCode(ServiceResponse.ERROR);

        if( error instanceof NetworkError) {
            responseObj.setErrorMessage("Network Connection is not available.");
        } else if( error instanceof ServerError) {
            responseObj.setErrorMessage("Server Error");

        } else if( error instanceof AuthFailureError) {
            responseObj.setErrorMessage("Auth Error");
        } else if( error instanceof ParseError) {
            responseObj.setErrorMessage("Parsing Error");
        } else if( error instanceof NoConnectionError) {

            responseObj.setErrorMessage("No connection could be established.");

        } else if( error instanceof TimeoutError) {
            responseObj.setErrorMessage("Network Connection Time out");
        }else{
            responseObj.setErrorMessage("Some thing went wrong.");
        }

        onResponse(responseObj);
    }

    @Override
    public void onResponse(Object response) {


    }

    public void onGetMedia(Media media){

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }
}
