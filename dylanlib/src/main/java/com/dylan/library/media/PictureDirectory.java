package com.dylan.library.media;

import java.io.Serializable;

/**
 * Author: Dylan
 * Date: 2020/2/26
 * Desc:  图片所在的文件夹 类
 */
public class PictureDirectory implements Serializable {
    private String dirName;
    private String dirPath;
    private int childCount;
    private String firstChildPath;


    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public String getFirstChildPath() {
        return firstChildPath;
    }

    public void setFirstChildPath(String firstChildPath) {
        this.firstChildPath = firstChildPath;
    }
}
