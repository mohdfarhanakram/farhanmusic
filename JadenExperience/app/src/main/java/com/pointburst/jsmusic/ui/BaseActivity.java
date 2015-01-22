package com.pointburst.jsmusic.ui;

import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.android.volley.*;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.constant.ApiEvent;
import com.pointburst.jsmusic.constant.Constants;
import com.pointburst.jsmusic.model.Album;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.model.Result;
import com.pointburst.jsmusic.network.ServiceResponse;
import com.pointburst.jsmusic.network.VolleyGenericRequest;
import com.pointburst.jsmusic.network.VolleyHelper;
import com.pointburst.jsmusic.parser.BaseParser;
import com.pointburst.jsmusic.parser.IParser;
import com.pointburst.jsmusic.utils.Logger;
import com.pointburst.jsmusic.utils.StringUtils;

/**
 * Created by FARHAN on 12/27/2014.
 */
abstract public class BaseActivity extends FragmentActivity implements Response.Listener, Response.ErrorListener, View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Helper method to make Http get data from server.
     *
     * @param url       request url
     * @param eventType request event type
     * @param requestObject Object used to uniquely identify the response
     */

    public boolean fetchData(String url, final int eventType,IParser parser,Object requestObject,Response.Listener responseListener,Response.ErrorListener errorListener,Context ctx,boolean isLoaderRequired) {
        boolean returnVal = false;
            String cachedResponse = getJSONForRequest(eventType);

            final IParser parser1 = parser == null ? new BaseParser() : parser;

            if (StringUtils.isNullOrEmpty(cachedResponse)) {
                Logger.print("request " + url + "");

                    VolleyGenericRequest req = new VolleyGenericRequest(url, responseListener, errorListener, ctx);
                    req.setRequestData(requestObject);
                    req.setEvent(eventType);
                    req.setParser(parser1);
                    //TODO  req.setRequestTimeOut(Constants.API_TIMEOUT);
                    VolleyHelper.getInstance(this).addRequestInQueue(req);

            } else {

                final String tempResponse = cachedResponse;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //       Log.d("BaseActivity fetchData", "found response in cache for eventType " + eventType);
                        onResponse(parser1.parseData(eventType, tempResponse));
                        //TODO execute it on Non UI Thread
                    }
                });
            }
        return returnVal;
    }

    private ProgressDialog mProgressDialog;

    /**
     * Shows a simple native progress dialog<br/>
     * Subclass can override below two methods for custom dialogs- <br/>
     * 1. showProgressDialog <br/>
     * 2. removeProgressDialog
     *
     * @param bodyText
     */
    public void showProgressDialog(String bodyText) {
        if (isFinishing()) {
            return;
        }
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(BaseActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_SEARCH) {
                        return true; //
                    }
                    return false;
                }
            });
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * Removes the simple native progress dialog shown via showProgressDialog <br/>
     * Subclass can override below two methods for custom dialogs- <br/>
     * 1. showProgressDialog <br/>
     * 2. removeProgressDialog
     */
    public void removeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Utility function for displaying progress dialog
     *
     */

    public void showProgressDialog() {

        showProgressDialog("Loading...");
    }


    /**
     * Utility function for showing common error dialog.
     *
     * @param message
     */

    public void showToast(String message) {
        if (TextUtils.isEmpty(message)) {
            message = "Something went wrong";
        }
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper function to obtain cached json data based on event type
     *
     * @param eventType
     * @return
     */
    public String getJSONForRequest(int eventType) {
        String request = null;
        switch (eventType) {
            case ApiEvent.GET_ALBUM_EVENT:
                request = Constants.MEDIA_RESULT;
                break;

            default:

        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(request)) {
            return preferences.getString(request, "");
        }

        return "";
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        removeProgressDialog();
        ServiceResponse responseObj = new ServiceResponse();

        responseObj.setResponseCode(ServiceResponse.ERROR);

		/*int  statusCode = error.networkResponse.statusCode;
		NetworkResponse response = error.networkResponse;

		Log.d("testerror",""+statusCode+" "+response.data);*/
        // Handle your error types accordingly.For Timeout & No connection error, you can show 'retry' button.
        // For AuthFailure, you can re login with user credentials.
        // For ClientError, 400 & 401, Errors happening on client side when sending api request.
        // In this case you can check how client is forming the api and debug accordingly.
        // For ServerError 5xx, you can do retry or handle accordingly.
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
        }

        updateUi(responseObj);

    }


    @Override
    public void onResponse(Object response) {
        removeProgressDialog();
        updateUi((ServiceResponse)response);
    }


    public void updateUi(ServiceResponse response){

    }





}