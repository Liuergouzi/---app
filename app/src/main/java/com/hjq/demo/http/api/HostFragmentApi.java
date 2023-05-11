package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;


public final class HostFragmentApi implements IRequestApi {

    @Override
    public String getApi() {
        return "article";
    }

    /** 数据 */
    private String sgin;
    private Integer pageNum;

    public HostFragmentApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }
    public HostFragmentApi setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return this;
    }
}
