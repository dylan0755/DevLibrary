package com.dylan.library.utils;

import android.content.Context;
import android.widget.Toast;

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



}
