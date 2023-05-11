package com.hjq.demo.comfirm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NetStateChangeReceiver extends BroadcastReceiver {

    private static class InstanceHolder{
        private static final NetStateChangeReceiver INSTANCE = new NetStateChangeReceiver();
    }

    private final List<NetStateChangeObserver> mObservers = new ArrayList<>();


    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            NetworkType networkType = NetworkUtil.getNetworkType(context);
            notifyObservers(networkType,context);
            Toast.makeText(context,networkType.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String registerReceiver(Context context){
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver( InstanceHolder.INSTANCE,intentFilter);
        NetworkType networkType = NetworkUtil.getNetworkType(context);
        return networkType.toString();
    }

    public static void unRegisterReceiver(Context context){
        context.unregisterReceiver( InstanceHolder.INSTANCE);
    }

    public static void registerObserver(NetStateChangeObserver observer){
        if (observer == null) {
            return;
        }
        if (!InstanceHolder.INSTANCE.mObservers.contains(observer)){
            InstanceHolder.INSTANCE.mObservers.add(observer);

        }
    }

    public static void unRegisterObserver(NetStateChangeObserver observer){
        if (observer == null) {
            return;
        }
        if (InstanceHolder.INSTANCE.mObservers == null) {
            return;
        }
        InstanceHolder.INSTANCE.mObservers.remove(observer);
    }

    private void notifyObservers(NetworkType networkType,Context context){
         NetworkType mType = NetworkUtil.getNetworkType(context);
        if (mType == networkType) {
            return;
        }
        if (networkType == NetworkType.NETWORK_NO){
            for (NetStateChangeObserver observer : mObservers){
                observer.onNetDisconnected();
            }
        }else {
            for (NetStateChangeObserver observer : mObservers){
                observer.onNetConnected(networkType);
            }
        }
    }

}
