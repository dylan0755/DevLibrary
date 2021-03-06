package com.dylan.library.screen;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.dylan.library.device.SystemSettings;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.RomUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
     * ???setFullScreenMode???????????????????????????????????????Activity?????????????????????
     * ????????????????????????setContentView?????????
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
     * ?????????????????????????????????,??????setContentView????????????
     */
    public void setFullScreenMode(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * ???????????????????????????????????????????????????
     */
    public static void showInFullScreen(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup mContentParent = (ViewGroup) mContentView.getParent();
        View statusBarView = mContentParent.getChildAt(0);
        if (statusBarView != null && statusBarView.getLayoutParams() != null && statusBarView.getLayoutParams().height == getStatusBarHeight(activity)) {
            //???????????? View
            mContentParent.removeView(statusBarView);
        }
        //ContentView ???????????????
        if (mContentParent.getChildAt(0) != null) {
            ViewCompat.setFitsSystemWindows(mContentParent.getChildAt(0), false);
        }
        //ChildView ???????????????
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
    }


    public static void showInFullScreen(Window window){
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }



    /**
     * ??????style.xml???????????????
     * ?????????????????????????????????????????????
     *
     * @param window
     * @param statusbarColor
     */
    public static void setStatusBarLightMode(Window window, int statusbarColor) {
        //??????????????????????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.x??????s
            if (RomUtils.isFlyme()) { //????????????????????????????????????
                if (StatusBarLightMode.FlymeSetStatusBarLightMode(window, true)) {
                    if (statusbarColor != 0) window.setStatusBarColor(statusbarColor);
                }
            } else if (RomUtils.isMiui()) {//????????????????????????????????????
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
     * ?????????????????????
     * ?????????????????? Google I/O App for Android ???????????????????????????????????????
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }





    /**
     * ????????????????????????
     * @return
     */
    public static boolean hasNotchScreen(Activity activity){
        if (getInt("ro.miui.notch",activity) == 1 || hasNotchAtHuawei(activity) || hasNotchAtOPPO(activity)
                || hasNotchAtVivo(activity) || isAndroidP(activity) != null){ //TODO ????????????
            return true;
        }

        return false;
    }

    /**
     * Android P ???????????????
     * @param activity
     * @return
     */
    private static DisplayCutout isAndroidP(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        if (decorView != null && android.os.Build.VERSION.SDK_INT >= 28){
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets != null)
                return windowInsets.getDisplayCutout();
        }
        return null;
    }

    /**
     * ?????????????????????.
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    private static int getInt(String key,Activity activity) {
        int result = 0;
        if (RomUtils.isMiui()){
            try {
                ClassLoader classLoader = activity.getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //????????????
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //??????
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * ?????????????????????
     * @return
     */
    private static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Logger.e("hasNotchAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Logger.e("hasNotchAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Logger.e( "hasNotchAtHuawei Exception");
        } finally {
            return ret;
        }
    }

    public static final int VIVO_NOTCH = 0x00000020;//???????????????
    public static final int VIVO_FILLET = 0x00000008;//???????????????

    /**
     * VIVO???????????????
     * @return
     */
    private static boolean hasNotchAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
            Logger.e( "hasNotchAtVivo ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Logger.e(  "hasNotchAtVivo NoSuchMethodException");
        } catch (Exception e) {
            Logger.e(  "hasNotchAtVivo Exception");
        } finally {
            return ret;
        }
    }
    /**
     * OPPO???????????????
     * @return
     */
    private static boolean hasNotchAtOPPO(Context context) {
        return  context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }



}
