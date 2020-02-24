package com.dylan.library.utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import com.dylan.library.exception.ELog;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.security.Policy;
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

            if (msg instanceof List) {
                int len=((List) msg).size();
                String arrayString=ArrayUtils.getStringByArray(((List)msg).toArray());
                e(LOGTAG, stackInfo + ":\n" +"List-> size="+len+"  "+arrayString );
                return;
            } else if (msg instanceof Throwable) {
                e(LOGTAG, stackInfo);
                Throwable throwable = (Throwable) msg;
                ELog.e(throwable);
                return;
            } else if (msg != null && msg.getClass().isArray()) {
                String str=ArrayUtils.getStringByArray(msg);
                e(LOGTAG, stackInfo + ":\n" +"array-> "+ str);
                return;
            }else if (msg instanceof Bitmap){
               e(LOGTAG,stackInfo + ":\n" +"bitmap-> with="+((Bitmap)msg).getWidth()+" height="+((Bitmap)msg).getHeight());
               return;
            }
            e(LOGTAG, stackInfo + ":\n" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            if (msg == null || msg.length() == 0) return;
            int segmentSize = 3900;
            long length = msg.length();
            if (length <= segmentSize) {// 长度小于等于限制直接打印
                Log.e(tag, msg);
            } else {
                while (msg.length() > segmentSize) {// 循环分段打印日志
                    String logContent = msg.substring(0, segmentSize);
                    msg = msg.replace(logContent, "");
                    Log.e(tag, logContent);
                }
                Log.e(tag, msg);// 打印剩余日志
            }
        }

    }


    public static void e(Object... objects) {
        if (EmptyUtils.isNotEmpty(objects)) {
            String printStr = "";
            for (Object o : objects) {
                printStr = printStr.concat(o.toString() + "  ");
            }
            e(printStr);
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            String stackInfo = getStackInfo();
            msg = stackInfo + ":\n" + msg;

            int segmentSize = 3900;
            long length = msg.length();
            if (length <= segmentSize) {// 长度小于等于限制直接打印
                Log.i(LOGTAG, msg);
            } else {
                while (msg.length() > segmentSize) {// 循环分段打印日志
                    String logContent = msg.substring(0, segmentSize);
                    msg = msg.replace(logContent, "");
                    Log.i(LOGTAG, logContent);
                }
                Log.i(LOGTAG, msg);// 打印剩余日志
            }
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

    public static boolean isDebugMode() {
        return isDebug;
    }
}
