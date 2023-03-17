package com.dylan.library.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Author: Dylan
 * Date: 2022/10/1
 * Desc:
 */
public class NIOUtils {

    public void copyFile(File srcFile, File dstFile) throws IOException {
        if (srcFile == null || !srcFile.exists()) {
            return;
        }

        FileInputStream fileIns = null;
        FileOutputStream fileOuts = null;
        FileChannel source = null;
        FileChannel destination = null;

        try {
            fileIns = new FileInputStream(srcFile);
            fileOuts = new FileOutputStream(dstFile);
            source = fileIns.getChannel();
            destination = fileOuts.getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileIns != null)
                fileIns.close();
            if (fileOuts != null)
                fileOuts.close();
            if (source != null)
                source.close();
            if (destination != null)
                destination.close();
        }
    }

}