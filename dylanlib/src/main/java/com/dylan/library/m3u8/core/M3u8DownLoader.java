package com.dylan.library.m3u8.core;

import com.dylan.library.m3u8.Exception.M3u8Exception;
import com.dylan.library.m3u8.entry.VideoMeta;
import com.dylan.library.m3u8.utils.CommonUtil;

import java.security.Provider;
import java.util.List;

public class M3u8DownLoader {

    private String dirPath;

    private VideoListFilter videoListFilter;
    private Downloader downloader;

    @Deprecated
    public M3u8DownLoader(int threadCount) {
        downloader = new Downloader(threadCount);
    }

    public M3u8DownLoader(int threadCount, Provider provider) {
        this(threadCount);
        CommonUtil.addProvider(provider);
    }

    private void checkField() {
        if (dirPath == null) {
            throw new M3u8Exception("未正确设置存储路径");
        }
    }


    public void setVideoListFilter(VideoListFilter filter) {
        videoListFilter = filter;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public void startDownload(final String url, final String fileName, final DownLoadListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkField();
                    UrlParseInit urlParseInit = new UrlParseInit(url, dirPath, fileName);
                    try {
                        urlParseInit.initUrl();
                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onError(e);
                        return;
                    }
                    List<VideoMeta> videoMetaList = urlParseInit.getVideoList();
                    if (videoListFilter == null) {
                        videoListFilter = new DefaultVideoFilter();
                    }
                    VideoMeta selectVideo;
                    if (videoMetaList.size() > 1) {
                        selectVideo = videoListFilter.filter(videoMetaList);
                    } else {
                        selectVideo = videoMetaList.get(0);
                    }
                    startDownload(selectVideo, listener);
                } catch (Exception e) {
                    listener.onError(e);
                }
            }
        }).start();

    }

    public void startDownload(VideoMeta videoMeta, DownLoadListener listener) {
        downloader.downloadVideo(videoMeta, listener);
    }
}
