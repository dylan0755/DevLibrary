package com.dylan.library.m3u8.listener;

public interface DownloadListener {

    void start();

    void process(String downloadUrl, int finished, int sum, float percent);

    void speed(String speedPerSecond);

    void onComplete(String savePath);

    void onError(Exception exception);

}
