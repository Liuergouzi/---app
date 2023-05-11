package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class KeyApi implements IRequestApi {

    @Override
    public String getApi() {
        return "key";
    }

    /** 数据 */
    private String sgin;
    private String name;


    public KeyApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

    public KeyApi setName(String name) {
        this.name = name;
        return this;
    }

}
