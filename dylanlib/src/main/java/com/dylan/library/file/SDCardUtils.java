package com.dylan.library.file;

import android.os.Environment;

/**
 * Created by Dylan on 2017/1/1.
 */

public class SDCardUtils {

    public static boolean isSDcardExist() {
        boolean flag = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        return flag;
    }

    public static String getSDcardDir(){
        if (isSDcardExist()){
            String dir=Environment.getExternalStorageDirectory().toString();
            return dir;
        }
        return "";
    }
}
