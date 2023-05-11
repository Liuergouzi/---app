package com.hjq.demo.overall;

import android.annotation.SuppressLint;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class CurrentTime {
    public String getTime(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public String getTimeNone(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public String getUuid(){
        return  UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }


}
