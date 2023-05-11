package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class ClockSelectApi implements IRequestApi {

    @Override
    public String getApi() {
        return "taskPackTotal";
    }

    /** 数据 */
    private String sgin;
    private String account;

    public ClockSelectApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }


    public ClockSelectApi setAccount(String account) {
        this.account = account;
        return this;
    }
}