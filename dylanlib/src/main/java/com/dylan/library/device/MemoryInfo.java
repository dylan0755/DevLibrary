package com.dylan.library.device;

import com.dylan.library.utils.StringUtils;

/**
 * Created by Dylan on 2017/1/1.
 */

public class MemoryInfo {
    private String totalSize;
    private String availableSize;
    private String usedSize;
    private long totalSizeL;
    private long availableSizeL;
    private long usedSizeL;

    public String getTotalSize() {
        return totalSize;
    }


    public String getAvailableSize() {
        return availableSize;
    }



    public String getUsedSize() {
        return usedSize;
    }


    public long getTotalSizeL() {
        return totalSizeL;
    }

    public void setTotalSizeL(long totalSizeL) {
        this.totalSizeL = totalSizeL;
        totalSize=StringUtils.getFormatFileSize(totalSizeL);
    }

    public long getAvailableSizeL() {
        return availableSizeL;
    }

    public void setAvailableSizeL(long availableSizeL) {
        this.availableSizeL = availableSizeL;
        this.availableSize=StringUtils.getFormatFileSize(availableSizeL);
    }

    public long getUsedSizeL() {
        return usedSizeL;
    }

    public void setUsedSizeL(long usedSizeL) {
        this.usedSizeL = usedSizeL;
        this.usedSize=StringUtils.getFormatFileSize(usedSizeL);
    }

    @Override
    public String toString() {
        return "MemoryInfo{" +
                "totalSize='" + totalSize + '\'' +
                ", availableSize='" + availableSize + '\'' +
                ", usedSize='" + usedSize + '\'' +
                ", totalSizeL=" + totalSizeL +
                ", availableSizeL=" + availableSizeL +
                ", usedSizeL=" + usedSizeL +
                '}';
    }
}
