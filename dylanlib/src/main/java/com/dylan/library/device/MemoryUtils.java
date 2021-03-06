package com.dylan.library.device;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by Dylan on 2017/1/1.
 */

public class MemoryUtils {


    public static MemoryInfo getInternalMemory(Context context){
        StatFs stat=new StatFs(Environment.getDataDirectory().toString());
        long blocksize=stat.getBlockSize();
        long totalblocks=stat.getBlockCount();
        long avaliableblocks=stat.getAvailableBlocks();

        long totalSizeLong=blocksize*totalblocks;
        long avaliableSizeLong=avaliableblocks*blocksize;
        long usedSizeLong=totalSizeLong-avaliableSizeLong;

        String totalSize=Formatter.formatFileSize(context, totalSizeLong);
        String availableSize=Formatter.formatFileSize(context,avaliableSizeLong);
        String usedSize=Formatter.formatFileSize(context,usedSizeLong);
        MemoryInfo memoryInfo=new MemoryInfo();
        memoryInfo.setTotalSize(totalSize);
        memoryInfo.setAvailableSize(availableSize);
        memoryInfo.setUsedSize(usedSize);
        return memoryInfo;
    }


    public static MemoryInfo getExternalMemory(Context context){
        StatFs stat=new StatFs(Environment.getExternalStorageDirectory().toString());
        long blocksize=stat.getBlockSize();
        long totalblocks=stat.getBlockCount();
        long avaliableblocks=stat.getAvailableBlocks();

        long totalSizeLong=blocksize*totalblocks;
        long availableSizeLong=avaliableblocks*blocksize;
        long usedSizeLong=totalSizeLong-availableSizeLong;

        String totalSize=Formatter.formatFileSize(context, totalSizeLong);
        String availableSize=Formatter.formatFileSize(context,availableSizeLong);
        String usedSize=Formatter.formatFileSize(context,usedSizeLong);
        MemoryInfo memoryInfo=new MemoryInfo();
        memoryInfo.setTotalSize(totalSize);
        memoryInfo.setAvailableSize(availableSize);
        memoryInfo.setUsedSize(usedSize);
        return memoryInfo;

    }


    public static MemoryInfo getRAM(Context context){
        long totalSizeLong=getTotalMemory(context);
        long availableSizeLong=getAvailMemory(context);
        long usedSizeLong=totalSizeLong-availableSizeLong;

        String totalSize=Formatter.formatFileSize(context, totalSizeLong);
        String availableSize=Formatter.formatFileSize(context,availableSizeLong);
        String usedSize=Formatter.formatFileSize(context,usedSizeLong);
        MemoryInfo memoryInfo=new MemoryInfo();
        memoryInfo.setTotalSize(totalSize);
        memoryInfo.setAvailableSize(availableSize);
        memoryInfo.setUsedSize(usedSize);
        return memoryInfo;

    }




    // ??????android????????????????????????
    private static long getAvailMemory(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }


    //?????????????????????
    private static long getTotalMemory(Context context){
        if (Build.VERSION.SDK_INT>=16){
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            return mi.totalMem;
        }else{
            long size = 0;
            //??????????????????????????????????????????????????????????????????/proc/meminfo
            File file = new File("/proc/meminfo");
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                //??????????????????????????????????????????????????????????????????
                String totalMemarysizeStr = reader.readLine();//MemTotal:         513744 kB
                //????????????????????????????????????
                int startIndex = totalMemarysizeStr.indexOf(':');
                int endIndex = totalMemarysizeStr.indexOf('k');
                //??????
                totalMemarysizeStr = totalMemarysizeStr.substring(startIndex + 1, endIndex).trim();
                //??????long??????????????????????????????kb
                size = Long.parseLong(totalMemarysizeStr);
                //?????????byte?????????
                size *= 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }

    }



}
