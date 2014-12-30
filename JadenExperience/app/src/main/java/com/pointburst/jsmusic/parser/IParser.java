package com.pointburst.jsmusic.parser;

import com.pointburst.jsmusic.network.ServiceResponse;

/**
 * Created by FARHAN on 12/27/2014.
 */
public interface IParser {
    public ServiceResponse parseData(int eventType, String data);

}
