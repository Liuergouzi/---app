package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;


public final class VersionApi implements IRequestApi {

    @Override
    public String getApi() {
        return "version";
    }

    /** 数据 */
    private String sgin;

    public VersionApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }
}