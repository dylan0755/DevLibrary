package com.dylan.library.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;

/**
 * Created by Dylan on 2017/11/4.
 */

public class PermissionUtils {
    public static final int REQUEST_PER_CAMERA = 100;
    public static final int REQUEST_PER_CAMERA_WRITE = 101;
    public static final int REQUEST_PER_EXTERNAL_STORAGE = 102;
    public static final int REQUEST_PER_DRAW_OVER_LAY = 103;
    public static final int REQUEST_PER_RECORD_AUDIO = 104;

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

    public static boolean hasCameraPermission(Activity activity) {
        int hasCameraPerm = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        //权限还没有授予，需要在这里写申请权限的代码
        if (hasCameraPerm != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasNotCameraPermission(Activity activity) {
        return !hasCameraPermission(activity);
    }


    public static boolean hasRecordAudioPermission(Activity activity) {
        int hasRecordAudioPerm = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        //权限还没有授予，需要在这里写申请权限的代码
        if (hasRecordAudioPerm != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasDrawOverlaysPermission(Context context) {
        Boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Settings.class;
                Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (Boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static boolean hasNotDrawOverlaysPermission(Context context) {
        return !hasDrawOverlaysPermission(context);
    }

    public static boolean hasWriteSecureSettingsPermission(Context context) {
        int hasSecurePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SECURE_SETTINGS);
        return hasSecurePerm == 0;
    }


    //申请权限
    public static void startDrawOverlaysActivityForResult(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, REQUEST_PER_DRAW_OVER_LAY);
    }

    public static void requestCameraAndExternalWrite(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PER_CAMERA_WRITE);
    }

    public static void requestCameraPerm(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA}, REQUEST_PER_CAMERA);

    }

    public static void requestRecordAudioPerm(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PER_RECORD_AUDIO);
    }

    public static void requestExternalStorage(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PER_EXTERNAL_STORAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isGrantAndroidData(Context context) {
        for (UriPermission persistedUriPermission : context.getContentResolver().getPersistedUriPermissions()) {
            if (persistedUriPermission.getUri().toString().
                    equals("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata")) {
                return true;
            }
        }
        return false;
    }


    private void showAuthGuideDialog() {


    }

}
