package com.xm.vbrowser.app.entity;

import com.dylan.library.m3u8.entry.M3U8;

/**
 * Created by xm on 17-8-17.
 */
public class VideoInfo {
    public long  genTimestamp=System.currentTimeMillis();
    private String fileName;
    private String url;
    private VideoFormat videoFormat;
    private M3U8 m3U8;
    private long size;//单位byte m3u8不显示
    private double duration;//单位s m3u8专用
    private String sourcePageUrl;//原网页url
    private String sourcePageTitle;//原网页标题
    private int downLoadStatus;//0  是未下载  1 是下载中  2 是下载完成 3 是下载失败
    private int downLoadProgress;

    public M3U8 getM3U8() {
        return m3U8;
    }

    public void setM3U8(M3U8 m3U8) {
        this.m3U8 = m3U8;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public VideoFormat getVideoFormat() {
        return videoFormat;
    }

    public void setVideoFormat(VideoFormat videoFormat) {
        this.videoFormat = videoFormat;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getSourcePageUrl() {
        return sourcePageUrl;
    }

    public void setSourcePageUrl(String sourcePageUrl) {
        this.sourcePageUrl = sourcePageUrl;
    }

    public String getSourcePageTitle() {
        return sourcePageTitle;
    }

    public void setSourcePageTitle(String sourcePageTitle) {
        this.sourcePageTitle = sourcePageTitle;
    }

    public int getDownLoadStatus() {
        return downLoadStatus;
    }

    public void setDownLoadStatus(int downLoadStatus) {
        this.downLoadStatus = downLoadStatus;
    }

    public int getDownLoadProgress() {
        return downLoadProgress;
    }

    public void setDownLoadProgress(int downLoadProgress) {
        this.downLoadProgress = downLoadProgress;
    }
}
