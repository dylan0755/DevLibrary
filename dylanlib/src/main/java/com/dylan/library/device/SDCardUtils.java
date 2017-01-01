package com.dylan.library.device;

import android.content.Context;
import android.os.Environment;

/**
 * Created by Dylan on 2017/1/1.
 */

public class SDCardUtils {

    public static boolean isSDcardExist() {
        boolean flag = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        return flag;
    }

    public static String getSDcardDir() {
        if (isSDcardExist()) {
            String dir = Environment.getExternalStorageDirectory().toString();
            return dir;
        }
        return "";
    }


    /**
     * 获取SDcard下的  "Android/data/包名
     * @param context
     * @return
     */
    public static String getSDcardCacheDir(Context context) {
        String path = getSDcardDir() + "/" + "Android/data/" + context.getPackageName();
        return path;
    }

}
