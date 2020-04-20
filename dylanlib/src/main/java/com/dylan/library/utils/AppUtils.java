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
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dylan on 2016/12/29.
 */

public class AppUtils {


    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }



    public static PackageInfo getPackageInfo(Context context) throws PackageManager.NameNotFoundException {
        String packageName = context.getPackageName();
        if (StringUtils.isEmpty(packageName)) return null;
        PackageManager pm = context.getPackageManager();
        return pm.getPackageInfo(packageName, 0);

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
        return RootUtil.hasRooted();
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



    public List<PackageInfo> getALLInstallApp(Context context) {
        if (context == null) return Collections.emptyList();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        return list;
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

     public static void backHome(Activity activity){
         Intent home=new Intent(Intent.ACTION_MAIN);
         home.addCategory(Intent.CATEGORY_HOME);
         activity.startActivity(home);
     }

     public static void backHome(Context context){
         Intent home=new Intent(Intent.ACTION_MAIN);
         home.addCategory(Intent.CATEGORY_HOME);
         home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         context.startActivity(home);
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


    public static void shareVideo(Context context,Uri uri){
        if (context == null) return;
        if (uri == null) return;
        context.startActivity(IntentUtils.getShareVideoIntent(uri));
    }


    public static boolean gotoInstallApk(Activity activity, String desFilePath) throws Exception {
        File desFile=new File(desFilePath);
        Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String fileProvider= AndroidManifestUtils.getFileProviderAuthority(activity);
            if (fileProvider==null||fileProvider.isEmpty()){
                throw new Exception("please set fileProvider in AndroidManifest");
            }
            installApkIntent.setDataAndType(FileProvider.getUriForFile(activity, fileProvider, desFile), "application/vnd.android.package-archive");
            installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            installApkIntent.setDataAndType(Uri.fromFile(desFile), "application/vnd.android.package-archive");
        }
        if (activity.getPackageManager().queryIntentActivities(installApkIntent, 0).size() > 0) {
            activity.startActivity(installApkIntent);
        }
        return true;
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
            } else if (RomUtils.isMiui()) {
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.setComponent(componentName);
                intent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(intent);
                return;
            } else if (RomUtils.isEmui()) {
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

    public static void gotoOpenLocationService(Context context){
        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

}
