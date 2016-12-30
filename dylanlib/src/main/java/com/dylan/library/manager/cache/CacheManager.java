package com.dylan.library.manager.cache;

import android.content.Context;
import android.text.format.Formatter;

import com.dylan.library.utils.StringUtils;

import java.io.File;

/**
 * Created by Dylan on 2016/9/27.
 */
public class CacheManager {
    private static CacheManager cacheManager;
    private static long totalCacheSize;

    private CacheManager() {

    }

    public  static CacheManager getInstance() {
        synchronized (cacheManager) {
            if (cacheManager == null) {
                cacheManager = new CacheManager();
            } else
                return cacheManager;

        }
        return cacheManager;
    }




    /**
     *
     * @param context
     * @param parentPaths  自定义的路径
     * @param obeserver
     */
    public static void cleanAllCache(Context context,CacheSizeObeserver  obeserver,String[] parentPaths){
        long size_long=cleanAllCache(context,parentPaths);
        String size_str= Formatter.formatFileSize(context,size_long);
        obeserver.cacheSize(size_long,size_str);
    }




    public static long cleanAllCache(Context context,String[] parentPaths){
        //清除内存下的缓存
        clearnInternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        //清除SDcard
        String cachepath= SDCacheDir.getInstance(context).cachepath;
        String filepath= SDCacheDir.getInstance(context).filesDir;
        deleteFolderFile(cachepath,false);
        deleteFolderFile(filepath,false);
        if (parentPaths!=null&&parentPaths.length>0){
            for (String path:parentPaths){
                if (path!=null&&!path.isEmpty()){
                    deleteFolderFile(path,true);
                }
            }
        }


        return  getTotalSize(context,parentPaths);
    }

    /**
     *
     * @param context
     * @param parentPaths  自定义路径的父文件路径即文件夹的路径
     * @return
     */
    public static long getTotalSize(Context context,String[] parentPaths){
        //获取内存下的缓存
        totalCacheSize=0;
        getCacheDirSize(context);
        getFileDirSize(context);
        getDataBaseFileSise(context);
        getSharePreferenceSize(context);
        //获取SDcard下的缓存
        try {
            totalCacheSize+=getFolderSize(new File(SDCacheDir.getInstance(context).cachepath));
            totalCacheSize+=getFolderSize(new File(SDCacheDir.getInstance(context).filesDir));

            if (parentPaths!=null&&parentPaths.length>0){
                for (String path:parentPaths){
                    if (path!=null&&!path.isEmpty())totalCacheSize+=getFolderSize(new File(path));//加上自定的路径
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalCacheSize;
    }




    private static void getCacheDirSize(Context context){
        File directory=context.getCacheDir();
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                totalCacheSize+=item.length();
            }
        }
    }

    private static void getFileDirSize(Context context){
        File directory=context.getFilesDir();
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                totalCacheSize+=item.length();
            }
        }
    }

    private static void getDataBaseFileSise(Context context){
        File directory=new File("/data/data/"+ context.getPackageName() + "/databases");
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                totalCacheSize+=item.length();
            }
        }
    }

    private static void getSharePreferenceSize(Context context){
        File directory=new File("/data/data/"+context.getPackageName()+"/shared_prefs");
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                totalCacheSize+=item.length();
            }
        }
    }


    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context
     */
    public static void clearnInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
     *
     * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     *
     * @param context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * * 清除/data/data/com.xxx.xxx/files下的内容 * *
     *
     * @param context
     */
    public static void cleanFiles(Context context) {
        File directory = context.getFilesDir();
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * 获取sdcard中的缓存
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (StringUtils.isValid(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
