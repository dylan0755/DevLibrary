package com.dylan.library.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dylan on 2016/4/16.
 */
public class ToastUtils {
    public static Toast toast = null;
    private static Context context;

    public static void initToast(Context context){
        context=context;
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }



    public static void show(String msg){
        toast.setText(msg);
        toast.show();
    }


    public static void showLongToast(final Toast lenthLongToast,final int duration){
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
        }, duration );
    }

}
