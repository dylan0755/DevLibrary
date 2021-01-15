package com.dylan.library.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.dylan.library.exception.ELog;

import java.util.List;

/**
 * Created by Dylan on 2017/10/9.
 */

public class Logger {
    private static final int LOG_TYPE_DEBUG = 0;
    private static final int LOG_TYPE_WARN = 1;
    private static final int LOG_TYPE_INFO = 2;
    private static final int LOG_TYPE_ERROR = 3;
    private static String LOGTAG = "logTag:";
    private static boolean isDebug = true;

    public static void d(Object msg) {
        log(LOG_TYPE_DEBUG, LOGTAG, msg);
    }

    public static void d(String tag, Object msg) {
        log(LOG_TYPE_DEBUG, tag, msg);
    }
    public static void d(Object... objects) {
        if (EmptyUtils.isNotEmpty(objects)) {
            String printStr = "";
            for (Object o : objects) {
                printStr = printStr.concat(o.toString() + "  ");
            }
            log(LOG_TYPE_DEBUG, LOGTAG, printStr);
        }
    }




    public static void e(Object msg) {
        log(LOG_TYPE_ERROR, LOGTAG, msg);
    }

    public static void e(String tag, Object msg) {
        log(LOG_TYPE_ERROR, tag, msg);
    }

    public static void e(Object... objects) {
        if (EmptyUtils.isNotEmpty(objects)) {
            String printStr = "";
            for (Object o : objects) {
                printStr = printStr.concat(o.toString() + "  ");
            }
            log(LOG_TYPE_ERROR, LOGTAG, printStr);
        }
    }


    public static void w(Object msg) {
        log(LOG_TYPE_WARN, LOGTAG, msg);
    }

    public static void w(String tag, Object msg) {
        log(LOG_TYPE_WARN, tag, msg);
    }

    public static void w(Object... objects) {
        if (EmptyUtils.isNotEmpty(objects)) {
            String printStr = "";
            for (Object o : objects) {
                printStr = printStr.concat(o.toString() + "  ");
            }
            log(LOG_TYPE_WARN, LOGTAG, printStr);
        }
    }


    public static void i(Object msg) {
        log(LOG_TYPE_INFO, LOGTAG, msg);
    }

    public static void i(String tag, Object msg) {
        log(LOG_TYPE_INFO, tag, msg);
    }

    public static void i(Object... objects) {
        if (EmptyUtils.isNotEmpty(objects)) {
            String printStr = "";
            for (Object o : objects) {
                printStr = printStr.concat(o.toString() + "  ");
            }
            log(LOG_TYPE_INFO, LOGTAG, printStr);
        }
    }


    private static void log(int logType, String tag, Object msg) {
        if (isDebug) {
            String stackInfo = getStackInfo();

            if (msg instanceof List) {
                int len = ((List) msg).size();
                String arrayString = ArrayUtils.getStringByArray(((List) msg).toArray());
                logString(logType, tag, stackInfo + ":\n" + "List-> size=" + len + "  " + arrayString);
                return;
            } else if (msg instanceof Throwable) {
                logString(logType, tag, stackInfo);
                Throwable throwable = (Throwable) msg;
                ELog.e(throwable);
                return;
            } else if (msg != null && msg.getClass().isArray()) {
                String str = ArrayUtils.getStringByArray(msg);
                logString(logType, tag, stackInfo + ":\n" + "array-> " + str);
                return;
            } else if (msg instanceof Bitmap) {
                logString(logType, tag, stackInfo + ":\n" + "bitmap-> with=" + ((Bitmap) msg).getWidth() + " height=" + ((Bitmap) msg).getHeight());
                return;
            }
            logString(logType, tag, stackInfo + ":\n" + msg);
        }
    }


    public static void logString(int logType, String tag, String msg) {
        if (isDebug) {
            if (msg == null || msg.length() == 0) return;
            int segmentSize = 3900;
            long length = msg.length();
            if (length <= segmentSize) {// 长度小于等于限制直接打印
                if (logType == LOG_TYPE_DEBUG) {
                    Log.d(tag, msg);
                } else if (logType == LOG_TYPE_WARN) {
                    Log.w(tag, msg);
                } else if (logType == LOG_TYPE_INFO) {
                    Log.i(tag, msg);
                } else {
                    Log.e(tag, msg);
                }
            } else {
                while (msg.length() > segmentSize) {// 循环分段打印日志
                    String logContent = msg.substring(0, segmentSize);
                    msg = msg.replace(logContent, "");
                    if (logType == LOG_TYPE_DEBUG) {
                        Log.d(tag, msg);
                    } else if (logType == LOG_TYPE_WARN) {
                        Log.w(tag, msg);
                    } else if (logType == LOG_TYPE_INFO) {
                        Log.i(tag, msg);
                    } else {
                        Log.e(tag, msg);
                    }
                }
                // 打印剩余日志
                if (logType == LOG_TYPE_DEBUG) {
                    Log.d(tag, msg);
                } else if (logType == LOG_TYPE_WARN) {
                    Log.w(tag, msg);
                } else if (logType == LOG_TYPE_INFO) {
                    Log.i(tag, msg);
                } else {
                    Log.e(tag, msg);
                }
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
