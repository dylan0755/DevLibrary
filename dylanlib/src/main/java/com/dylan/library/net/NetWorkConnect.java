package com.dylan.library.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * 添加 ACCESS_NETWORK_STATE ACCESS_WIFI_STATE 两个权限
 */
public class NetWorkConnect {

  public static boolean isNetworkConnected(Context context) {
    try {
      if (context != null) {
        ConnectivityManager mConnectivityManager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
          return mNetworkInfo.isAvailable();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public static boolean isWifiConnected(Context context) {
    try {
      if (context != null) {
        ConnectivityManager mConnectivityManager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo =
            mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWiFiNetworkInfo != null) {
          return mWiFiNetworkInfo.isAvailable();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public static boolean isMobileConnected(Context context) {
    try {
      if (context != null) {
        ConnectivityManager mConnectivityManager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobileNetworkInfo =
            mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobileNetworkInfo != null) {
          return mMobileNetworkInfo.isAvailable();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
}
