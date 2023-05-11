package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

public final class HostPicRandApi implements IRequestApi, IRequestServer {

    @Override
    public String getHost() {
        return "https://api.apiopen.top/";
    }

    @Override
    public String getPath() {
        return "api/";
    }

    @Override
    public String getApi() {
        return "getImages?page=0&size=10";
    }


}



