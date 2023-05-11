package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

public final class ComfirmApi implements IRequestApi, IRequestServer {

    @Override
    public String getHost() {
        return "http://ctrlc.cc/api/";
    }

    @Override
    public String getApi() {
        return "";
    }
    /** 数据 */
    private String sgin;
    private String account;
    private String phone;
    private String cardname;
    private String cardid;
    public ComfirmApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }


    public ComfirmApi setAccount(String account) {
        this.account = account;
        return this;
    }
    public ComfirmApi setPhone(String phone) {
        this.phone = phone;
        return this;
    }
    public ComfirmApi setcardname(String cardname) {
        this.cardname = cardname;
        return this;
    }
    public ComfirmApi setcardid(String cardid) {
        this.cardid = cardid;
        return this;
    }



}