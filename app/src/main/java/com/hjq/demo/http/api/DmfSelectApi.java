package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;


public final class DmfSelectApi implements IRequestApi {

    @Override
    public String getApi() {
        return "pay";
    }

    /** 数据 */
    private String sgin;
    private String account;
    private String mode;

    public DmfSelectApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }


    public DmfSelectApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public DmfSelectApi setMode(String mode) {
        this.mode = mode;
        return this;
    }

}