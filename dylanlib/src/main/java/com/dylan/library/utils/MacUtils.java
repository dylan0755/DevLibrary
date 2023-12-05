package com.dylan.library.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 获取Mac地址工具类
 * 必须在联网的情况下才能获取到mac地址
 */
public class MacUtils {

    private static final String TAG = "MacUtil";

    private MacUtils() {
    }

    /**
     * 获取当前系统连接网络的网卡的mac地址
     */
    public static final String getMac() {
        byte[] mac = null;
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces != null && netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address) || ip.isLoopbackAddress())
                        continue;
                    if (ip.isSiteLocalAddress()) {
                        mac = ni.getHardwareAddress();
                        Log.e(TAG, "获取Mac地址的网卡名：" + ni.getDisplayName());
                    } else if (!ip.isLinkLocalAddress()) {
                        mac = ni.getHardwareAddress();
                        Log.e(TAG, "获取Mac地址的网卡名：" + ni.getDisplayName());
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return getMacString(mac);
    }

    /**
     * 获取wifi模块的mac地址
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String getWifiMac(Context context) {
        //return getNetworkInterfaceMac("wlan0");
        WifiManager wifiManager = context.getSystemService(WifiManager.class);
        String macAddress = null;
        try {
            Method method = WifiManager.class.getMethod("getFactoryMacAddresses");
            final String[] macAddresses = (String[])method.invoke(wifiManager);
            if (macAddresses != null && macAddresses.length > 0) {
                macAddress = macAddresses[0];
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return macAddress;
    }

    public static String getP2pMac() {
        return getNetworkInterfaceMac("p2p0");
    }

    public static String getIp6tnl0Mac() {
        return getNetworkInterfaceMac("ip6tnl0");
    }

    public static String getIpVti0Mac() {
        return getNetworkInterfaceMac("ip_vti0");
    }

    public static String getLoMac() {
        return getNetworkInterfaceMac("lo");
    }

    public static String getTeql0Mac() {
        return getNetworkInterfaceMac("teql0");
    }

    public static String getSit0Mac() {
        return getNetworkInterfaceMac("sit0");
    }

    public static String getIp6Vti0Mac() {
        return getNetworkInterfaceMac("ip6_vti0");
    }

    /**
     * 获取有线网卡模块的mac地址
     */
    public static String getEthernetMac() {
        return getNetworkInterfaceMac("eth0");
    }

    /**
     * 获取指定网卡mac地址
     */
    private static String getNetworkInterfaceMac(String networkInterfaceName) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (networkInterfaceName.equals(ni.getName())) {
                    return getMacString(getMacBytes(ni));
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getMacBytes(NetworkInterface ni) {
        byte[] mac = null;
        try {
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress ip = address.nextElement();
                if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address) || ip.isLoopbackAddress())
                    continue;
                if (ip.isSiteLocalAddress())
                    mac = ni.getHardwareAddress();
                else if (!ip.isLinkLocalAddress()) {
                    mac = ni.getHardwareAddress();
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mac;
    }

    private static String getMacString(byte[] mac) {
        if (mac != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                sb.append(parseByte(mac[i]));
            }
            return sb.substring(0, sb.length() - 1);
        }
        return null;
    }

    private static String parseByte(byte b) {
        String s = "00" + Integer.toHexString(b) + ":";
        return s.substring(s.length() - 3);
    }
    public static String getBluetoothMac(BluetoothAdapter adapter) {
        if (adapter == null) return null;

        Class<? extends BluetoothAdapter> btAdapterClass = adapter.getClass();
        try {
            Class<?> btClass = Class.forName("android.bluetooth.IBluetooth");
            Field bluetooth = btAdapterClass.getDeclaredField("mService");
            bluetooth.setAccessible(true);
            Method btAddress = btClass.getMethod("getAddress");
            btAddress.setAccessible(true);
            return (String) btAddress.invoke(bluetooth.get(adapter));
        } catch (Exception e) {
            Logger.w(TAG, "Call Bluetooth by reflection failed.");
            return adapter.getAddress();
        }
    }
}
