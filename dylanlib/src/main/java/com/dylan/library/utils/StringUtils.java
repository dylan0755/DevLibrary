package com.dylan.library.utils;

import android.util.Log;

/**
 * Created by Dylan on 2016/6/2.
 */
public class StringUtils {
    private static String TAG = "StringUtils";
    private StringUtils(){

    }
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
    public static boolean isValid(String strObj, String variableName) {
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


    public static boolean isInValid(String strObj) {
        return !isValid(strObj);
    }

    public static boolean isInValid(String strObj, String variableName) {
        return !isValid(strObj, variableName);
    }


    public static String upperFirstLetter(String str) {
        if (isInValid(str) || !Character.isLowerCase(str.charAt(0))) return str;
        String endStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        return endStr;
    }

    public static String lowerFirstLetter(String str) {
        if (isInValid(str) || !Character.isUpperCase(str.charAt(0))) return str;
        String endStr = str.substring(0, 1).toLowerCase() + str.substring(1);
        return endStr;
    }


    public static String reverse(String s) {
        if(isInValid(s))return "";
        StringBuffer buff=new StringBuffer();
        buff.append(s);
        return buff.reverse().toString();
    }

    /**
     *
     * @param args
     * @param separator 分隔符
     * @return
     */
    public static String append(String[] args,String separator){
        if (args==null||args.length==0)return "";
        if (separator==null)separator="、";
        StringBuffer buffer=new StringBuffer();
        for (int i=0,size=args.length;i<size;i++){
            String str=args[i];
            if (isValid(str)){
                if (i!=size-1)buffer.append(str).append(separator);
                else buffer.append(str);
            }
        }

        return buffer.toString();
    }





    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }


    /**
     *   判断是否包含中文，支持 。 ，中文字符
     */
    public static boolean containsChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i< ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

}
