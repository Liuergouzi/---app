package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class PasswordResetApi implements IRequestApi {

    @Override
    public String getApi() {
        return "passwordUpdate";
    }

    /** 手机号 */
    private String account;
    /** 登录密码 */
    private String password;

    private String sgin;

    public PasswordResetApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public PasswordResetApi setPassword(String password) {
        this.password = password;
        return this;
    }

    public PasswordResetApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

}