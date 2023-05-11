package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;

public final class DmfQrcodeApi implements IRequestApi, IRequestServer {

    @Override
    public String getHost() {
        return "http://ctrlc.cc/api/";
    }

    @Override
    public String getApi() {
        return "payapp/pay.php";
    }

    /** 数据 */
    private String sgin;
    private String account;

    public DmfQrcodeApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }


    public DmfQrcodeApi setAccount(String account) {
        this.account = account;
        return this;
    }
}