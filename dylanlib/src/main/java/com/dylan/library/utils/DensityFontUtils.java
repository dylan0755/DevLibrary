package com.dylan.library.utils;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Author: Dylan
 * Date: 2022/02/23
 * Desc:
 */
public class DensityFontUtils {


    public static void setCustomActivityDensityInWidth(int dp, Activity activity, final Application application) {
        Resources res = activity.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics());
        DisplayMetrics applicationDisplayMetrics = application.getResources().getDisplayMetrics();
        float targetDenisty = (float)applicationDisplayMetrics.widthPixels * 1.0F / (float)dp;
        float targetScaledDensity = targetDenisty * 1;
        int targetDensityDpi = (int)(160.0F * targetDenisty);
        DisplayMetrics activityDisplayMetrics = res.getDisplayMetrics();
        activityDisplayMetrics.density = targetDenisty;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

}
