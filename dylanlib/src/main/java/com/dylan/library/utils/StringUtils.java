package com.dylan.library.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dylan on 2016/6/2.
 */
public class StringUtils {
    private static String TAG = "StringUtils";
    private StringUtils(){

    }
    public static boolean isNotEmpty(String strObj) {
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
    public static boolean isNotEmpty(String strObj, String variableName) {
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


    public static boolean isEmpty(String strObj) {
        return !isNotEmpty(strObj);
    }

    public static boolean isEmpty(String strObj, String variableName) {
        return !isNotEmpty(strObj, variableName);
    }


    public static String upperFirstLetter(String str) {
        if (isEmpty(str) || !Character.isLowerCase(str.charAt(0))) return str;
        String endStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        return endStr;
    }

    public static String lowerFirstLetter(String str) {
        if (isEmpty(str) || !Character.isUpperCase(str.charAt(0))) return str;
        String endStr = str.substring(0, 1).toLowerCase() + str.substring(1);
        return endStr;
    }


    public static String reverse(String s) {
        if(isEmpty(s))return "";
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
            if (isNotEmpty(str)){
                if (i!=size-1)buffer.append(str).append(separator);
                else buffer.append(str);
            }
        }

        return buffer.toString();
    }


    public static boolean containsOneStr(String[] array,String str){
         if (array==null||array.length==0)return false;
         for (int i=0,len=str.length();i<len;i++){
             if (array[i].equals(str))return true;
         }
         return false;
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


    public static CharSequence splitColor(String str,int limitStart,int limitEnd,String splitColor){
        if (str==null)return str;
        SpannableString span=null;
        try{
            span= new SpannableString(str);
            ForegroundColorSpan fspan = new ForegroundColorSpan(Color.parseColor(splitColor));
            span.setSpan(fspan, limitStart, limitEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }catch (Exception e){

        }
        return span;
    }

    //全角转半角
    public static String ToDBC(String input) {
        if (input==null)return "";
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    /**
     * 半角转全角
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }


    /**
     * 判断是否为数字
     */
    public static boolean isNumber(String str){
        String pattern = "^[\\+\\-]?[\\d]+(\\.[\\d]+)?$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return m.matches();
    }
}
