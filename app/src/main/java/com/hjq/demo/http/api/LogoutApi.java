package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class LogoutApi implements IRequestApi {

    @Override
    public String getApi() {
        return "logout";
    }
}