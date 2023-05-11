package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class PutFragmentOssApi implements IRequestApi {

    @Override
    public String getApi() {
        return "articleInsert";
    }

    /** 数据 */
    private String sgin;
    private String account;
    private String content;
    private String time;
    private int imageSize;

    public PutFragmentOssApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }


    public PutFragmentOssApi setAccount(String account) {
        this.account = account;
        return this;
    }

    public PutFragmentOssApi setContent(String content) {
        this.content = content;
        return this;
    }

    public PutFragmentOssApi setTime(String time) {
        this.time = time;
        return this;
    }

    public PutFragmentOssApi setImageSize(int imageSize) {
        this.imageSize = imageSize;
        return this;
    }


}