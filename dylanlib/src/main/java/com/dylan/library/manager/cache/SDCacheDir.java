package com.dylan.library.manager.cache;

import android.content.Context;
import android.os.Environment;

/**
 * Created by Dylan on 2016/10/12.
 */

@Deprecated
public class SDCacheDir {
    private static SDCacheDir mInstance;
    @Deprecated
    private String sdpath;
    @Deprecated
    public String cachepath;
    @Deprecated
    public String filesDir;
    @Deprecated


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
