package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class LoginApi implements IRequestApi {

    @Override
    public String getApi() {
        return "login";
    }
//
//    @Override
//    public String getApi() {
//        return "024c7d81d75930bcb09f8f72886c83b8.php";
//    }

    /** 手机号 */
    private String account;
    /** 登录密码 */
    private String password;

    private String sgin;

    public LoginApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public LoginApi setPassword(String password) {
        this.password = password;
        return this;
    }

    public LoginApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

    public final static class Bean {

        private String token;

        public String getToken() {
            return token;
        }
    }
}