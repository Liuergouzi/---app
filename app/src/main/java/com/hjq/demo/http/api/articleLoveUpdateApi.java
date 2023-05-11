package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class articleLoveUpdateApi implements IRequestApi {

    @Override
    public String getApi() {
        return "articleLoveCountUpdate";
    }

    /** 数据 */
    private String sgin;
    private String account;
    private Integer id;
    private String name;

    public articleLoveUpdateApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public articleLoveUpdateApi setId(Integer id) {
        this.id = id;
        return this;
    }

    public articleLoveUpdateApi setName(String name) {
        this.name = name;
        return this;
    }

    public articleLoveUpdateApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

}