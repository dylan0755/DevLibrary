package com.xm.vbrowser.app.util;


/**
 * Author: Dylan
 * Date: 2022/3/18
 * Desc:
 */

public class VideoSnifferLogger {
    private  static boolean isDebug=true;

    public static void setIsDebug(boolean isDebug) {
        VideoSnifferLogger.isDebug = isDebug;
    }

    public static void d(String tag,String msg){
       if (!isDebug)return;
        android.util.Log.d(tag,msg);
    }

    public static void d(String tag, String msg, Throwable throwable){
        if (!isDebug)return;
        android.util.Log.d(tag,msg+""+(throwable!=null?throwable.getMessage():""));
    }

    public static void e(String tag,String msg){
        if (!isDebug)return;
        android.util.Log.d(tag,msg);
    }
}
