package com.pointburst.jsmusic.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Debug;
import android.util.Log;

/**
 * Created by FARHAN on 12/27/2014.
 */
public class Utils {

    /**
     * Check Internet connection is available or not.
     *
     * @param context is the {@link android.content.Context} of the {@link android.app.Activity}.
     * @return <b>true</b> is Internet connection is available.
     */
    public static boolean isInternetAvailable(Context context) {
        boolean isInternetAvailable = false;

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();

            if (networkInfo != null && (networkInfo.isConnected())) {
                isInternetAvailable = true;
            }
        } catch (Exception exception) {
            // Do Nothing
            String error = exception.getMessage();
            Logger.print(error);
        }

        return isInternetAvailable;
    }
}
