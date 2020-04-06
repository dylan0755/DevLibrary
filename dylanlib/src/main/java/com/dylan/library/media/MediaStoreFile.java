package com.dylan.library.media;

import android.support.annotation.NonNull;

import com.dylan.library.bean.MergeCell;

import java.io.Serializable;

/**
 * Author: Dylan
 * Date: 2020/2/11
 * Desc:
 */
public class MediaStoreFile extends MergeCell implements Comparable<MediaStoreFile>, Serializable {
    private  int _id=-1;
    private String name;
    private String path;
    private long length;
    private String displayName;
    private String title;
    private long modifyTime;
    private int sourceType; //来源  视频库  文件库  或 File
    private String mimeType; //文件类型
    private long duration;//时长


    public MediaStoreFile(){

    }

    @Override
    public String getKey() {
        return path==null?"":path;
    }

    public MediaStoreFile(@NonNull String pathname) {
        this.path=pathname;
    }


    public int get_ID() {
        return _id;
    }

    public void set_ID(int _ID) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(@NonNull MediaStoreFile o) {//升序
        if (modifyTime > o.getModifyTime()) return 1;
        return -1;
    }

    @Override
    public String toString() {
        return "MediaStoreFile{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", length=" + length +
                ", displayName='" + displayName + '\'' +
                ", title='" + title + '\'' +
                ", modifyTime=" + modifyTime +
                ", sourceType=" + sourceType +
                ", mimeType='" + mimeType + '\'' +
                ", duration=" + duration +
                '}';
    }
}
