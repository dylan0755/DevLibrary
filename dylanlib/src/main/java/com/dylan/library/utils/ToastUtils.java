package com.dylan.library.utils;

import android.app.Application;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.thread.ThreadUtils;
import com.hjq.toast.IToastInterceptor;
import com.hjq.toast.IToastStrategy;
import com.hjq.toast.IToastStyle;
import com.hjq.toast.ToastMsg;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dylan on 2016/4/16.
 */

public class ToastUtils {
    private static Toast longToast = null;
    private static Toast centerLongToast = null;
    private static Application applicationContext;

    public static void initToast(Application application) {
        HJQToastUtils.init(application);
        if (ThreadUtils.isMainThread()) {
            applicationContext = application;
            longToast = Toast.makeText(applicationContext, "", Toast.LENGTH_LONG);
            centerLongToast = Toast.makeText(applicationContext, "", Toast.LENGTH_LONG);
            centerLongToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            Log.e("HJQToastUtils ", "Can't create handler inside thread that has not called Looper.prepare()");
        }
    }


    private static void setGravityBottom() {
        HJQToastUtils.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ScreenUtils.getScreenHeight(applicationContext) / 8);
    }

    public static void show(String msg) {
        if (applicationContext == null) return;
        setGravityBottom();
        ToastMsg toastMsg=new ToastMsg();
        toastMsg.setText(msg);
        toastMsg.setDuration(IToastStrategy.SHORT_DURATION_TIMEOUT);
        HJQToastUtils.show(toastMsg);
    }

    public static void showLong(String msg) {
        if (applicationContext == null) return;
//        longToast.setText(msg);
//        longToast.show();
        setGravityBottom();
        ToastMsg toastMsg=new ToastMsg();
        toastMsg.setText(msg);
        toastMsg.setDuration(IToastStrategy.LONG_DURATION_TIMEOUT);
        HJQToastUtils.show(toastMsg);

    }


    public static void showCenterShort(String msg) {
        if (applicationContext == null) return;
        HJQToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastMsg toastMsg=new ToastMsg();
        toastMsg.setText(msg);
        toastMsg.setDuration(IToastStrategy.SHORT_DURATION_TIMEOUT);
        HJQToastUtils.show(toastMsg);
    }

    public static void showCenterLong(String string) {
        if (applicationContext == null) return;
//        centerLongToast.setText(string);
//        centerLongToast.show();
        HJQToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastMsg toastMsg=new ToastMsg();
        toastMsg.setText(string);
        toastMsg.setDuration(IToastStrategy.LONG_DURATION_TIMEOUT);
        HJQToastUtils.show(toastMsg);
    }


    public static void showLongToast(String text, final int durationMills) {
        if (applicationContext == null) return;
        setGravityBottom();
        ToastMsg toastMsg=new ToastMsg();
        toastMsg.setText(text);
        toastMsg.setDuration(durationMills);
        HJQToastUtils.show(toastMsg);
    }




    /**
     * 取消吐司的显示
     */
    public static synchronized void cancel() {
        HJQToastUtils.cancel();
    }

    /**
     * 设置吐司的位置
     *
     * @param gravity 重心
     * @param xOffset x轴偏移
     * @param yOffset y轴偏移
     */
    public static void setGravity(int gravity, int xOffset, int yOffset) {
        HJQToastUtils.setGravity(gravity, xOffset, yOffset);
    }

    /**
     *
     */
    public static void setView(int id) {
        HJQToastUtils.setView(id);
    }

    public static void setView(View view) {
        HJQToastUtils.setView(view);
    }

    /**
     * 获取当前 Toast 的视图
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> V getView() {
        return HJQToastUtils.getView();
    }

    /**
     * 初始化全局的Toast样式
     */
    public static void initStyle(IToastStyle style) {
        HJQToastUtils.initStyle(style);
    }

    /**
     * 设置当前Toast对象
     */
    public static void setToast(Toast toast) {
        HJQToastUtils.setToast(toast);
    }

    /**
     * 设置 Toast 显示策略
     */
    public static void setToastStrategy(IToastStrategy strategy) {
        HJQToastUtils.setToastStrategy(strategy);
    }

    /**
     * 设置 Toast 拦截器（可以根据显示的内容决定是否拦截这个Toast）
     * 场景：打印 Toast 内容日志、根据 Toast 内容是否包含敏感字来动态切换其他方式显示（这里可以使用我的另外一套框架 XToast）
     */
    public static void setToastInterceptor(IToastInterceptor interceptor) {
        HJQToastUtils.setToastInterceptor(interceptor);
    }

    /**
     * 获取当前Toast对象
     */
    public static Toast getToast() {
        return HJQToastUtils.getToast();
    }


}
