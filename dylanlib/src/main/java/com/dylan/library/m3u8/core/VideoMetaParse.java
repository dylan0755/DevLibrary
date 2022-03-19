package com.dylan.library.m3u8.core;


import com.dylan.library.m3u8.entry.VideoMeta;

import java.util.List;

public interface VideoMetaParse {

  public List<VideoMeta> parseVideoMeta(String path, String name);

}
