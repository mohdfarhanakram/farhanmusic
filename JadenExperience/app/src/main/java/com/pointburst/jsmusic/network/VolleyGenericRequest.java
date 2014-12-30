package com.pointburst.jsmusic.network;

import android.content.Context;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.pointburst.jsmusic.parser.IParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by FARHAN on 12/27/2014.
 */
public class VolleyGenericRequest extends Request<Object>{

    private static final String SERVER_UID ="server";
    private static final String SERVER_PWD ="pwd";

    private Map<String, String> mParams;
    protected Response.Listener mListener;
    protected String mPostData;
    protected Response.ErrorListener mErrorListener;
    protected int mContentType;
    protected Context mContext;
    protected int mEvent;

    private Object requestData = null;

    private IParser parser;

    public IParser getParser() {
        return parser;
    }

    public void setParser(IParser parser) {
        this.parser = parser;
    }

    public Object getRequestData() {
        return requestData;
    }

    public void setRequestData(Object requestData) {
        this.requestData = requestData;
    }

    /**
     * Charset for request.
     */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);


    public interface ContentType {

        int PLAIN_TEXT = 0;
        int FORM_ENCODED_DATA = 1;
        int JSON = 2;
    }

    /**
     * Creates a new request with the given method.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public VolleyGenericRequest(int contentType, String url, String postData, Response.Listener<String> listener,
                                Response.ErrorListener errorListener, Context ctx) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mErrorListener = errorListener;
        mContentType = contentType;
        mPostData = postData;
        mContext = ctx.getApplicationContext();

    }

    public VolleyGenericRequest(int contentType, String url, HashMap<String, String> paramsMap, Response.Listener<String> listener,
                                Response.ErrorListener errorListener, Context ctx) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mErrorListener = errorListener;
        mContentType = contentType;
        mParams = paramsMap;
        mContext = ctx.getApplicationContext();

    }

    /**
     * Creates a new request with the given method.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public VolleyGenericRequest(String url, Response.Listener<String> listener,
                                Response.ErrorListener errorListener, Context ctx) {
        super(Method.GET, url, errorListener);
        mListener = listener;
        mErrorListener = errorListener;
        mContext = ctx.getApplicationContext();
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {

            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d("VolleyGenericRequest", jsonString);
            ServiceResponse serviceResponse = getParser().parseData(mEvent, jsonString);
            serviceResponse.setRequestObj(getRequestData());
            serviceResponse.setEvent(mEvent);
            Response<Object> resp = Response.success((Object) (serviceResponse), HttpHeaderParser.parseCacheHeaders(response));
            return resp;
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Object response) {
        mListener.onResponse(response);
    }

    public String getPostData() {
        return mPostData;
    }

    public void setPostData(String mPostData) {
        this.mPostData = mPostData;
    }

    public int getEvent() {
        return mEvent;
    }

    public void setEvent(int mEvent) {
        this.mEvent = mEvent;
    }
}
