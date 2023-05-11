package com.hjq.demo.overall;

import static android.provider.UserDictionary.Words.APP_ID;

import static com.alipay.zoloz.toyger.ToygerBaseService.KEY_TOKEN;

import android.content.Context;
import android.content.SharedPreferences;

public class Cache {
    /**
     * 获取缓存的token
     * */
    public static String getCachedToken(Context context){
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).getString(KEY_TOKEN, null);
    }

    /**
     *  缓存token
     */
    public static void cacheToken(Context context, String token){
        SharedPreferences.Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        e.putString(KEY_TOKEN, token);
        e.apply();
    }
}
