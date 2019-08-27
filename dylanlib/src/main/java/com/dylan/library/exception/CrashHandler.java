package com.dylan.library.exception;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.dylan.library.device.DeviceUtils;
import com.dylan.library.device.MemoryInfo;
import com.dylan.library.device.MemoryUtils;
import com.dylan.library.device.SDCardUtils;
import com.dylan.library.io.FileUtils;
import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dylan on 2017/1/1.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static Context mContext;
    private static CrashHandler mHandler;
    private Thread.UncaughtExceptionHandler mDefualtHandler;
    private HashMap<String, String> infos = new HashMap<>();
    private SimpleDateFormat formatter = new SimpleDateFormat("yyy--MM--dd");


    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        if (mHandler == null) {
            mHandler = new CrashHandler();
        }
        return mHandler;
    }


    public void init(Context context) {
        mContext = context;
        mDefualtHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    /**
     * 如果用户没有处理异常，则异常会让系统该方法处理
     *
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        saveCrashInfo2File(e);
        mDefualtHandler.uncaughtException(t, e);

    }


    /**
     * 保存错误信息到文件中
     *
     * @param throwable
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable throwable) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        sb.append("\n").append("\n").append("\n");


        String result = ELog.getThrowableContent(throwable);
        sb.append(result);

        long timestamp = System.currentTimeMillis();
        String date = formatter.format(new Date());
        String fileName =date + "_" + timestamp + ".log";

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String path = SDCardUtils.getSDcardCacheDir(mContext) + "/CrashLog/";
            FileUtils.mkdirsIfNotExist(path);//创建文件夹
            String filepath = path + fileName;
            try {
                FileUtils.writeTextToSdcard(sb.toString(), filepath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fileName;
        }

        return null;

    }


    /**
     * 收集设备参数信息
     *
     * @param context
     */
    public void collectDeviceInfo(Context context) {
        try {
            if (context==null)return;
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                String appName = AppUtils.getAppName(mContext);
                infos.put(CrashLog.APP_NAME, appName);
                infos.put(CrashLog.VERSION_NAME, versionName);
                infos.put(CrashLog.VERSION_CODE, versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("collectDeviceInfo", "an error occured when collect package info", e);
        }


        infos.put(CrashLog.PHONE_BRAND, DeviceUtils.getBrand());
        infos.put(CrashLog.PHONE_MODE, DeviceUtils.getMode());
        infos.put(CrashLog.ARCHITETURE, DeviceUtils.getArchi());
        infos.put(CrashLog.SCREEN_WIDTH, String.valueOf(ScreenUtils.getScreenWidth(mContext)));
        infos.put(CrashLog.SCREEN_HEIGHT, String.valueOf(ScreenUtils.getScreenWidth(mContext)));

        MemoryInfo memoryInfo_in = MemoryUtils.getInternalMemory(mContext);
        MemoryInfo memoryInfo_ext = MemoryUtils.getExternalMemory(mContext);
        infos.put(CrashLog.INTERNAL_TOTALSIZE, memoryInfo_in.getTotalSize());
        infos.put(CrashLog.INTERNAL_AVAILABLE, memoryInfo_in.getAvailableSize());
        infos.put(CrashLog.SDCARD_TOTALSIZE, memoryInfo_ext.getTotalSize());
        infos.put(CrashLog.SDCARD_AVAILABLESIZE, memoryInfo_ext.getAvailableSize());
        //infos.put(CrashLog.BATTERY,)
        infos.put(CrashLog.CRASH_TIME, Long.toString(System.currentTimeMillis()));


    }


}
