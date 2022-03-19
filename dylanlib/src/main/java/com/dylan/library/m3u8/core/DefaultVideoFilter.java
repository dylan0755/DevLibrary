package com.dylan.library.m3u8.core;


import com.dylan.library.m3u8.entry.VideoMeta;

import java.util.List;

public class DefaultVideoFilter implements VideoListFilter {

  @Override
  public VideoMeta filter(List<VideoMeta> videoList) {
    if (videoList != null && !videoList.isEmpty()){
      if (videoList.size() > 1) {
        System.out.println("解析到多个视频文件，已默认选择最后一个视频文件进行下载");
        return videoList.get(videoList.size() - 1);
      }else {
        return videoList.get(0);
      }
    }else {
      return null;
    }
  }
}
