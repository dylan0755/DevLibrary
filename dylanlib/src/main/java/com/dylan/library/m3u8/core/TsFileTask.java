package com.dylan.library.m3u8.core;

import com.dylan.library.io.IOUtils;
import com.dylan.library.m3u8.Exception.M3u8Exception;
import com.dylan.library.m3u8.entry.VideoTs;
import com.dylan.library.m3u8.threadpool.SubTask;
import com.dylan.library.m3u8.utils.CommonUtil;
import com.dylan.library.utils.HttpRequestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;

public class TsFileTask implements SubTask {
    private VideoTs videoFileTS;
    private File tsFile;

    public TsFileTask(VideoTs videoFileTS){
      this.videoFileTS = videoFileTS;
    }

    public String getVideoName(){
      return videoFileTS.getName();
    }

    @Override
    public void run() {
      tsFile = new File(videoFileTS.getPath() + videoFileTS.getName());
      try {
        if (tsFile.exists()){
          tsFile.delete();
        }else {
          tsFile.createNewFile();
          FileOutputStream fileOutputStream = new FileOutputStream(tsFile);

            HttpURLConnection connection= (HttpURLConnection) HttpRequestUtils.sendGetRequest(videoFileTS.getUrl());
            int code=connection.getResponseCode();
            if ( code>= 200 && code < 300) {
                if (code!= 200){
                    connection.disconnect();
                    throw new M3u8Exception("响应码异常：" + code);
                }
            }else {
                connection.disconnect();
                throw new M3u8Exception("HTTP请求失败");
            }

          byte[] byteData = IOUtils.getBytes(connection.getInputStream());
          if (videoFileTS.getKeyByte() != null){
            byte[] keyByte = videoFileTS.getKeyByte();
            byte[] ivByte = videoFileTS.getKeyIvByte();
            byteData = CommonUtil.decrypt(byteData, keyByte, ivByte);
          }
          fileOutputStream.write(byteData);
          fileOutputStream.flush();
          fileOutputStream.close();
          connection.disconnect();
        }
        videoFileTS.setFile(tsFile);
      }catch (Exception e){
        System.out.println(videoFileTS.getName() + " 下载失败");
        tsFile.delete();
        e.printStackTrace();
        System.exit(0);
      }
    }
  }