package com.dylan.library.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.util.Log;

import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/9/8
 * Desc:
 */
public class AndroidManifestUtils {
    private static final String TAG = AndroidManifestUtils.class.getSimpleName();

    public static ProviderInfo[] getProviderInfo(Context context) throws PackageManager.NameNotFoundException {
        String packName = context.getPackageName();
        PackageInfo info = context.getPackageManager().getPackageInfo(packName,
                PackageManager.GET_PROVIDERS);
        ProviderInfo[] providers = info.providers;
        return providers;

    }


    public static String getFileProviderAuthority(Context context) {

        try {
            ProviderInfo[] providers = getProviderInfo(context);
            for (ProviderInfo provider : providers) {
                if ("android.support.v4.content.FileProvider".equals(provider.name)){
                    return provider.authority;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
