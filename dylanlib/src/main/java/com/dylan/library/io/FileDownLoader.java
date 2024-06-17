package com.dylan.library.io;

import android.os.Environment;
import android.util.Log;


import com.dylan.library.utils.EmptyUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class FileDownLoader {
    private final static int DATA_BUFFER = 8192;
    private String downLoadDir;
    private String downloadFilePath;
    private boolean isDownLoading;
    private long mTotalLength;
    private DownLoadListener mDownLoadListener;
    private boolean cancel;
    ExecutorService downLoadService;


    public FileDownLoader() {
        downLoadService = Executors.newFixedThreadPool(1);
    }


    public void downLoad(final String url, final String filename) {
        downLoad(null, url, filename);
    }

    public void downLoad(final String downLoadDirPath, final String url, final String filename) {
        downLoad(downLoadDirPath, url, filename, false);
    }


    public void downLoad(String downLoadDirPath,String url,String filename,boolean checkIsExist) {
        downLoadService.execute(new Runnable() {
            @Override
            public void run() {
                startDownLoad(downLoadDirPath,url,filename,checkIsExist);
            }
        });
    }


    private void startDownLoad(String downLoadDirPath,String url,String filename, final boolean checkIsExist){
        int downloadProgress = 0;
        long totalSize = -1;
        InputStream is = null;
        FileOutputStream os = null;
        boolean succeed = false;
        String failureReason="";
        String downLoadFileName;
        try {

            if (EmptyUtils.isEmpty(filename)) {
                downLoadFileName = parseFileNameFormUrl(url);
                if (EmptyUtils.isEmpty(downLoadFileName)) {
                    if (mDownLoadListener!=null)mDownLoadListener.onError("file name undefined");
                    return;
                }
            }else{
                downLoadFileName=filename;
            }

            if (EmptyUtils.isNotEmpty(downLoadDirPath)) {
                downLoadDir = downLoadDirPath;
                createDirIfNotExists(downLoadDirPath);
            } else {
                downLoadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            }


            downloadFilePath = downLoadDir + "/" + downLoadFileName;

            //检查是否存在缓存
            if (checkIsExist) {
                File file = new File(downloadFilePath);
                if (file.exists()) {
                    if (mDownLoadListener != null) {
                        mDownLoadListener.onComplete(file.length(), downloadFilePath, true);
                        return;
                    }
                }
            }


            //忽略https 证书问题
            SSLContext sslcontext = SSLContext.getInstance("SSL");//第一个参数为协议,第二个参数为提供者(可以缺省)
            TrustManager[] tm = {new MyX509TrustManager()};
            sslcontext.init(null, tm, new SecureRandom());
            HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
                public boolean verify(String s, SSLSession sslsession) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());

            cancel = false;
            URL mURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) mURL.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                mTotalLength = connection.getContentLength();
                is = connection.getInputStream();
                String contentEncoding = connection.getContentEncoding();
                if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                    is = new GZIPInputStream(is);
                }
                os = new FileOutputStream(downloadFilePath);
                byte[] buffer = new byte[DATA_BUFFER];
                int readSize;
                int temp = 0;
                while ((readSize = is.read(buffer)) != -1) {
                    isDownLoading = true;
                    os.write(buffer, 0, readSize);
                    os.flush();
                    totalSize += readSize;
                    if (cancel) {
                        break;
                    }
                    //下载进度
                    if (mDownLoadListener != null) {
                        downloadProgress = (int) (totalSize * 100 / mTotalLength);
                        if (downloadProgress >= temp) {
                            temp++;
                            mDownLoadListener.onProgress(mTotalLength, totalSize, downloadProgress);

                        }
                    }
                }
                //下载完成
                succeed=!cancel;
            } else if (responseCode == 301 || responseCode == 302) {
                String location = connection.getHeaderField("Location");
                connection.disconnect();
                downLoad(downLoadDirPath, location, filename, checkIsExist);
                return;
            } else {
                if (responseCode == 404) {
                    failureReason = "resource not found";
                } else {
                    failureReason = "resource connection failure";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            failureReason=e.getMessage();
        }
        closeIO(os);
        closeIO(is);
        isDownLoading = false;
        if (mDownLoadListener != null){
            if (succeed){
                mDownLoadListener.onComplete(mTotalLength, downloadFilePath, false);
            }else if (cancel){
                deleteDownLoadFile();
                mDownLoadListener.onCancel();
            }else{
                deleteDownLoadFile();
                mDownLoadListener.onError(failureReason);
            }
        }
    }

    private void deleteDownLoadFile() {
        if (EmptyUtils.isEmpty(downloadFilePath)) return;
        File file = new File(downloadFilePath);
        if (file.exists()) {
            file.delete();
        }
    }


    public interface DownLoadListener {
        void onCancel();

        void onError(String error);

        void onProgress(long totalSize, long hasDownLoadSize, int progressPercent);

        void onComplete(long totalSize, String downLoadFilePath, boolean loadFromCache);

    }


    public static abstract class SimpleDownLoadListener implements DownLoadListener {
        public void onCancel() {
        }

        public void onError(String error) {
        }

        public void onProgress(long totalSize, long hasDownLoadSize, int progressPercent) {
        }

        public abstract void onComplete(long totalSize, String downLoadFilePath, boolean loadFromCache);
    }

    public void setDownLoadListener(DownLoadListener listener) {
        this.mDownLoadListener = listener;
    }

    public void cancel() {
        if (isDownLoading) {
            cancel = true;
        }
    }


    public static String parseFileNameFormUrl(String url) throws UrlFileNameException {
        try {
            String fileName = "";
            URL mUrl = new URL(url);
            String urlString = mUrl.toString();
            String query = mUrl.getQuery();
            int lastSpart = urlString.lastIndexOf("/");
            if (query == null || query.isEmpty()) {
                fileName = urlString.substring(lastSpart + 1, urlString.length());
            } else {
                int queryParamStart = urlString.indexOf("?");
                fileName = urlString.substring(lastSpart + 1, queryParamStart);
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UrlFileNameException();
        }
    }


    public static class UrlFileNameException extends Exception {

    }


    public static class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }

    public static void closeIO(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean createDirIfNotExists(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return false;
    }

}
