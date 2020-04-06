package com.dylan.library.utils;

import android.util.Base64;

/**
 * Created by Dylan on 2017/12/1.
 */

public class Base64Utils {
    public static String encodeToString(byte[] bytes){
        return Base64.encodeToString(bytes, Base64.NO_WRAP);//这里的模式要选择不能换行，否则解码出来会有乱码
    }

    public static String decode(String encodeString){
        byte[] bytes= Base64.decode(encodeString, Base64.NO_WRAP);
        return new String(bytes);
    }

}
