package com.dylan.library.manager;

import android.content.Context;
import android.os.Environment;

/**
 * Author: Dylan
 * Date: 2020/2/23
 * Desc:
 */
public class ExternalStorageDir {
    private static ExternalStorageDir mInstance;
    private String rootPath;
    private String cacheDir;
    private String filesDir;
    private String packageNameDir;

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
