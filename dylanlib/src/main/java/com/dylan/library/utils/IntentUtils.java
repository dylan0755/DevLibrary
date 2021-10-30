package com.dylan.library.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.dylan.library.exception.ELog;

import java.util.List;

/**
 * Created by Dylan on 2016/12/31.
 */

public class IntentUtils {




    public static Intent getShareTextIntent(String content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        Intent intent=Intent.createChooser(shareIntent,"");
        return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }


    public static Intent getShareImageIntent(String content, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        Intent intent=Intent.createChooser(shareIntent,"");
        return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }


    public static Intent getShareVideoIntent(Uri uri){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("video/*");
        Intent intent=Intent.createChooser(shareIntent,"");
        return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 获取关机的意图
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.SHUTDOWN"/>}</p>
     * @return intent
     */

    public static Intent getShutdownIntent() {
        Intent intent = new Intent(Intent.ACTION_SHUTDOWN);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**

     * 获取打开App的意图

     *

     * @param context     上下文

     * @param packageName 包名

     * @return intent

     */

    public static Intent getLaunchAppIntent(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }


    public static Intent getLaunchIntentFromBackToFront(Context context, String className) {
        Intent launchIntent=null;
        if (EmptyUtils.isEmpty(className)) {
            try {
                launchIntent=context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
                ELog.e(e);
            }
        }



        //获取ActivityManager
        ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);


        //场景一：关闭App回到桌面，栈中没有Activity实例的情况
        if (EmptyUtils.isEmpty(taskList)||(taskList.size()==1&&!taskList.get(0).topActivity.getPackageName().equals(context.getPackageName()))){
            launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.putExtra("bringAppToFront",true);
            ComponentName cn = new ComponentName(context.getPackageName(), className);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchIntent.setComponent(cn);
            return launchIntent;
        }

        //场景二：直接回到桌面，栈中有Activity实例
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            Logger.e(rti.topActivity.getPackageName()+"   "+context.getPackageName());
            if (rti.topActivity.getPackageName().equals(context.getPackageName())) {
                launchIntent= new Intent(Intent.ACTION_MAIN);
                ComponentName cn = new ComponentName(context.getPackageName(), EmptyUtils.isEmpty(className) ? rti.topActivity.getClassName() : className);
                if (ContextUtils.getActivity(context) == null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
                launchIntent.setComponent(cn);
                return launchIntent;
            }
        }

        return launchIntent;
    }




    /**
     * 获取卸载App的意图
     * @param packageName 包名
     * @return intent
     */

    public static Intent getUninstallAppIntent(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    }


    /**
     * 拨打电话
     * @param phoneNumber
     * @return
     */
    public static Intent getPhoneCallIntent(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber));
        return intent;
    }


    /**
     * 调起拨号页面
     */


    public static Intent getDialIntent(String phoneNumber){
        Uri uri = Uri.parse("tel:"+phoneNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        return intent;
    }

    //打开系统相册App
    public static Intent getSystemAppGallery(Context context){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        PackageManager packageManager =context.getPackageManager();
        List<ResolveInfo> infos =packageManager.queryIntentActivities(intent, PackageManager.MATCH_SYSTEM_ONLY);
        String targetPack = null;
        if (infos.size() == 1) {
            targetPack = infos.get(0).activityInfo.packageName;
        } else {
            for (ResolveInfo info : infos) {
                String packName = info.activityInfo.packageName;
                if (packName != null && packName.contains("gallery")) {
                    targetPack = packName;
                }
            }
            if (EmptyUtils.isEmpty(targetPack))
                targetPack = infos.get(0).activityInfo.packageName;
        }
        intent = getLaunchAppIntent(context, targetPack);
        return intent;
    }


    public static List<ResolveInfo> queryIntentActivities(Context context,Intent intent){
        return queryIntentActivities(context,intent, PackageManager.MATCH_ALL);
    }

    public static List<ResolveInfo> queryIntentActivities(Context context,Intent intent,int flag){
        PackageManager packageManager =context.getPackageManager();
        return packageManager.queryIntentActivities(intent, flag);
    }




    public static Intent getMarketIntent(Context context){
       AppMarket appMarket=new AppMarket();
       return appMarket.getMarketIntent(context);
    }


}
