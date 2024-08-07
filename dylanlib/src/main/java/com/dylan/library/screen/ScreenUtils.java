package com.dylan.library.screen;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

import android.util.DisplayMetrics;
import android.util.Size;
import android.view.DisplayCutout;
import android.view.OrientationEventListener;
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
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        if (context == null) return 0;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Size getScreenSize(Context context) {
        if (context == null) return new Size(0, 0);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return new Size(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        if (statusBarHeight == 0) {
            statusBarHeight = getStatusBarHeight2(activity);
        }
        if (statusBarHeight == 0) {
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
            }
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
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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


    public static void showInFullScreen(Window window) {
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

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
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * 判断是否是刘海屏
     *
     * @return
     */
    public static boolean hasNotchScreen(Activity activity) {
        if (getInt("ro.miui.notch", activity) == 1 || hasNotchAtHuawei(activity) || hasNotchAtOPPO(activity) || hasNotchAtVivo(activity) || isAndroidP(activity) != null) { //TODO 各种品牌
            return true;
        }

        return false;
    }

    /**
     * Android P 刘海屏判断
     *
     * @param activity
     * @return
     */
    private static DisplayCutout isAndroidP(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        if (decorView != null && android.os.Build.VERSION.SDK_INT >= 28) {
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets != null) return windowInsets.getDisplayCutout();
        }
        return null;
    }

    /**
     * 小米刘海屏判断.
     *
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    private static int getInt(String key, Activity activity) {
        int result = 0;
        if (RomUtils.isMiui()) {
            try {
                ClassLoader classLoader = activity.getClassLoader();
                @SuppressWarnings("rawtypes") Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes") Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
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
     * 华为刘海屏判断
     *
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
            Logger.e("hasNotchAtHuawei Exception");
        } finally {
            return ret;
        }
    }

    public static final int VIVO_NOTCH = 0x00000020;//是否有刘海
    public static final int VIVO_FILLET = 0x00000008;//是否有圆角

    /**
     * VIVO刘海屏判断
     *
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
            Logger.e("hasNotchAtVivo ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Logger.e("hasNotchAtVivo NoSuchMethodException");
        } catch (Exception e) {
            Logger.e("hasNotchAtVivo Exception");
        } finally {
            return ret;
        }
    }

    /**
     * OPPO刘海屏判断
     *
     * @return
     */
    private static boolean hasNotchAtOPPO(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    public static String getScreenParams(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int heightPixels = dm.heightPixels;//高的像素
        int widthPixels = dm.widthPixels;//宽的像素
        int densityDpi = dm.densityDpi;//dpi
        float xdpi = dm.xdpi;//xdpi
        float ydpi = dm.ydpi;//ydpi
        float density = dm.density;//density=dpi/160,密度比
        float scaledDensity = dm.scaledDensity;//scaledDensity=dpi/160 字体缩放密度比
        float heightDP = heightPixels / density;//高度的dp
        float widthDP = widthPixels / density;//宽度的dp
        String str = "heightPixels: " + heightPixels + "px";
        str += "\nwidthPixels: " + widthPixels + "px";
        str += "\ndensityDpi: " + densityDpi + "dpi";
        str += "\nxdpi: " + xdpi + "dpi";
        str += "\nydpi: " + ydpi + "dpi";
        str += "\ndensity: " + density;
        str += "\nscaledDensity: " + scaledDensity;
        str += "\nheightDP: " + heightDP + "dp";
        str += "\nwidthDP: " + widthDP + "dp";
        return str;
    }

    public static class OrientHelper {
        private OrientationEventListener mOrientationListener;

        public void registerOrientListener(Context context, OrientationListener listener) {
            mOrientationListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
                @Override
                public void onOrientationChanged(int orientation) {
                    if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                        return;  //手机平放时，检测不到有效的角度
                    }
                    int degree = 0;
                    //可以根据不同角度检测处理，这里只检测四个角度的改变
                    if (orientation > 350 || orientation < 10) { //0度
                        degree = 0;
                    } else if (orientation > 80 && orientation < 100) { //90度
                        degree = 90;
                    } else if (orientation > 170 && orientation < 190) { //180度
                        degree = 180;
                    } else if (orientation > 260 && orientation < 280) { //270度
                        degree = 270;
                    } else {
                        return;
                    }
                    if (listener != null) listener.onOrientationChanged(orientation, degree);

                }
            };
            if (mOrientationListener.canDetectOrientation()) {
                mOrientationListener.enable();
            } else {
                mOrientationListener.disable();
            }

        }


        public void unRegisterListener() {
            if (mOrientationListener != null) mOrientationListener.disable();
        }

    }


    public interface OrientationListener {
        void onOrientationChanged(int orientation, int degree);
    }

}
