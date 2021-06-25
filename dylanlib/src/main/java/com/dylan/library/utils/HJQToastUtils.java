package com.dylan.library.utils;

import android.app.Application;

import com.dylan.library.toast.IToastStrategy;
import com.dylan.library.toast.IToastStyle;
import com.dylan.library.toast.ToastMsg;
import com.dylan.library.toast.ToastStrategy;
import com.dylan.library.toast.style.ToastBlackStyle;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/ToastUtils
 *    time   : 2018/09/01
 *    desc   : Toast 工具类
 */
 final class HJQToastUtils {


    /** Toast 处理策略 */
    private static IToastStrategy sStrategy;


    /**
     * 不允许被外部实例化
     */
    private HJQToastUtils() {}

    /**
     * 初始化 Toast，需要在 Application.create 中初始化
     *
     * @param application       应用的上下文
     */
    public static void init(Application application) {
        init(application, new ToastBlackStyle(application));
    }

    /**
     * 初始化 Toast 及样式
     */
    public static void init(Application application, IToastStyle style) {
        sStrategy=new ToastStrategy(application);

    }



    /**
     * 显示一个吐司
     */
    public static synchronized void show(ToastMsg toastMsg) {
        sStrategy.show(toastMsg);
    }

    /**
     * 取消吐司的显示
     */
    public static synchronized void cancel() {
        sStrategy.cancel();
    }




}