package com.dylan.library.m3u8.core;

public interface DownLoadListener {

    void onStart();

    void onProgress(int finished, int sum, float percent);

    void onComplete(String savePath);

    void onError(Exception exception);

}
