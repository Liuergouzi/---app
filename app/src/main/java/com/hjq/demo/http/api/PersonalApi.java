package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class PersonalApi implements IRequestApi {


    @Override
    public String getApi() {
        return "personalUpdate";
    }

    /**
     * 数据
     */
    private String sgin;
    private String account;
    private String mode;
    private String value;

    public PersonalApi setAccount(String account) {
        this.account = account;
        return this;
    }
    public PersonalApi setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public PersonalApi setValue(String value) {
        this.value = value;
        return this;
    }

    public PersonalApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

}