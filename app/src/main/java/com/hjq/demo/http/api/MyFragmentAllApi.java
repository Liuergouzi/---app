package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class MyFragmentAllApi implements IRequestApi {


    @Override
    public String getApi() {
        return "myArticle";
    }

    /**
     * 数据
     */
    private String sgin;

    private String account;
    private Integer pageNum;

    public MyFragmentAllApi setAccount(String account) {
        this.account = account;
        return this;
    }
    public MyFragmentAllApi setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return this;
    }

    public MyFragmentAllApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

}
