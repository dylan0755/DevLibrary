package com.dylan.library.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dylan on 2016/12/29.
 */

public class AppUtils {


    /**
     * @param context
     * @param assetsFileName
     * @param outputFilePath 必须是一个文件的路径，而不是文件夹的路径
     */
    public static void copyAssets2SDcard(Context context, String assetsFileName, String outputFilePath) {
        if (context == null) return;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFilePath);
            byte[] buffer = new byte[1024];
            InputStream in = context.getAssets().open(assetsFileName);
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            in.close();

            Log.e("copyAssets2SDcard: ", "测试");
            out.close();
           /* closeIO(in);
            closeIO(out);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @param context
     * @param apkPath
     */
    public static void installApp(Context context, String apkPath) {
        try {
            if (context == null) {
                new NullPointerException("param context is a  empty reference");
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(apkPath);
            if (!file.exists()) return;
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("installApp: ", "" + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void unInstallApp(Context context, String packageName) {
        if (context == null) return;
        context.startActivity(IntentUtils.getUninstallAppIntent(packageName));
    }


    /**
     * 打开App
     *
     * @param context 上下文
     */
    public static void launchApp(Context context, String packageName) {
        if (context == null) return;
        if (StringUtils.isEmpty(packageName)) return;
        context.startActivity(IntentUtils.getLaunchAppIntent(context, packageName));
    }


    /**
     * 打开App
     *
     * @param activity    activity
     * @param requestCode 请求值
     */

    public static void launchApp(Activity activity, String packageName, int requestCode) {
        if (activity == null) return;
        if (StringUtils.isEmpty(packageName)) return;
        activity.startActivityForResult(IntentUtils.getLaunchAppIntent(activity, packageName), requestCode);

    }

    /**
     * Activity从后台回到前台，后台不存在则创建
     *
     * @param context
     * @param aClass
     */
    public static void bringActivityToFront(Context context, Class aClass) {
        Intent intent = new Intent(context, aClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    /**
     * Activity从后台回到前台，后台不存在则创建
     *
     * @param context
     * @param aClass
     */
    public static void bringActivityToFront(Context context, String bundleKey, Bundle bunldeValue, Class aClass) {
        Intent intent = new Intent(context, aClass);
        intent.putExtra(bundleKey, bunldeValue);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }


    public static void shareText(Context context, String content) {
        if (context == null) return;
        context.startActivity(IntentUtils.getShareTextIntent(content));
    }


    public static void shareImage(Context context, String content, Uri uri) {
        if (context == null) return;
        if (uri == null) return;
        context.startActivity(IntentUtils.getShareImageIntent(content, uri));
    }


    /**
     * 获取App版本号
     *
     * @param context 上下文
     * @return App版本号
     */

    public static String getAppVersionName(Context context) {
        if (context == null) return "";
        String packageName = context.getPackageName();
        if (StringUtils.isEmpty(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取App版本码
     *
     * @param context 上下文
     * @return App版本码
     */

    public static int getAppVersionCode(Context context) {
        if (context == null) return -1;
        String packageName = context.getPackageName();
        if (StringUtils.isEmpty(packageName)) return -1;
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
     * 获取App图标
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App图标
     */
    public static Drawable getAppIcon(Context context, String packageName) {
        if (context == null) return null;
        if (StringUtils.isEmpty(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getAppName(Context context) {
        if (context == null) return "";
        CharSequence label = context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
        if (label != null) return label.toString();
        else return "";
    }


    /**
     * 静默安装App
     * <p>
     * <p>非root需添加权限 {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param filePath 文件路径
     * @return {@code true}: 安装成功<br>{@code false}: 安装失败
     */

    public static boolean installAppSilent(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return false;
        String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install " + filePath;
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, !isSystemApp(context), true);
        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success");

    }

    public static boolean isAppRoot() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("echo root", true);
        if (result.result == 0) return true;
        if (result.errorMsg != null) {
            Log.e("isAppRoot", result.errorMsg);
        }
        return false;
    }

    /**
     * 判断App是否是系统应用
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSystemApp(Context context) {
        if (context == null) return false;
        String packageName = context.getPackageName();
        if (StringUtils.isEmpty(packageName)) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 跳到权限设置页面
     */
    public static void gotoPermission(Context context) {
        if (context == null) return;
        try {
            if (RomUtils.isFlyme()) {
                Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.putExtra("packageName", context.getPackageName());
                context.startActivity(intent);
                return;
            } else if (RomUtils.isMIUI()) {
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.setComponent(componentName);
                intent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(intent);
                return;
            } else if (RomUtils.isEMUI()) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("packageName", context.getPackageName());
                ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
                intent.setComponent(comp);
                context.startActivity(intent);
                return;
            }
        } catch (Exception e) {
            gotoApplicationSetting(context);
            return;
        }
        gotoApplicationSetting(context);
    }

    /**
     * 跳到应用信息页面
     *
     * @param context
     */
    public static void gotoApplicationSetting(Context context) {
        Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        String pkg = "com.android.settings";
        String cls = "com.android.settings.applications.InstalledAppDetails";
        i.setComponent(new ComponentName(pkg, cls));
        i.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(i);
    }


    public List<PackageInfo> getALLInstallApp(Context context) {
        if (context == null) return Collections.emptyList();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        return list;
    }

}
