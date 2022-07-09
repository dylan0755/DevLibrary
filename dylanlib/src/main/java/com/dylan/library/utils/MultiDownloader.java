package com.dylan.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dylan.library.io.FileDownLoader;
import com.dylan.library.utils.CollectionsUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;

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
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
    private SharedPreferences sharedPreferences;
    private ArrayList<DownLoadResultBean> pathList=new ArrayList<>();

    // 下载完成回调接口
    public interface DownloadStateListener {
        void onFinish(ArrayList<DownLoadResultBean> pathList);
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


        for (int i = 0; i <listURL.size() ; i++) {
            String url=listURL.get(i);
            try {
                int finalI = i;
                DEFAULT_TASK_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        downloadBitmap(finalI,url,false,null);
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

    private File downloadBitmap(int sortId,String urlString,boolean isReload,String originUrl) {
        String fileName = urlString;
        final File outPutFile = new File(createFilePath(new File(downloadPath), fileName));
        String cachePath=sharedPreferences.getString(urlString,"");
        if (EmptyUtils.isNotEmpty(cachePath)){
            File file=new File(cachePath);
            if (file.exists()){
                DownLoadResultBean downLoadResultBean=new DownLoadResultBean(urlString,cachePath);
                downLoadResultBean.setSortId(sortId);
                pathList.add(downLoadResultBean);
                statDownloadNum();
                return file;
            }

        }
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            TrustManager[] tm = new TrustManager[]{new FileDownLoader.MyX509TrustManager()};
            sslcontext.init((KeyManager[])null, tm, new SecureRandom());
            HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
                public boolean verify(String s, SSLSession sslsession) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            final  URL mURL = new URL(urlString);
            urlConnection = (HttpURLConnection)mURL.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 301 || responseCode == 302){
                Logger.e("重定向");
                String errorText = urlConnection.getHeaderField("Location");
                urlConnection.disconnect();
                downloadBitmap(sortId,errorText,true,urlString);
                return null;
            }

            final InputStream in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(new FileOutputStream(outPutFile), IO_BUFFER_SIZE);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            sharedPreferences.edit().putString(urlString,outPutFile.getAbsolutePath()).apply();
            DownLoadResultBean downLoadResultBean;
            if (isReload){
                downLoadResultBean= new DownLoadResultBean(originUrl,outPutFile.getAbsolutePath());
            }else{
                downLoadResultBean= new DownLoadResultBean(urlString,outPutFile.getAbsolutePath());
            }
            downLoadResultBean.setSortId(sortId);
            pathList.add(downLoadResultBean);
            statDownloadNum();
            return outPutFile;

        } catch (final Exception e) {
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

                Collections.sort(pathList, new Comparator<DownLoadResultBean>() {
                    @Override
                    public int compare(DownLoadResultBean o1, DownLoadResultBean o2) {
                        return Long.compare(o1.sortId,o2.sortId);
                    }
                });
                listener.onFinish(pathList); // 下载成功回调
            }
        }
    }


    public static class DownLoadResultBean {
        public int sortId;//排序
        public String downLoadUrl;
        public String downLoadPath;

        public DownLoadResultBean(String downLoadUrl, String downLoadPath) {
            this.downLoadUrl = downLoadUrl;
            this.downLoadPath = downLoadPath;
        }

        public void setSortId(int sortId) {
            this.sortId = sortId;
        }

        public String getDownLoadUrl() {
            return downLoadUrl;
        }

        public void setDownLoadUrl(String downLoadUrl) {
            this.downLoadUrl = downLoadUrl;
        }

        public String getDownLoadPath() {
            return downLoadPath;
        }

        public void setDownLoadPath(String downLoadPath) {
            this.downLoadPath = downLoadPath;
        }


    }





}