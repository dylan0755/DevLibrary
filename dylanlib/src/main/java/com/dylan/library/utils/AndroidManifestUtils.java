package com.dylan.library.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.util.Log;

import com.dylan.library.BuildConfig;

import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/9/8
 * Desc:
 */
public class AndroidManifestUtils {

    public static ProviderInfo[] getProviderInfo(Context context) throws PackageManager.NameNotFoundException {
        String packName = context.getPackageName();
        PackageInfo info = context.getPackageManager().getPackageInfo(packName,
                PackageManager.GET_PROVIDERS);
        return info.providers;

    }

    /**
     * 因为 存在app 与第三方库的 FileProvider 冲突的可能，app 重新写了子类
     * 找到是否有自定义的Provider，该自定义的Provider 是否继承 v4.content.FileProvider，
     */
    public static String getFileProviderAuthority(Context context) {

        try {
            ProviderInfo[] providers = getProviderInfo(context);
            String packageName = context.getPackageName();
            String defalutAuthority = "";
            for (ProviderInfo provider : providers) {
                if (provider.name.contains(packageName)) {
                    Class<?> classApp = Class.forName(provider.name);
                    Class<?> classV4 = Class.forName("android.support.v4.content.FileProvider");
                    if (classV4.isAssignableFrom(classApp)) {
                        defalutAuthority = provider.authority;
                        return defalutAuthority;
                    }
                }
                if ("android.support.v4.content.FileProvider".equals(provider.name)) {
                    if (provider.authority.contains(packageName)) {
                        defalutAuthority = provider.authority;
                    }
                }
            }
            return defalutAuthority;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
