package com.dylan.library.screen;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Created by Dylan on 2017/11/13.
 */

public class NavBarUtils {


    public static void hideNavBarFromWindowAndDecorView(Window window){
        hideNavBarFromWindow(window);
        hideNavBarFromDecorView(window);
    }


    //从Window层隐藏，键盘缩下去的时候导航栏自动收缩
    public static void hideNavBarFromWindow(Window window){
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        window.setAttributes(params);
    }

    //从DecorView 层隐藏，键盘上的收缩角标按下时，键盘收缩，但监听不到，所以要和hideNavBarFromWindow 搭配使用
    public static void hideNavBarFromDecorView(Window window){
        hideNavBar(window);
    }







    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideNavBar(Window window) {
        if (window==null)return;
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = window.getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
//            View decorView = window.getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);

            fullScreenImmersive(window);
        }
    }


    public static void fullScreenImmersive(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    public static void setNotFocusableFlag(Window window){
        if (window==null)return;
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void clearNotFocusableFlag(Window window){
        if (window==null)return;
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }



    public static void showNavBar(Window window, boolean statusLightMode){
        if (window==null)return;
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = window.getDecorView();
            v.setSystemUiVisibility(View.VISIBLE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView =window.getDecorView();
            if (!statusLightMode){
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
            }else{
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decorView.setSystemUiVisibility(uiOptions);
            }

        }
    }




    public static int getNavBarHeight(Context context) {
        boolean mInPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar((Activity) context)) {
                String key;
                if (mInPortrait) {
                    key = "navigation_bar_height";
                } else {
                    key = "navigation_bar_height_landscape";
                }
                return getInternalDimensionSize(context, key);
            }
        }
        return result;
    }


    private static int getInternalDimensionSize(Context context, String key) {
        int result = 0;
        try {
            int resourceId = context.getResources().getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                result = Math.round(context.getResources().getDimensionPixelSize(resourceId) *
                        Resources.getSystem().getDisplayMetrics().density /
                        context.getResources().getDisplayMetrics().density);
            }
        } catch (Resources.NotFoundException ignored) {
            return 0;
        }
        return result;
    }





    /**
     * 检查是否存在虚拟按键栏
     * @param context
     * @return
     */
    public static boolean hasNavBar(Context context) {
        try{
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
            if (resourceId != 0) {
                boolean hasNav = res.getBoolean(resourceId);
                // check override flag
                String sNavBarOverride = getNavBarOverride();
                if ("1".equals(sNavBarOverride)) {
                    hasNav = false;
                } else if ("0".equals(sNavBarOverride)) {
                    hasNav = true;
                }
                return hasNav;
            } else { // fallback
                return !ViewConfiguration.get(context).hasPermanentMenuKey();
            }
        }catch (Exception e){

        }

        return false;

    }

    /**
     * 判断虚拟按键栏是否重写
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }
}
