package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;


public final class InvitationItemApi implements IRequestApi {


    @Override
    public String getApi() {
        return "invitationSelect";
    }

    /**
     * 数据
     */
    private String sgin;

    private String account;
    private Integer pageNum;

    public InvitationItemApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public InvitationItemApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }

    public InvitationItemApi setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return this;
    }

}