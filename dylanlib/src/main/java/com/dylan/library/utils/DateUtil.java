package com.dylan.library.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import com.dylan.library.R;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by Dylan on 2016/12/16.
 */

public class DateUtil {

    private DateUtil(){

    }



    private static Calendar calendar=Calendar.getInstance();
    private static DatePickerDialog mDatePicker;


    /**
     * 多少天之后的日期
     */
    public static void getDateByDaysAfter(int dayafter){
        resetCalendar();
        calendar.add(Calendar.DAY_OF_MONTH,dayafter);
    }

    /**
     * 多少月之后的日期
     */
    public static void getDateByMonthsAfter(int monthafter){
        resetCalendar();
        calendar.add(Calendar.MONTH,monthafter);
    }

    public static void resetCalendar(){
        calendar.setTime(new Date());
    }



    public static String getYear(){
        String year=Integer.toString(calendar.get(Calendar.YEAR));
        return year;
    }

    public static String getMonth(){
        String month=Integer.toString(calendar.get(Calendar.MONTH)+1);
        return month;
    }

    public static String getDayOfMonth(){
        String day=Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        return day;
    }

    public static String getDayOfWeek(){
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



    public static void showDatePickerDialog(Context context, final DatePickerListener listener) {
        Calendar calendar = Calendar.getInstance();
        if (mDatePicker == null) {
            mDatePicker = new DatePickerDialog(context, R.style.datePickerTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int year, int month, int dayofmonth) {
                    String date = year + "-" + (month + 1) + "-" + dayofmonth;
                    if (listener!=null)listener.onDateSet(arg0,year,(month + 1),dayofmonth);

                }
            },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        mDatePicker.show();
    }


    public static interface DatePickerListener{
        void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth);
    }

}
