package com.dylan.library.utils;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dylan on 2016/4/16.
 */

public class ToastUtils {
    public static Toast shortToast = null;
    public static Toast longToast = null;
    private static Application applicationContext;

    public static void initToast(Application application) {
        if (ThreadUtils.isMainThread()) {
            applicationContext = application;
            shortToast = Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT);
            longToast = Toast.makeText(applicationContext, "", Toast.LENGTH_LONG);
        } else {
            Log.e("ToastUtils ","Can't create handler inside thread that has not called Looper.prepare()");
        }
    }


    public static void show(String msg) {
        if (applicationContext == null) return;
        shortToast.setText(msg);
        shortToast.show();
    }

    public static void showLong(String msg) {
        if (applicationContext == null) return;
        longToast.setText(msg);
        longToast.show();
    }

    public static void showCenterOnMainThread(String string) {
        if (applicationContext == null) return;
        if (ThreadUtils.isMainThread()){
            Toast toast = null;
            toast = Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT);
            toast.setText(string);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }


    public static void show(Context context, String text) {
        if (ThreadUtils.isMainThread()) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }

    }


    public static void showLongToast(final Toast lenthLongToast, final int duration) {
        if (applicationContext == null) return;
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                lenthLongToast.show();
            }
        }, 0, 3500);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                lenthLongToast.cancel();
                timer.cancel();
            }
        }, duration);
    }
}
