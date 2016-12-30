package com.dylan.library.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

/**
 * Created by Dylan on 2016/12/29.
 */

public class AppUtils {
    /**

     * 静默安装App

     * <p>非root需添加权限 {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>

     *

     * @param filePath 文件路径

     * @return {@code true}: 安装成功<br>{@code false}: 安装失败

     */

    public static boolean installAppSilent(Context context,String filePath) {

        File file = new File(filePath);
        if (!file.exists()) return false;

        String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install " + filePath;

        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, !isSystemApp(context), true);

        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success");

    }

    /**

     * 获取App版本码

     *

     * @param context 上下文

     * @return App版本码

     */

    public static int getAppVersionCode(Context context) {

        return getAppVersionCode(context, context.getPackageName());

    }



    /**

     * 获取App版本码

     *

     * @param context     上下文

     * @param packageName 包名

     * @return App版本码

     */

    public static int getAppVersionCode(Context context, String packageName) {

        if (StringUtils.isInValid(packageName)) return -1;

        try {

            PackageManager pm = context.getPackageManager();

            PackageInfo pi = pm.getPackageInfo(packageName, 0);

            return pi == null ? -1 : pi.versionCode;

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();

            return -1;

        }

    }



    /**

     * 判断App是否是系统应用

     *

     * @param context 上下文

     * @return {@code true}: 是<br>{@code false}: 否

     */

    public static boolean isSystemApp(Context context) {

        return isSystemApp(context, context.getPackageName());

    }



    /**

     * 判断App是否是系统应用
     *
     * @param context     上下文

     * @param packageName 包名

     * @return {@code true}: 是<br>{@code false}: 否

     */

    public static boolean isSystemApp(Context context, String packageName) {

        if (StringUtils.isInValid(packageName)) return false;

        try {

            PackageManager pm = context.getPackageManager();

            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);

            return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();

            return false;

        }

    }

}
