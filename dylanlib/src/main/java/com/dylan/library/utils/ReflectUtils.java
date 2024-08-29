package com.dylan.library.utils;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Field;

/**
 * Author: Dylan
 * Date: 2023/5/7
 * Desc:
 */
public class ReflectUtils {
    public static Application getApplication() {
        try {
            return (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return  (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static  Handler getMainThreadHandler(){
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            // 其实这货就是ActivityThread.mH这个贼重要的Handler(用于处理各种Client端的组件与系统服务端进行消息交互等等)
            Field field = clazz.getDeclaredField("sMainThreadHandler");
            field.setAccessible(true);
            Handler sMainThreadHandler = (Handler) field.get(null);
            return sMainThreadHandler;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Handler(Looper.getMainLooper());
    }

}