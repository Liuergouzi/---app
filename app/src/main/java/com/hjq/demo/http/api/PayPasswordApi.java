package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class PayPasswordApi implements IRequestApi {

    @Override
    public String getApi() {
        return "payPasswordUpdate";
    }

    /** 手机号 */
    private String account;
    /** 登录密码 */
    private String payPassword;

    private String sgin;

    public PayPasswordApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public PayPasswordApi setPayPassword(String payPassword) {
        this.payPassword = payPassword;
        return this;
    }

    public PayPasswordApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

}