package com.dylan.library.manager.cache;

import android.content.Context;
import android.os.Environment;

/**
 * Created by Dylan on 2016/10/12.
 */
public class SDCacheDir {
    private static SDCacheDir mInstance;
    private String sdpath;
    public String cachepath;
    public String filesDir;

    public SDCacheDir(Context context){
        sdpath=Environment.getExternalStorageDirectory().toString();
        cachepath=sdpath+"/"+"Android/data/"+context.getPackageName()+"/cache";
        filesDir =sdpath+"/"+"Android/data/"+context.getPackageName()+"/files";
    }


    public  static SDCacheDir getInstance(Context context){
        if (mInstance==null){
            synchronized (SDCacheDir.class){
                if (mInstance==null){
                    mInstance=new SDCacheDir(context);
                    return mInstance;
                }
            }
        }
        return mInstance;
    }
}
