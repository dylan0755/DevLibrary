package com.dylan.library.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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

}
