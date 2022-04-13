package com.dylan.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Author: Dylan
 * Date: 2022/04/13
 * Desc:
 */


public class MultiDownloader {

    private static String TAG = "MultiDownloader";
    public static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final String CACHE_FILENAME_PREFIX = "cache_";
    private   ExecutorService DEFAULT_TASK_EXECUTOR;
    private static Object lock = new Object();


// 下载状态监听，提供回调
    DownloadStateListener listener;
// 下载目录
    private String downloadPath;
// 下载链接集合
    private List<String> listURL;
// 下载个数
    private int size = 0;
    private List<String> cachePathList=new ArrayList<>();
    private SharedPreferences sharedPreferences;

// 下载完成回调接口
    public interface DownloadStateListener {
         void onFinish(List<String> cachePathList);
         void onFailed();
    }

    public MultiDownloader(Context context, String downloadPath, List<String> listURL, DownloadStateListener listener) {
        sharedPreferences=context.getSharedPreferences("MultiDownLoaderSp",0);
        this.downloadPath = downloadPath;
        this.listURL = listURL;
        this.listener = listener;
        DEFAULT_TASK_EXECUTOR = (ExecutorService) Executors
                .newFixedThreadPool(5);
    }


    /**
     * 开始下载
     */

    public void startDownload() {

// 首先检测path是否存在

        File downloadDirectory = new File(downloadPath);

        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs();

        }

        for (final String url : listURL) {
            try {
                DEFAULT_TASK_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        downloadBitmap(url);
                    }
                });

            } catch (RejectedExecutionException e) {
                e.printStackTrace();
                Log.e(TAG, "thread pool rejected error");
                listener.onFailed();
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailed();
            }
        }

    }

    /**
     * 下载图片
     * @param urlString
     * @return
     */

    private File downloadBitmap(String urlString) {
        String fileName = urlString;
        final File cacheFile = new File(createFilePath(new File(downloadPath), fileName));
        String cachePath=sharedPreferences.getString(urlString,"");
        if (EmptyUtils.isNotEmpty(cachePath)){
            File file=new File(cachePath);
            if (file.exists()){
                cachePathList.add(cachePath);
                statDownloadNum();
                return file;
            }

        }
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        try {

            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            final InputStream in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(new FileOutputStream(cacheFile), IO_BUFFER_SIZE);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            sharedPreferences.edit().putString(urlString,cacheFile.getAbsolutePath()).apply();
            cachePathList.add(cacheFile.getAbsolutePath());
            statDownloadNum();
            return cacheFile;

        } catch (final IOException e) {
            Log.e(TAG, "download " + urlString + " error");
            listener.onFailed();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error in downloadBitmap - " + e);
                }
            }
        }
        return null;

    }


    public static String createFilePath(File cacheDir, String key) {
        try {
            return cacheDir.getAbsolutePath() + File.separator +
                    CACHE_FILENAME_PREFIX+ URLEncoder.encode(key.replace("*", ""), "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            Log.e(TAG, "createFilePath - " + e);
        }
        return null;
    }

    /**
     * 统计下载个数
     */

    private void statDownloadNum() {
        synchronized (lock) {
            size++;
            if (size == listURL.size()) {
                Log.d(TAG, "download finished total " + size);
                DEFAULT_TASK_EXECUTOR.shutdownNow();
                listener.onFinish(cachePathList); // 下载成功回调
            }
        }
    }

}