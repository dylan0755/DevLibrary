package com.dylan.library.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by Dylan on 2017/12/1.
 */

public class Base64Utils {

    public static byte[] encode(byte[] bytes){
        return Base64.encode(bytes, Base64.NO_WRAP);//这里的模式要选择不能换行，否则解码出来会有乱码
    }

    public static String encodeToString(byte[] bytes) throws Exception {
        return new String(encode(bytes),"UTF-8");
    }

    public static byte[] decode(String encodeString){
        return  Base64.decode(encodeString, Base64.NO_WRAP);
    }

    public static String decodeToString(String encodeString) throws Exception{
        return new String(decode(encodeString),"UTF-8");
    }


}
