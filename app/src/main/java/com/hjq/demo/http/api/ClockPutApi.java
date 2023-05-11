package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class ClockPutApi implements IRequestApi {

    @Override
    public String getApi() {
        return "taskPackReceive";
    }

    /** 数据 */
    private String sgin;
    private String account;

    public ClockPutApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public ClockPutApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

}