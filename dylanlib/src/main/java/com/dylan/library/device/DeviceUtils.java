package com.dylan.library.device;

import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Dylan on 2017/1/1.
 */

public class DeviceUtils {

    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取设备厂商
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取型号
     *
     * @return
     */
    public static String getMode() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }



    public static String getDeviceId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        if (id.isEmpty()) id = String.valueOf(System.currentTimeMillis());
        return id;
    }

    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    //添加权限 READ_PHONE_STATE
    public static String getIMEI(Context context){
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        return szImei;
    }

    // 添加权限 android.permission.INTERNET
    //添加权限 android.permission.ACCESS_WIFI_STATE"
    public static String getLocalMac(Context context) {
        String macAddress = null;
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) macAddress = info.getMacAddress();
        return macAddress;
    }


    // 获取CPU架构
    public static String getArchi() {
        return android.os.Build.CPU_ABI;
    }


    public static MemoryInfo getInternalMemory(Context context){
        StatFs stat=new StatFs(Environment.getDataDirectory().toString());
        long blocksize=stat.getBlockSize();
        long totalblocks=stat.getBlockCount();
        long avaliableblocks=stat.getAvailableBlocks();

        long totalSizeLong=blocksize*totalblocks;
        long avaliableSizeLong=avaliableblocks*blocksize;
        long usedSizeLong=totalSizeLong-avaliableSizeLong;
        MemoryInfo memoryInfo=new MemoryInfo();
        memoryInfo.setTotalSizeL(totalSizeLong);
        memoryInfo.setAvailableSizeL(avaliableSizeLong);
        memoryInfo.setAvailableSizeL(usedSizeLong);
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

        MemoryInfo memoryInfo=new MemoryInfo();
        memoryInfo.setTotalSizeL(totalSizeLong);
        memoryInfo.setAvailableSizeL(availableSizeLong);
        memoryInfo.setUsedSizeL(usedSizeLong);
        return memoryInfo;

    }


    public static MemoryInfo getRAM(Context context){
        long totalSizeLong=getTotalMemory(context);
        long availableSizeLong=getAvailMemory(context);
        long usedSizeLong=totalSizeLong-availableSizeLong;
        MemoryInfo memoryInfo=new MemoryInfo();
        memoryInfo.setTotalSizeL(totalSizeLong);
        memoryInfo.setAvailableSizeL(availableSizeLong);
        memoryInfo.setUsedSizeL(usedSizeLong);
        return memoryInfo;

    }




    // 获取android当前可用内存大小
    private static long getAvailMemory(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }


    //获取总运存大小
    private static long getTotalMemory(Context context){
        if (Build.VERSION.SDK_INT>=16){
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            return mi.totalMem;
        }else{
            long size = 0;
            //通过读取配置文件方式获取总内大小。文件目录：/proc/meminfo
            File file = new File("/proc/meminfo");
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                //根据命令行可以知道，系统总内存大小位于第一行
                String totalMemarysizeStr = reader.readLine();//MemTotal:         513744 kB
                //要获取大小，对字符串截取
                int startIndex = totalMemarysizeStr.indexOf(':');
                int endIndex = totalMemarysizeStr.indexOf('k');
                //截取
                totalMemarysizeStr = totalMemarysizeStr.substring(startIndex + 1, endIndex).trim();
                //转为long类型，得到数据单位是kb
                size = Long.parseLong(totalMemarysizeStr);
                //转为以byte为单位
                size *= 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }

    }


}
