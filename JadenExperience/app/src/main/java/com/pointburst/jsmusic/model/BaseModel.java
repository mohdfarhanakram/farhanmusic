package com.pointburst.jsmusic.model;

import com.pointburst.jsmusic.network.ServiceResponse;
import org.json.JSONObject;

/**
 * Created by FARHAN on 12/27/2014.
 */
public class BaseModel {

    private boolean success;
    private int responseCode;
    private String errorMessage;
    private JSONObject jsonObject;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        success = (getResponseCode()== ServiceResponse.SUCCESS);  // 200 is success code
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }



    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
