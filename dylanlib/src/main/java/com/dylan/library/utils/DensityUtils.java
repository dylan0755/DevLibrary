package com.dylan.library.utils;


import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

public class DensityUtils {
    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;

    /**
     *  Activity#onCreate  中使用
     */
    public static void setCustomDensity(Activity activity, final Application application) {
        final DisplayMetrics applicationDisplayMetrics = application.getResources().getDisplayMetrics();
        if (sNoncompatDensity == 0) {
            sNoncompatDensity = applicationDisplayMetrics.density;
            sNoncompatScaledDensity = applicationDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() { }
            });
        }

        //以 640*360 宽度 为标准， 宽度 360
        final float targetDenisty = applicationDisplayMetrics.widthPixels * 1.0f / 360;
        final float targetScaledDensity = targetDenisty * (sNoncompatScaledDensity / sNoncompatDensity);
        final int targetDensityDpi = (int) (160 * targetDenisty);
        applicationDisplayMetrics.density = targetDenisty;
        applicationDisplayMetrics.scaledDensity = targetScaledDensity;
        applicationDisplayMetrics.densityDpi = targetDensityDpi;

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDenisty;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;


    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


}
