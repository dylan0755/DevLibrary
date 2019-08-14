package com.dylan.library.graphics;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dylan.library.io.FileUtils;
import com.dylan.library.net.UrlUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * Author : Dylan
 * Time   : 2019/08/13
 */

public class BitmapDownloader {
    private  String cacheParentPath ;
    private  SharedPreferences bitmapCachePreference;


    public static BitmapDownloader bitmapDownloader;
    private Application applicationContext;

    private BitmapDownloader(Application application) {
        applicationContext = application;
        bitmapCachePreference= application.getSharedPreferences("BitmapDownloadCache", Context.MODE_PRIVATE);
        cacheParentPath=application.getCacheDir()+"/bitmap_download_disk_cache";
    }


    public static  BitmapDownloader getInstance(Application application) {
        if (bitmapDownloader == null) {
            synchronized(BitmapDownloader.class){
                bitmapDownloader = new BitmapDownloader(application);
            }
        }
        return bitmapDownloader;
    }


    public void downloadOnSubThread(final String picurl, final BitmapDownLoadListener listener) {
        if (EmptyUtils.isEmpty(picurl)) return;
        String cacheFilePath = bitmapCachePreference.getString(picurl, null);
        if (EmptyUtils.isNotEmpty(cacheFilePath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(cacheFilePath);
            if (bitmap != null) {
                listener.onSuccess(bitmap,this);
                return;
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL mUrl = new URL(picurl);
                    InputStream inputStream = mUrl.openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    if(listener!=null)listener.onSuccess(bitmap,BitmapDownloader.this);

                    String outPutPath = cacheParentPath +"/"+ UrlUtils.parseFileNameFormUrl(picurl);
                    try {
                        save(bitmap, outPutPath);
                        bitmapCachePreference.edit().putString(picurl, outPutPath).apply();
                      // Logger.e("缓存成功---->"+picurl);
                    } catch (IOException e) {
                        Logger.e(e);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(listener!=null)listener.onFailed(e,BitmapDownloader.this);
                }
            }
        }).start();


    }


    private void save(Bitmap bitmap, String outPath) throws IOException {
        File dirFile = new File(cacheParentPath);
        if (!dirFile.exists()) {
            boolean isCreated=dirFile.mkdirs();
            //Logger.e("cacheParentPath->isCreated: " +isCreated+"   "+cacheParentPath);
        }

        FileOutputStream fos = new FileOutputStream(outPath);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        FileUtils.notifyScanFile(applicationContext, outPath);
    }

    public void toSave(Bitmap bitmap, String outPath) throws IOException {
        File outPutFile = new File(outPath);
        if (!outPutFile.getParentFile().exists()) {
            outPutFile.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(outPath);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        FileUtils.notifyScanFile(applicationContext, outPath);
    }

    public interface BitmapDownLoadListener {
        void onSuccess(Bitmap bitmap, BitmapDownloader downloader);

        void onFailed(Exception e, BitmapDownloader downloader);
    }
}
