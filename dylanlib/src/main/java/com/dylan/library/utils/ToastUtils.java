package com.dylan.library.utils;

import android.app.Application;
import android.util.Log;
import android.view.Gravity;

import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.thread.ThreadUtils;
import com.dylan.library.toast.IToastStrategy;
import com.dylan.library.toast.ToastMsg;

/**
 * Created by Dylan on 2016/4/16.
 */

public class ToastUtils {
    private static Application applicationContext;

    public static void initToast(Application application) {
        HJQToastUtils.init(application);
        if (ThreadUtils.isMainThread()) {
            applicationContext = application;
        } else {
            Log.e("HJQToastUtils ", "Can't create handler inside thread that has not called Looper.prepare()");
        }
    }




    public static void show(String msg) {
        if (applicationContext == null) return;
        ToastMsg toastMsg=new ToastMsg();
        toastMsg.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        toastMsg.setOffsetX(0);
        toastMsg.setOffsetY(ScreenUtils.getScreenHeight(applicationContext) / 8);
        toastMsg.setText(msg);
        toastMsg.setDuration(IToastStrategy.SHORT_DURATION_TIMEOUT);
        HJQToastUtils.show(toastMsg);
    }

    public static void showLong(String msg) {
        if (applicationContext == null) return;
        ToastMsg toastMsg=new ToastMsg();
        toastMsg.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,0,ScreenUtils.getScreenHeight(applicationContext) / 8);
        toastMsg.setText(msg);
        toastMsg.setDuration(IToastStrategy.LONG_DURATION_TIMEOUT);
        HJQToastUtils.show(toastMsg);

    }


    public static void showCenterShort(String msg) {
        if (applicationContext == null) return;
        ToastMsg toastMsg=new ToastMsg();
        toastMsg.setGravity(Gravity.CENTER, 0, 0);
        toastMsg.setText(msg);
        toastMsg.setDuration(IToastStrategy.SHORT_DURATION_TIMEOUT);
        HJQToastUtils.show(toastMsg);
    }

    public static void showCenterLong(String string) {
        if (applicationContext == null) return;
//        centerLongToast.setText(string);
//        centerLongToast.show();
        ToastMsg toastMsg=new ToastMsg();
        toastMsg.setGravity(Gravity.CENTER, 0, 0);
        toastMsg.setText(string);
        toastMsg.setDuration(IToastStrategy.LONG_DURATION_TIMEOUT);
        HJQToastUtils.show(toastMsg);
    }


    public static void showLongToast(String text, final int durationMills) {
        if (applicationContext == null) return;
        ToastMsg toastMsg=new ToastMsg();
        toastMsg.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        toastMsg.setOffsetX(0);
        toastMsg.setOffsetY(ScreenUtils.getScreenHeight(applicationContext) / 8);
        toastMsg.setText(text);
        toastMsg.setDuration(durationMills);
        HJQToastUtils.show(toastMsg);
    }




    /**
     * 取消吐司的显示
     */
    public static synchronized void cancel() {
        HJQToastUtils.cancel();
    }





}
