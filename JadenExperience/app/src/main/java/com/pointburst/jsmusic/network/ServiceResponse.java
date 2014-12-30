package com.pointburst.jsmusic.network;

import com.pointburst.jsmusic.model.BaseModel;

/**
 * Created by FARHAN on 12/27/2014.
 */
public class ServiceResponse extends BaseModel{

  public static final int SUCCESS     = 200;
  public static final int ERROR       = -1;
  public static  final int EXCEPTION  = 0;

  private Object response;
  private int event;
  private Object requestObj;



    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public Object getRequestObj() {
        return requestObj;
    }

    public void setRequestObj(Object requestObj) {
        this.requestObj = requestObj;
    }
}
