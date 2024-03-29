package com.dylan.library.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.NonNull;


/**
 * 添加 ACCESS_NETWORK_STATE ACCESS_WIFI_STATE 两个权限
 */
public class NetworkUtils {

  /**
   * 网络是否已连接
   *
   * @return true:已连接 false:未连接
   */
  @SuppressWarnings("deprecation")
  public static boolean iConnected(@NonNull Context context) {
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (manager != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
        if (networkCapabilities != null) {
          return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                  || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                  || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        }
      } else {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
      }
    }
    return false;
  }

  /**
   * Wifi是否已连接
   *
   * @return true:已连接 false:未连接
   */
  @SuppressWarnings("deprecation")
  public static boolean isWifiConnected(@NonNull Context context) {
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (manager != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
        if (networkCapabilities != null) {
          return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        }
      } else {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
      }
    }
    return false;
  }

  /**
   * 是否为流量
   */
  @SuppressWarnings("deprecation")
  public static boolean isMobileData(@NonNull Context context) {
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (manager != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
        if (networkCapabilities != null) {
          return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        }
      } else {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
      }
    }
    return false;
  }

}
