package com.dylan.library.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dylan on 2016/4/16.
 */

public class ToastUtil {
    public static Toast shortToast = null;
    public static Toast longToast = null;
    private static Context context;

    public static void initToast(Context ctext) {
        context = ctext;
        shortToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        longToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
    }


    public static void show(String msg) {
        if (shortToast == null) shortToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        shortToast.setText(msg);
        shortToast.show();
    }

    public static void showLong(String msg) {
        if (longToast == null) longToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        longToast.setText(msg);
        longToast.show();
    }

    public static void showCenter(String string) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setText(string);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public static void show(Context context, int info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }


    public static void showLongToast(final Toast lenthLongToast, final int duration) {
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
