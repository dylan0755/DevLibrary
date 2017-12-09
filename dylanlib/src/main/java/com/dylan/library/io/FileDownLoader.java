package com.dylan.library.io;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class FileDownLoader {
    private int ThreadCount = 6;
    private String downLoadDir;
    private String downloadFilePath;
    private double currentSize = 0;//当前的进度
    private long mTotalLength;
    private Handler mHandler;
    private DownLoadListener listener;
    public static final int UDAPTE_PROGRESS = 3;
    private List<DownLoadThread> downLoadThreads = new ArrayList<>();
    private boolean isPause;
    private boolean isCancel;
    public static final int ERROR_URLPARSE_FILE = 20;
    public static final int ERROR_ACCESS_RESOURCE = 21;
    public static final int ERROR_DONWLOAD = 22;

    public FileDownLoader() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == UDAPTE_PROGRESS) {
                    if (listener != null) {
                        int perProgress = msg.arg1;
                        currentSize += perProgress;
                        double percent = (currentSize / mTotalLength) * 100;
                        listener.onProgress((long) mTotalLength, (long) currentSize, (int) percent);
                        if (percent == 100) {
                            listener.onComplete(mTotalLength, downloadFilePath);
                            downLoadThreads.clear();
                            currentSize = 0;
                        }
                        if (isCancel) {
                            currentSize = 0;
                            mTotalLength = 0;
                        }

                    }
                } else if (msg.what == ERROR_ACCESS_RESOURCE ||
                        msg.what == ERROR_URLPARSE_FILE ||
                        msg.what == ERROR_DONWLOAD) {
                    if (listener != null) {
                        listener.onError(msg.what,(String) msg.obj);
                    }
                }
                super.handleMessage(msg);
            }
        };
    }


    public void downLoad(final Context context, final String downLoadUrl, final String filename) {
        isCancel = false;
        isPause = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(downLoadUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        int fileLength = connection.getContentLength();
                        mTotalLength = fileLength;
                        downLoadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        try {
                            String fileName = filename;
                            if (filename == null || filename.isEmpty()) {
                                fileName = parseFileNameFormUrl(downLoadUrl);
                                downloadFilePath = downLoadDir + "/" + fileName;
                            } else {
                                downloadFilePath = downLoadDir + "/" + filename;
                            }
                            final String finalFileName = fileName;
                            if ("".equals(finalFileName)) {
                                throw new UrlFileNameException();
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) listener.onDownLoadFileName(finalFileName);
                                }
                            });
                        } catch (UrlFileNameException e) {
                            Message msg = Message.obtain();
                            msg.what = ERROR_URLPARSE_FILE;
                            msg.obj = "无法下载该文件,请检查下载链接";
                            mHandler.sendMessage(msg);
                            return;
                        }


                        //在本地创建和服务器中一样大小的临时文件
                        BufferedRandomAccessFile raf = new BufferedRandomAccessFile(downloadFilePath, "rwd");
                        raf.setLength(fileLength);
                        raf.close();
                        int blockSize = fileLength / ThreadCount;
                        for (int threadId = 1; threadId <= ThreadCount; threadId++) {
                            int startIndex = (threadId - 1) * blockSize;
                            int endIndex = threadId * blockSize - 1;
                            if (threadId == ThreadCount) {
                                endIndex = fileLength;
                            }
                            DownLoadThread thread = new DownLoadThread(downLoadUrl, downloadFilePath, mHandler,
                                    threadId, startIndex, endIndex);
                            downLoadThreads.add(thread);
                            thread.start();
                        }
                    } else {
                        Message message = Message.obtain();
                        message.what = ERROR_ACCESS_RESOURCE;
                        if (responseCode==404){
                            message.obj = "找不到该文件";
                        }else{
                            message.obj = "资源连接失败";
                        }
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = ERROR_ACCESS_RESOURCE;
                    message.obj = "资源连接失败";
                    mHandler.sendMessage(message);
                }

            }
        }).start();

    }

    public boolean isPause() {
        return isPause;
    }

    public void pause() {
        for (DownLoadThread thread : downLoadThreads) {
            thread.toPause();
            isPause = true;
        }
    }

    public void cancel() {
        for (DownLoadThread thread : downLoadThreads) {
            thread.toCancel();
            isCancel = true;
        }
        downLoadThreads.clear();
        currentSize = 0;
        mTotalLength = 0;
        isPause = false;
        if (downloadFilePath != null && !downloadFilePath.isEmpty()) {
            File file = new File(downloadFilePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }


    public void restart() {
        if (downLoadThreads.size() > 0) {
            for (DownLoadThread thread : downLoadThreads) {
                thread.reStart();
            }
        }
        isPause = false;
        isCancel = false;
    }


    public interface DownLoadListener {
        void onDownLoadFileName(String fileName);

        void onError(int erroType, String error);

        void onProgress(long totalSize, long hasDownLoadSize, int progressPercent);

        void onComplete(long totalSize, String downLoadFilePath);

    }

    public void setDownLoadListener(DownLoadListener listener) {
        this.listener = listener;
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


}
