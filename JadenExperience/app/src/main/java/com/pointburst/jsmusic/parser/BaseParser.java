package com.pointburst.jsmusic.parser;

import com.pointburst.jsmusic.constant.ApiEvent;
import com.pointburst.jsmusic.network.ServiceResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FARHAN on 12/27/2014.
 */
public class BaseParser implements IParser{

    public ServiceResponse parseData(int eventType, String data) {
        ServiceResponse response = null;
        try {
            response = handleJsonResponse(eventType, new JSONObject(data));
        } catch (Exception e) {
            response = new ServiceResponse();
            response.setResponseCode(ServiceResponse.EXCEPTION);
        }
        return response;
    }


    protected ServiceResponse handleJsonResponse(int eventType, JSONObject jsonObject) {
        ServiceResponse response = new ServiceResponse();
        try {
            response = parseBaseData(jsonObject,response);
            response.setEvent(eventType);

            if(response.isSuccess()){
                parseJsonData(response);
            }

        } catch (Exception e) {
            response.setResponseCode(ServiceResponse.EXCEPTION);
            e.printStackTrace();
        }
        return response;
    }





    protected void parseJsonData(ServiceResponse response) throws JSONException {
        JSONObject jsonObject = response.getJsonObject();
        switch (response.getEvent()) {
            case ApiEvent.GET_MEDIA_EVENT:
                 response.setResponse(JsonParser.parseAlbumJson(jsonObject));
                break;

            default:
                break;
        }

    }

    protected ServiceResponse parseBaseData(JSONObject jsonObject, ServiceResponse response) throws JSONException {

        response.setJsonObject(jsonObject);
        JSONObject jMeta = jsonObject.optJSONObject("response").optJSONObject("metadata");
        response.setResponseCode(Integer.parseInt(jMeta.optString("statusCode","0")));

        return response;
    }

}
