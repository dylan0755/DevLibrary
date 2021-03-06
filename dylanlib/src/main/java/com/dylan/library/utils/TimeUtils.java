package com.dylan.library.utils;

import android.util.Log;

import com.dylan.library.exception.ELog;

import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dylan on 2016/11/26.
 */

@Deprecated
public class TimeUtils {


    /**
     * 获取网络时间
     * @return
     */
    public static long getServerTimeMillSeconds(){
        try {
            URL url= new URL("http://www.baidu.com");
            URLConnection uc=url.openConnection();
            uc.connect();
            long dateLong=uc.getDate();
            return dateLong;
        } catch (Exception e) {
            if(e instanceof UnknownHostException){
                Log.e("TimeUtils","网络断开" );
            }else{
                ELog.e(e);
            }
        }
        return 0;
    }
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

    //是否为今天
    public static boolean isToday(long desTimeStamp){
        Date currentDate=new Date();
        Date desDate=new Date(desTimeStamp);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String todayTime=format.format(currentDate);
        String desTime=format.format(desDate);
        if (todayTime.equals(desTime)){
            return true;
        }else{
            return false;
        }
    }

    //是否为明天
    public static boolean isTomorrow(long timeStamp){
        Calendar todayCalendar = Calendar.getInstance();
        Date currentDay = new Date(System.currentTimeMillis());
        todayCalendar.setTime(currentDay);
        Calendar tomarrowCalendar = Calendar.getInstance();
        Date date =new Date(timeStamp);
        tomarrowCalendar.setTime(date);
        if (todayCalendar.get(Calendar.YEAR) == (tomarrowCalendar.get(Calendar.YEAR))) {
            int diffDay = tomarrowCalendar.get(Calendar.DAY_OF_YEAR) - todayCalendar.get(Calendar.DAY_OF_YEAR);
            if (diffDay ==1) {
                return true;
            }
        }
        return false;
    }
    //是否为后天
    public static boolean isDayAfterTomorrow(long timeStamp){
        Calendar todayCalendar = Calendar.getInstance();
        Date currentDay = new Date(System.currentTimeMillis());
        todayCalendar.setTime(currentDay);
        Calendar desCalendar = Calendar.getInstance();
        Date date =new Date(timeStamp);
        desCalendar.setTime(date);
        if (todayCalendar.get(Calendar.YEAR) == (desCalendar.get(Calendar.YEAR))) {
            int diffDay = desCalendar.get(Calendar.DAY_OF_YEAR) - todayCalendar.get(Calendar.DAY_OF_YEAR);
            if (diffDay ==2) {
                return true;
            }
        }
        return false;
    }


    public static String[] getCountTimeStartWidthDay(long millTimeStmap){
        millTimeStmap-=System.currentTimeMillis();//减去当前时间
        long secondTime = millTimeStmap / 1000;//转成秒单位的
        int hour = (int) (secondTime / 3600);
        int minute = (int) (secondTime % 3600);
        if (minute != 0) {
            minute = minute / 60;
        }
        int second = (int) (secondTime % 60);
        String dayStr=null;
        String houstr = null;
        String minuteStr = null;
        String secondStr = null;
        if (hour < 10) {
            houstr = "0" + hour;
        } else {
            if (hour>=24){
                //天数
                int  day=hour/24;
                if (day>0&&day<10){
                    dayStr="0"+Integer.toString(day);
                }else{
                    dayStr=Integer.toString(day);
                }
                //小时
                hour=hour%24;
                if (hour<10){
                    houstr = "0" + hour;
                }else{
                    houstr = String.valueOf(hour);
                }
            }else{
                houstr = String.valueOf(hour);
                dayStr=Integer.toString(0);
            }
        }
        if (minute < 10) {
            minuteStr = "0" + minute;
        } else {
            minuteStr = String.valueOf(minute);
        }

        if (second < 10) {
            secondStr = "0" + second;
        } else {
            secondStr = String.valueOf(second);
        }
        String[] timeArray=new String[]{dayStr,houstr,minuteStr,secondStr};
        return timeArray;
    }

}
