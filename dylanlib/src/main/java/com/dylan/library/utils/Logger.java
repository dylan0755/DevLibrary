package com.dylan.library.utils;

import android.util.Log;

import com.dylan.library.exception.ELog;

import java.util.List;

/**
 * Created by Dylan on 2017/10/9.
 */

public class Logger {
    private static String LOGTAG = "logTag:";
    private static boolean isDebug = true;

    public static void e(Object msg) {
        if (isDebug) {
            String stackInfo = getStackInfo();

            if (msg != null && msg instanceof List) {
                Log.e(LOGTAG, stackInfo + ":\n" + ((List) msg).size());
                return;
            }else if (msg!=null&& msg instanceof Throwable){
                Log.e(LOGTAG, stackInfo);
                Throwable throwable= (Throwable) msg;
                ELog.e(throwable);
                return;

            }
            Log.e(LOGTAG, stackInfo + ":\n"+msg);
        }

    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, "" + msg);
        }

    }

    public static void i(String msg) {
        if (isDebug) {
            String stackInfo = getStackInfo();
            Log.i(LOGTAG, stackInfo + ":\n" + msg);
        }

    }


    private static String getStackInfo() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements.length < 5) {
            return "no stack info";
        } else {
            String packAndClassName = elements[4].getClassName();
            String simpleClassName = elements[4].getClassName().substring(
                    elements[4].getClassName().lastIndexOf(".") + 1);
            String methodName = elements[4].getMethodName();
            String lineNumber = String.valueOf(elements[4].getLineNumber());
            String tag = packAndClassName + "." + methodName + "(" + simpleClassName + ".java:" + lineNumber + ")";
            return tag;
        }
    }

    public static void setLogTag(String filterTag) {
        LOGTAG = filterTag;
    }

    public static void setDebugMode(boolean bl) {
        isDebug = bl;
    }
}
