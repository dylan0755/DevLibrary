package com.dylan.library.net;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2016/10/19.
 */
public class NetWorkChangeBroadCast extends BroadcastReceiver {
    private static Context mContext;
    private static NetWorkChangeBroadCast mInstance;
    private List<NetWorkChangeListener> mList;
    private boolean hasregister;
    private int netWorkBreak=1;
    private NetWorkChangeBroadCast() {
        mList = new ArrayList<NetWorkChangeListener>();
    }

    public static NetWorkChangeBroadCast getInstance(Context context) {
        if (mContext==null) mContext=context;
        if (mInstance == null) {
            synchronized (NetWorkChangeBroadCast.class) {
                if (mInstance == null) {
                    mInstance = new NetWorkChangeBroadCast();
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            System.out.println("当前网络断开！");
            Toast.makeText(context, "当前网络断开！", Toast.LENGTH_SHORT).show();
            netWorkBreak=0;
        } else {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (netWorkBreak!=1){
                    Toast.makeText(context, "当前切换到2G/3G/4G,请注意流量", Toast.LENGTH_LONG).show();
                }

                if (mList!= null&&netWorkBreak==0) {//从无网络进入移动网络
                    for (NetWorkChangeListener listener : mList) {
                        if (listener != null) {
                            listener.restoreConnect();
                        }
                    }
                }
                netWorkBreak=1;
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                System.out.println("当前切换到wifi网络");
                if (mList != null&&netWorkBreak==0) {//从无网络进入wifi网络
                    for (NetWorkChangeListener listener : mList) {
                        if (listener != null) {
                            listener.restoreConnect();
                        }
                    }
                }
                netWorkBreak=2;
            }
        }

    }

    private void registerBroadCast(Context context) {
        if (!hasregister){
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            context.registerReceiver(this, filter);
            hasregister=true;
        }

    }

    public void unregisterReceiver(Context context) {
        if (hasregister){
            context.unregisterReceiver(this);
            hasregister=false;
        }

    }


    public interface NetWorkChangeListener {
        void restoreConnect();
    }

    public NetWorkChangeListener setNetWorkChangeListener(NetWorkChangeListener listener) {
        registerBroadCast(mContext);
        mList.add(listener);
        return listener;
    }

    public void removeListener(NetWorkChangeListener listener) {
        mList.remove(listener);
    }
}
