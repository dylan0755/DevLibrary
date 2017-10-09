package com.dylan.library.utils;

import android.util.Log;

/**
 * Created by Dylan on 2017/10/9.
 */

public class Logger {
    private static  String LOGTAG="logTag:";
     public static void e(String msg){
           String stackInfo= getStackInfo();
           Log.e(LOGTAG, stackInfo+":\n"+msg );
     }

     public static void e(String tag,String msg){
         Log.e(tag, ""+msg );
     }



    private static String getStackInfo(){
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements.length < 5) {
            return "no stack info";
        } else {
            String packAndClassName=elements[4].getClassName();
            String simpleClassName = elements[4].getClassName().substring(
                    elements[4].getClassName().lastIndexOf(".") + 1);
            String methodName=elements[4].getMethodName();
            String lineNumber=String.valueOf(elements[4].getLineNumber());
            String tag=packAndClassName + "."+methodName+"("+simpleClassName+".java:"+lineNumber+")";
            return tag;
        }
    }

    public static void setLogTag(String filterTag){
        LOGTAG=filterTag;
    }
}
