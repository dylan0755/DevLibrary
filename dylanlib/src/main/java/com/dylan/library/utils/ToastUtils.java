package com.dylan.library.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.dylan.library.utils.thread.ThreadUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dylan on 2016/4/16.
 */

public class ToastUtils {
    private static Toast shortToast = null;
    private static Toast longToast = null;
    private static Toast centerShortToast = null;
    private static Toast centerLongToast = null;

    private static Application applicationContext;

    public static void initToast(Application application) {
        if (ThreadUtils.isMainThread()) {
            applicationContext = application;
            shortToast = Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT);
            longToast = Toast.makeText(applicationContext, "", Toast.LENGTH_LONG);
            centerShortToast = Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT);
            centerShortToast.setGravity(Gravity.CENTER, 0, 0);
            centerLongToast = Toast.makeText(applicationContext, "", Toast.LENGTH_LONG);
            centerLongToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            Log.e("ToastUtils ", "Can't create handler inside thread that has not called Looper.prepare()");
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

    @Deprecated
    public static void showCenter(String string) {
        if (applicationContext == null) return;
        centerShortToast.setText(string);
        centerShortToast.show();
    }

    public static void showCenterShort(String string) {
        if (applicationContext == null) return;
        centerShortToast.setText(string);
        centerShortToast.show();
    }

    public static void showCenterLong(String string) {
        if (applicationContext == null) return;
        centerLongToast.setText(string);
        centerLongToast.show();
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
