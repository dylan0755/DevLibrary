package com.dylan.library.device;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

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


}
