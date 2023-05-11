package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class BalanceApi implements IRequestApi {


    @Override
    public String getApi() {
        return "recordSelect";
    }

    /**
     * 数据
     */
    private String sgin;

    private String account;

    private Integer pageNum;

    public BalanceApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public BalanceApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

    public BalanceApi setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return this;
    }

}
