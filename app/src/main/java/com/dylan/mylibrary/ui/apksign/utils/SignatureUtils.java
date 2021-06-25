package com.dylan.mylibrary.ui.apksign.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Dylan on 2017/11/27.
 */

public class SignatureUtils {
    public static String getMD5Signature(Context context, String packageName) {
        try {
            PackageInfo packageInfo = null;
            PackageManager manager = context.getPackageManager();
            packageInfo = manager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            /******* 通过返回的包信息获得签名指纹数组 *******/
            Signature[] signatures = packageInfo.signatures;
            if (signatures == null || signatures.length == 0) {
                Toast.makeText(context, "没有签名", Toast.LENGTH_SHORT).show();
                return "";
            }
            String signature = toMD5(signatures[0].toByteArray());
            return signature;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context,"找不到该包名",Toast.LENGTH_SHORT).show();
        }
        return "";
    }


    private static String toMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }
}
