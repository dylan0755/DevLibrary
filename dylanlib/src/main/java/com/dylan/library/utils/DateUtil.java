package com.dylan.library.utils;

import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Dylan on 2016/12/16.
 */

public class DateUtil {

    private DateUtil() {

    }


    private static Calendar calendar = Calendar.getInstance();


    /**
     * 多少天之后的日期
     */
    public static Date getDateByDaysAfter(int dayafter) {
        resetCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, dayafter);
        Date date=calendar.getTime();
        return date;
    }

    /**
     * 多少月之后的日期
     */
    public static Date getDateByMonthsAfter(int monthafter) {
        resetCalendar();
        calendar.add(Calendar.MONTH, monthafter);
        Date date=calendar.getTime();
        return date;
    }

    public static void resetCalendar() {
        calendar.setTime(new Date());
    }


    public static String getYear() {
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        return year;
    }

    public static String getMonth() {
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        return month;
    }

    public static String getDayOfMonth() {
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        return day;
    }

    public static String getDayOfWeek() {
        String weakday = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(weakday)) {
            weakday = "天";
        } else if ("2".equals(weakday)) {
            weakday = "一";
        } else if ("3".equals(weakday)) {
            weakday = "二";
        } else if ("4".equals(weakday)) {
            weakday = "三";
        } else if ("5".equals(weakday)) {
            weakday = "四";
        } else if ("6".equals(weakday)) {
            weakday = "五";
        } else if ("7".equals(weakday)) {
            weakday = "六";
        }
        return weakday;
    }





    public static interface DatePickerListener {
        void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth);
    }


    public static String getCurrentDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd  HH:mm:ss");
        return format.format(date);
    }


    public static String getCurrentData(String pattern){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

}
