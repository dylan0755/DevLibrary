package com.dylan.library.device;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.dylan.library.exception.ELog;

/**
 * Author: Dylan
 * Date: 2020/2/16
 * Desc:
 */
public class SystemSettings {

    public static boolean isScreenAutoRotate(Context context) {
        int gravity = 0;
        try {
            gravity = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            ELog.e(e);
        }
        return gravity==1;
    }



    public static int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }
}
