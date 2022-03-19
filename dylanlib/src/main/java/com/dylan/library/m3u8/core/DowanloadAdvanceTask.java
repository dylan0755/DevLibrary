package com.dylan.library.m3u8.core;

import com.dylan.library.m3u8.entry.VideoMeta;
import com.dylan.library.m3u8.entry.VideoTs;
import com.dylan.library.m3u8.threadpool.AdvanceTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DowanloadAdvanceTask implements AdvanceTask<TsFileTask> {
  private VideoMeta videoMeta;
  private List<TsFileTask> tsFileTaskList;
  private File videoFile;
  private File tempPath;
  private AtomicInteger atomicInteger;
  private DownLoadListener mDownLoadListener;

  public DowanloadAdvanceTask(VideoMeta videoMeta)  throws Exception{
    this.videoMeta = videoMeta;
    tempPath = new File(videoMeta.getTempPath());
    tempPath.mkdirs();
    videoFile = new File(videoMeta.getPath() + videoMeta.getName());
    if (videoFile.exists()){
      videoFile.delete();
    }else {
      videoFile.createNewFile();
    }
  }


  public void setDownLoadListener(DownLoadListener mDownLoadListener) {
    this.mDownLoadListener = mDownLoadListener;
  }

  @Override
  public void completeAll() {
    try {
      //System.out.println("全部分片下载完成，开始合并文件");
      FileOutputStream outputStream = new FileOutputStream(videoFile);
      byte[] tempByte = new byte[1024 * 1024 * 5];
      int readLen;
      for (VideoTs fileTS : videoMeta.getVideoTsList()) {
        FileInputStream inputStream = new FileInputStream(fileTS.getFile());
        while ((readLen = inputStream.read(tempByte)) != -1){
          outputStream.write(tempByte, 0, readLen);
        }
        inputStream.close();
        fileTS.getFile().delete();
      }
      outputStream.flush();
      outputStream.close();
      tempPath.delete();
      if (mDownLoadListener!=null)mDownLoadListener.onComplete(videoFile.getAbsolutePath());
      //System.out.println(videoMeta.getName() + " 下载完成");
     // System.out.println("请手动关闭程序...");
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  @Override
  public boolean failedRetry(TsFileTask subTask) {
    return false;
  }

  @Override
  public void completeOne(TsFileTask subTask) {
    int supply=atomicInteger.decrementAndGet();
   // System.out.println(subTask.getVideoName() + " 下载完成, 剩余：" +supply );
    int sum=videoMeta.getVideoTsList().size();
    if (sum==0)return;
    int finishedCount=sum-supply;
    float percent=new BigDecimal(finishedCount).divide(new BigDecimal(sum), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    if (mDownLoadListener!=null)mDownLoadListener.onProgress(finishedCount,sum,percent);

  }

  @Override
  public List<TsFileTask> getSubTaskList() {
    if (tsFileTaskList != null){
      return tsFileTaskList;
    }else {
      tsFileTaskList = new LinkedList<>();
      List<VideoTs> videoFileList = videoMeta.getVideoTsList();
      for (VideoTs videoFileTS : videoFileList) {
        TsFileTask tsFileTask = new TsFileTask(videoFileTS);
        tsFileTaskList.add(tsFileTask);
      }
      atomicInteger = new AtomicInteger(tsFileTaskList.size());
      if (mDownLoadListener!=null)mDownLoadListener.onStart();
      return tsFileTaskList;
    }
  }



}
