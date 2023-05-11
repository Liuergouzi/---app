package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class NoticeFragmentApi implements IRequestApi {

    @Override
    public String getApi() {
        return "notice";
    }

    /** 数据 */
    private String sgin;
    private Integer pageNum;


    public NoticeFragmentApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }
    public NoticeFragmentApi setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return this;
    }

}