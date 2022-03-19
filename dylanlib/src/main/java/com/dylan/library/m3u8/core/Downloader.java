package com.dylan.library.m3u8.core;

import com.dylan.library.m3u8.entry.VideoMeta;
import com.dylan.library.m3u8.threadpool.AdvanceTaskExecutor;

class Downloader {
  private AdvanceTaskExecutor taskExecutor;

  public Downloader(int threadCount){
    taskExecutor = new AdvanceTaskExecutor(threadCount);
    taskExecutor.startTaskExecutor();
  }

  public void downloadVideo(VideoMeta videoMeta, DownLoadListener listener) {
    DowanloadAdvanceTask dowanloadTask;
    try {
      dowanloadTask = new DowanloadAdvanceTask(videoMeta);
      dowanloadTask.setDownLoadListener(listener);
      taskExecutor.submitAdvanceTask(dowanloadTask);
    } catch (Exception e) {
      listener.onError(e);
      //System.out.println("发生异常，停止下载");
      e.printStackTrace();
    }
  }
}
