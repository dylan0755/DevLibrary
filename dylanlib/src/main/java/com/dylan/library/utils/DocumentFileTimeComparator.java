package com.dylan.library.utils;

import androidx.documentfile.provider.DocumentFile;

import java.util.Comparator;

/**
 * @author huangj created   2020/9/14 17:42
 * @description 对比文件时间
 */
public class DocumentFileTimeComparator implements Comparator<DocumentFile> {
    @Override
    public int compare(DocumentFile p1, DocumentFile p2) {
        if (p1.lastModified()< p2.lastModified()) {
            return 1;
        }
        if (p1.lastModified() == p2.lastModified()) {
            return 0;
        }
        return -1;

    }


}
