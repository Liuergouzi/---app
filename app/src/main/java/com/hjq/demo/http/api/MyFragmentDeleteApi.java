package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class MyFragmentDeleteApi implements IRequestApi {


    @Override
    public String getApi() {
        return "articleDelete";
    }

    /**
     * 数据
     */
    private String sgin;

    private String account;

    private String time;

    public MyFragmentDeleteApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public MyFragmentDeleteApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

    public MyFragmentDeleteApi setTime(String time) {
        this.time = time;
        return this;
    }

}

