package com.dylan.library.exception;


import com.dylan.library.utils.ToastUtils;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * Created by Dylan on 2018/7/13.
 */

public class ThrowableUtils {
    public static void show(Throwable throwable) {
        if (throwable == null) return;
        if (throwable instanceof SocketTimeoutException || throwable instanceof ConnectTimeoutException) {
            ToastUtils.show("连接超时");
        } else if (throwable instanceof UnknownHostException) {
            ToastUtils.show("无法连接服务器，请检查网络连接");
        } else if (throwable instanceof ConnectException) {//禁止报错显示ip
            ToastUtils.show("连接失败");
        } else {
            if (throwable.getMessage() != null && throwable.getMessage().contains("HTTP 502 Bad Gateway")) {
                ToastUtils.show("服务器繁忙,请稍后重试(502)");
            } else {
                ToastUtils.show(throwable.getMessage());
            }
        }
        ELog.e(throwable);

    }


    public static void show(Object errorMsg) {
        if (errorMsg == null) return;
        if (errorMsg instanceof String) {
            ToastUtils.show((String) errorMsg);
        } else if (errorMsg instanceof Throwable) {
            Throwable throwable = (Throwable) errorMsg;
            show(throwable);
        } else {
            ToastUtils.show("invalid input,only support String or Throwable");
        }
    }

}
