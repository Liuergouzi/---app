package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class PersonalSelectApi implements IRequestApi {


    @Override
    public String getApi() {
        return "personalSelect";
    }

    /**
     * 数据
     */
    private String account;
    private String sgin;


    public PersonalSelectApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public PersonalSelectApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

}