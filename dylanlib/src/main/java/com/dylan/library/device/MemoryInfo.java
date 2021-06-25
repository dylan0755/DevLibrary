package com.dylan.library.device;

/**
 * Created by Dylan on 2017/1/1.
 */

public class MemoryInfo {
    private String totalSize;
    private String availableSize;
    private String usedSize;

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public String getAvailableSize() {
        return availableSize;
    }

    public void setAvailableSize(String availableSize) {
        this.availableSize = availableSize;
    }

    public String getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(String usedSize) {
        this.usedSize = usedSize;
    }

    @Override
    public String toString() {
        return "MemoryInfo{" +
                "totalSize='" + totalSize + '\'' +
                ", availableSize='" + availableSize + '\'' +
                ", usedSize='" + usedSize + '\'' +
                '}';
    }
}
