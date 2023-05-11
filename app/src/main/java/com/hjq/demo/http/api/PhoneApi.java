package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class PhoneApi implements IRequestApi {

    @Override
    public String getApi() {
        return "phoneUpdate";
    }

    /** 旧手机号验证码（没有绑定情况下可不传） */
    private String preCode;

    /** 新手机号 */
    private String phone;
    /** 新手机号验证码 */
    private String code;

    private String account;

    private String sgin;

    public PhoneApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public PhoneApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

    public PhoneApi setPreCode(String preCode) {
        this.preCode = preCode;
        return this;
    }

    public PhoneApi setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public PhoneApi setCode(String code) {
        this.code = code;
        return this;
    }
}