package com.dylan.library.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Dylan on 2017/11/4.
 */

public class PermissionUtils {

    public static final int REQUEST_PER_CAMERA_WRITE =101;
    public static final int REQUEST_PER_EXTERNAL_STORAGE =102;


    public static boolean hasNotCameraAndExternalWritePermission(Activity activity) {
        return !hasCameraAndExternalWritePermission(activity);
    }

    public static boolean hasCameraAndExternalWritePermission(Activity activity) {
        int hasCameraPermison = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int hasWritePermison = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //权限还没有授予，需要在这里写申请权限的代码
        if (hasCameraPermison != PackageManager.PERMISSION_GRANTED || hasWritePermison != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }


    public static boolean hasNotExternalStoragePermission(Activity activity) {
      return !hasExternalStoragePermission(activity);
    }

    public static boolean hasExternalStoragePermission(Activity activity) {
        int hasReadPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasWritePermison = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //权限还没有授予，需要在这里写申请权限的代码
        if (hasReadPermission != PackageManager.PERMISSION_GRANTED || hasWritePermison != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }


    public static void requestCameraAndExternalWrite(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PER_CAMERA_WRITE);
    }


    public static void requestExternalStorage(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PER_EXTERNAL_STORAGE);
    }
}
