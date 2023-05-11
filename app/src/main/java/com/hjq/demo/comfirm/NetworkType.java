package com.hjq.demo.comfirm;

public enum NetworkType {
    NETWORK_WIFI("您当前为WiFi网络"),
    NETWORK_4G("您当前为4G网络"),
    NETWORK_2G("您当前为3G网络"),
    NETWORK_3G("您当前为2G网络"),
    NETWORK_UNKNOWN("您当前为5G网络"),
    NETWORK_NO("网络已断开");

    private String desc;
    NetworkType(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
