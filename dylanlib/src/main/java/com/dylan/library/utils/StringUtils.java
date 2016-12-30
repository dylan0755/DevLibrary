package com.dylan.library.utils;

import android.util.Log;

/**
 * Created by Dylan on 2016/6/2.
 */
public class StringUtils {
    private static String TAG = "StringUtils";

    public static boolean isValid(String strObj) {
        if (strObj != null && !strObj.isEmpty()) {
            return true;
        } else if (strObj == null) {
            return false;
        } else {
            return false;
        }
    }


    /**
     * 这个方法会提示变量是null还是 empty
     */
    public static boolean isValid( String strObj,String variableName) {
        if (strObj != null && !strObj.isEmpty()) {
            return true;
        } else if (strObj == null) {
            Log.e(TAG, variableName + " == null");
            return false;
        } else {
            Log.e(TAG, variableName + " is Empty");
            return false;
        }
    }


    public static boolean isInValid(String strObj){
           return !isValid(strObj);
    }

    public static boolean isInValid(String strObj,String variableName){
        return !isValid(strObj,variableName);
    }


}
