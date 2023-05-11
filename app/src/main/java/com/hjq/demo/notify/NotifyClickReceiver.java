package com.hjq.demo.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hjq.demo.main.MainActivity;

/**
 * 点击通知栏跳转的广播
 */
public class NotifyClickReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        String data = intent.getStringExtra("data");
        if (action.equals("com.hjq.demo"))
        {
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("data", data);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //从四大组件的其他三个组件跳转到Activity,需要设置FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i);
        }
    }
}