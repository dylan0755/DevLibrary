package com.dylan.library.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dylan on 2016/11/26.
 */

public class TimeUtil {


    /**
     *
     * 日期转时间戳和时间戳转字符串
     */


    public static String date2TimeStamp(String date_str) {
        try {
            String format = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String date2TimeStamp(String date_str,String format) {
        if (format==null||format.isEmpty())return "";
        try {

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String timeStamp2Date(String seconds) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }



    public static String timeStamp2Date(String seconds,String format) {
        if (format==null||format.isEmpty())return "";
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }
}
