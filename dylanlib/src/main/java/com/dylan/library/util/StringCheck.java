package com.dylan.library.util;

import android.util.Log;

/**
 * Created by Dylan on 2016/6/2.
 */
public class StringCheck {
    private static String TAG = "StringCheck";

    public static boolean isValid(String str) {
        if (str != null && !str.isEmpty()) {
            return true;
        } else if (str == null) {
            return false;
        } else {
            return false;
        }
    }

    /**
     * 这个方法会提示变量是null还是 empty
     */
    public static boolean isValid(String variableName, String str) {
        if (str != null && !str.isEmpty()) {
            return true;
        } else if (str == null) {
            Log.e(TAG, variableName + " == null");
            return false;
        } else {
            Log.e(TAG, variableName + " is Empty");
            return false;
        }
    }


}
