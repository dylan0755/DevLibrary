package com.dylan.library.io;

import android.content.Context;
import android.os.Environment;
import com.dylan.library.exception.ELog;
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
    public static final int UDAPTE_PROGRESS = 3;
    public static final int ERROR_URLPARSE_FILE = 20;
    public static final int ERROR_ACCESS_RESOURCE = 21;
    public static final int ERROR_DONWLOAD = 22;
    private boolean cancel;

    public FileDownLoader() {

    }


    public void downLoad(final String url, final String filename){
        downLoad(null,url,filename);
    }

    public void downLoad(final String downLoadDirPath,final String url, final String filename) {
        downLoad(downLoadDirPath, url,filename,false);
    }




    public void downLoad(final String downLoadDirPath, final String url, final String filename, final boolean checkIsExist) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int downloadProgress = 0;
                long totalSize = -1;
                InputStream is = null;
                FileOutputStream os = null;

                try {
                    if (EmptyUtils.isNotEmpty(downLoadDirPath)){
                        downLoadDir = downLoadDirPath;
                        FileUtils.createDirIfNotExists(downLoadDirPath);
                    }else{
                        downLoadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                    }
                    try {
                        if (filename == null || filename.isEmpty()) {
                            String fileName = parseFileNameFormUrl(url);
                            if (EmptyUtils.isEmpty(fileName)){
                                fileName=System.currentTimeMillis()+".mp4";
                            }
                            downloadFilePath = downLoadDir + "/" + fileName;
                        } else {
                            downloadFilePath = downLoadDir + "/" + filename;
                        }
                    } catch (UrlFileNameException e) {
                        downloadFilePath = downLoadDir + "/" + System.currentTimeMillis()+".mp4";
                    }

                    //检查是否存在
                    if (checkIsExist){
                        File file =new File(downloadFilePath);
                       if (file.exists()){
                           if (mDownLoadListener != null) {
                               mDownLoadListener.onComplete(file.length(), downloadFilePath);
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


                    URL mURL = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) mURL.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        mTotalLength = connection.getContentLength();
                        is = connection.getInputStream();
                        String contentEncoding = connection.getContentEncoding();
                        if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                            is = new GZIPInputStream(is);
                        }
                        os = new FileOutputStream(new File(downloadFilePath));
                        byte buffer[] = new byte[DATA_BUFFER];
                        int readSize = 0;
                        int temp = 0;
                        while ((readSize = is.read(buffer)) > 0) {
                            isDownLoading = true;
                            os.write(buffer, 0, readSize);
                            os.flush();
                            totalSize += readSize;
                            if (cancel) {
                                if (mDownLoadListener != null) mDownLoadListener.onCancel();
                                cancel = false;
                                File des = new File(downloadFilePath);
                                if (des.exists()) {
                                    des.delete();
                                }
                                return;
                            }
                            if (mDownLoadListener != null) {
                                downloadProgress = (int) (totalSize * 100 / mTotalLength);
                                if (downloadProgress >= temp) {
                                    temp++;
                                    mDownLoadListener.onProgress(mTotalLength, totalSize, downloadProgress);
                                }
                            }
                        }
                        if (totalSize > 0) {
                            mDownLoadListener.onProgress(mTotalLength, mTotalLength, 100);
                        }
                    } else if (responseCode == 301 || responseCode == 302) {
                        String location = connection.getHeaderField("Location");
                        connection.disconnect();
                        downLoad(downLoadDirPath,location, filename,checkIsExist);
                        return;
                    } else {
                        String errorText = "";
                        if (responseCode == 404) {
                            errorText = "找不到该文件";
                        } else {
                            errorText = "资源连接失败";
                        }
                        if (mDownLoadListener != null)
                            mDownLoadListener.onError(ERROR_ACCESS_RESOURCE, errorText);
                    }
                } catch (Exception e) {
                    ELog.e(e);
                    if (mDownLoadListener != null)
                        mDownLoadListener.onError(ERROR_ACCESS_RESOURCE, e.getMessage());
                }
                isDownLoading = false;
                closeIO(os);
                closeIO(is);
                if (mDownLoadListener != null) {
                    mDownLoadListener.onComplete(mTotalLength, downloadFilePath);
                }
            }
        }).start();

    }


    public interface DownLoadListener {
        void onCancel();

        void onError(int erroType, String error);

        void onProgress(long totalSize, long hasDownLoadSize, int progressPercent);

        void onComplete(long totalSize, String downLoadFilePath);

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


}
