package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;


public final class RegisterApi implements IRequestApi {

    @Override
    public String getApi() {
        return "register";
    }

    /** 数据 */
    private String account;
    private String password;
    private String invitation;
    private String sgin;

    public RegisterApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public RegisterApi setPassword(String password) {
        this.password = password;
        return this;
    }

    public RegisterApi setInvitation(String invitation) {
        this.invitation = invitation;
        return this;
    }

    public RegisterApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

}
