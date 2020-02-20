package com.dylan.library.screen;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.dylan.library.device.SystemSettings;
import com.dylan.library.exception.ELog;
import com.dylan.library.utils.RomUtils;

import java.lang.reflect.Field;

/**
 * Created by Dylan on 2016/10/15.
 */
public class ScreenUtils {

    public static int getScreenWidth(Context context) {
        if (context == null) return 0;
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        if (context == null) return 0;
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        if (statusBarHeight==0){
            statusBarHeight=getStatusBarHeight2(activity);
        }
        return statusBarHeight;
    }

    private static int getStatusBarHeight2(Context context) {
        Class<?> clazz;
        Object object;
        Field field;
        int x;
        int statusBarHeight = 0;
        try {
            clazz = Class.forName("com.android.internal.R$dimen");
            object = clazz.newInstance();
            field = clazz.getField("status_bar_height");
            x = Integer.parseInt(field.get(object).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 和setFullScreenMode的作用一致，只不过它可以在Activity任意位置使用，
     * 不需要一定是要在setContentView之前。
     */
    public static void removeStatuBar(Activity activity) {
        WindowManager.LayoutParams wlp = activity.getWindow().getAttributes();
        wlp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(wlp);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    public static void restoreStatusBar(Activity activity) {
        WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(attr);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 全屏模式，状态栏隐藏了,要在setContentView之前调用
     */
    public void setFullScreenMode(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 全屏显示，状态栏还在，布局在最顶部
     */
    public static void showInFullScreen(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup mContentParent = (ViewGroup) mContentView.getParent();
        View statusBarView = mContentParent.getChildAt(0);
        if (statusBarView != null && statusBarView.getLayoutParams() != null && statusBarView.getLayoutParams().height == getStatusBarHeight(activity)) {
            //移除假的 View
            mContentParent.removeView(statusBarView);
        }
        //ContentView 不预留空间
        if (mContentParent.getChildAt(0) != null) {
            ViewCompat.setFitsSystemWindows(mContentParent.getChildAt(0), false);
        }
        //ChildView 不预留空间
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
    }

    /**
     * 要在style.xml文件中配置
     * 设置状态栏颜色和状态栏字体颜色
     *
     * @param window
     * @param statusbarColor
     */
    public static void setStatusBarLightMode(Window window, int statusbarColor) {
        //更改状态栏和状态栏的字体颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.x系统s
            if (RomUtils.isFlyme()) { //魅族，白色背景，灰色字体
                if (StatusBarLightMode.FlymeSetStatusBarLightMode(window, true)) {
                    if (statusbarColor != 0) window.setStatusBarColor(statusbarColor);
                }
            } else if (RomUtils.isMiui()) {//小米，白色背景，灰色字体
                if (StatusBarLightMode.MIUISetStatusBarLightMode(window, true)) {
                    if (statusbarColor != 0) window.setStatusBarColor(statusbarColor);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (statusbarColor != 0) window.setStatusBarColor(statusbarColor);
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        }
    }


    public static boolean isScreenAutoRotate(Context context) {
        return SystemSettings.isScreenAutoRotate(context);
    }


    public static boolean isOrientPortrail(Activity activity) {
        Configuration mConfiguration = activity.getResources().getConfiguration();
        return mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isOrientLandscape(Activity activity) {
        Configuration mConfiguration = activity.getResources().getConfiguration();
        return mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }


    public static void switchOrientationLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }


    public static void switchOrientationPortrail(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    /**
     * 判断是否是平板
     * 这个方法是从 Google I/O App for Android 的源码里找来的，非常准确。
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }



}
