package com.dylan.library.service;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Dylan on 2016/12/31.
 */

public class ServiceHelper {


    public  static boolean isAccessibilitySettingsOn(Context context, Class accessibilityService) {
        if (context==null)return false;
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + accessibilityService.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
           // Log.v("isAccessibilityon", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
           /** Log.e("isAccessibilityon", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());*/
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
          // ***ACCESSIBILITY IS ENABLED*** -----------------
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String serviceFilter = mStringColonSplitter.next();
                    Log.v("isAccessibilityon", "serviceFilter "+serviceFilter);
                    if (serviceFilter.equalsIgnoreCase(service))   return true;
                }
            }
        } else {
            return false;
        }

        return false;
    }
}
