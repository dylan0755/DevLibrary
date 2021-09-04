package com.dylan.library.manager;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.dylan.library.io.FileUtils;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Author: Dylan
 * Date: 2020/2/23
 * Desc:
 */
public class ExternalStorageDir {
    private static final String DCIM_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    private static ExternalStorageDir mInstance;
    private String rootPath;
    private String cacheDir;
    private String filesDir;
    private String packageNameDir;
    public static String CameraRootDir = null;
    static {
        if (Build.FINGERPRINT.contains("Flyme")
                || Pattern.compile("Flyme", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("Meizu")
                || Build.MANUFACTURER.contains("MeiZu")) {
            CameraRootDir = DCIM_FILE_PATH + File.separator + "Video" + File.separator;
        } else if (Build.FINGERPRINT.contains("vivo")
                || Pattern.compile("vivo", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("vivo")
                || Build.MANUFACTURER.contains("Vivo")) {
            CameraRootDir = Environment.getExternalStoragePublicDirectory("") + File.separator + "相机" + File.separator;
        } else {
            CameraRootDir =DCIM_FILE_PATH + File.separator + "Camera" + File.separator;
        }
        FileUtils.createDirIfNotExists(CameraRootDir);
    }

    public ExternalStorageDir(Context context){
        rootPath = Environment.getExternalStorageDirectory().toString();
        cacheDir = rootPath +"/"+"Android/data/"+context.getPackageName()+"/cache";
        filesDir = rootPath +"/"+"Android/data/"+context.getPackageName()+"/files";
        packageNameDir=rootPath+"/"+"Android/data/"+context.getPackageName();
    }


    public  static ExternalStorageDir getInstance(Context context){
        if (mInstance==null){
            synchronized (ExternalStorageDir.class){
                if (mInstance==null){
                    mInstance=new ExternalStorageDir(context);
                    return mInstance;
                }
            }
        }
        return mInstance;
    }


    public String getRootPath() {
        return rootPath;
    }



    public String getCacheDir() {
        return cacheDir;
    }



    public String getFilesDir() {
        return filesDir;
    }



    public String getPackageNameDir() {
        return packageNameDir;
    }

}
