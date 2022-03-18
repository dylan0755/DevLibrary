package com.dylan.library.m3u8.utils;


import android.webkit.MimeTypeMap;

import com.dylan.library.io.FileUtils;
import com.dylan.library.m3u8.Exception.M3u8Exception;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;


public class MediaFormat {

    private static Set<String> set = new HashSet<>();

    static {
        set.add("mp4");
        set.add("mkv");
        set.add("webm");
        set.add("gif");
        set.add("mov");
        set.add("ogg");
        set.add("flv");
        set.add("avi");
        set.add("3gp");
        set.add("wmv");
        set.add("mpg");
        set.add("vob");
        set.add("swf");
        set.add("m3u8");
    }

    private MediaFormat() {

    }

    public static String getMediaFormat(String url) {
        if (!StringUtils.isUrl(url))
            throw new M3u8Exception(url + "不是一个完整URL链接！");
        String suffix=MimeTypeMap.getFileExtensionFromUrl(url);
        if (EmptyUtils.isNotEmpty(suffix)){
            for (String s : set) {
                if (s.equals(suffix.toLowerCase()))
                    return s;
            }
        }
        throw new M3u8Exception("非视频链接！");
    }
}
