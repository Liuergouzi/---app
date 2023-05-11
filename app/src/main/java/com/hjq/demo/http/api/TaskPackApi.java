package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

public final class TaskPackApi implements IRequestApi {

    @Override
    public String getApi() {
        return "buyTask";
    }

    /** 数据 */
    private String sgin;
    private String account;
    private String type;
    private String time;
    private String surplus;
    private String quota;

    public TaskPackApi setSgin(String sgin) {
        this.sgin = sgin;
        return this;
    }
    public TaskPackApi setAccount(String account) {
        this.account = account;
        return this;
    }
    public TaskPackApi setType(String type) {
        this.type = type;
        return this;
    }
    public TaskPackApi setTime(String time) {
        this.time = time;
        return this;
    }
    public TaskPackApi setSurplus(String surplus) {
        this.surplus = surplus;
        return this;
    }
    public TaskPackApi setQuota(String quota) {
        this.quota = quota;
        return this;
    }

}