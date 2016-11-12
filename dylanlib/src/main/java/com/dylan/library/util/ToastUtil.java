package com.dylan.library.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Dylan on 2016/4/16.
 */
public class ToastUtil {
    public static Context mContext;
    public static  void toToast(String str){
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }

    public static void toToast(int stringId){
        String text=mContext.getString(stringId);
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    public static  void toLongToast(String str){
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
    }

    public static void toLongToast(int stringId){
        String text=mContext.getString(stringId);
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }

}
