package com.dylan.library.m3u8.core;


import com.dylan.library.m3u8.entry.VideoMeta;

import java.util.List;

public interface VideoListFilter {

  public VideoMeta filter(List<VideoMeta> videoList);

}
