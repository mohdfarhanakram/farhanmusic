package com.pointburst.jsmusic.parser;

import android.util.Log;
import com.google.gson.Gson;
import com.pointburst.jsmusic.model.Media;
import com.pointburst.jsmusic.model.Result;
import org.json.JSONObject;

/**
 * Created by FARHAN on 12/27/2014.
 */
public class JsonParser {

    private static final String TAG = "JsonParser";

    public static Result parseAlbumJson(JSONObject jsonObject){
        Result result = null;
        try{
            JSONObject jobj = jsonObject.optJSONObject("response").optJSONObject("result");
            Gson gson = new Gson();
            result = gson.fromJson(jobj.toString(),Result.class);

        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
     return result;
    }


    public static Media parseMediaJson(JSONObject jsonObject){
        Media media = null;
        try{
            JSONObject jobj = jsonObject.optJSONObject("response").optJSONObject("result").optJSONObject("media");
            Gson gson = new Gson();
            media = gson.fromJson(jobj.toString(),Media.class);

        }catch(Exception e){
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
        return media;
    }
}
