package com.dylan.library.utils;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;


import com.dylan.library.bean.DateInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * 日历工具类
 * @author ly
 * date 2020/4/24 9:52
 */
public class CalendarUtils {
    public final static String YEAR = "yyyy";
    public final static String YEAR_MONTH = "yyyy年MM月";
    public final static String MONTH_DAY = "MM-dd";

    public final static String DATE = "yyyy-MM-dd";
    public final static String TIME = "HH:mm";
    public final static String MONTH_DAY_TIME = "MM月dd日  hh:mm";

    public final static String DATE_TIME_SECOND = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_TIME = "yyyy-MM-dd HH:mm";
    public final static String DATE1_TIME = "yyyy/MM/dd HH:mm";


    public static @NonNull
    LinkedHashMap<Long, ArrayList<DateInfo>> getMonthMap(Calendar initialCalendar, int monthNum) {
        return getMonthMap(initialCalendar, monthNum, Integer.MAX_VALUE);
    }

    public static @NonNull
    LinkedHashMap<Long, ArrayList<DateInfo>> getMonthByLimit(Calendar initialCalendar, int validSelNumLimit) {
        return getMonthMap(initialCalendar, Integer.MAX_VALUE, validSelNumLimit);
    }

    /**
     * 以月为单位，获取每个月的日期列表
     *
     * @param initialCalendar  初始选中的日期
     * @param validSelNumLimit 最大有效选中数量
     * @return 返回一个key为当前loop月的时间戳，value为当前月的所有日期list的有序map
     * @author ly on 2020/4/25 11:37
     */
    public static @NonNull
    LinkedHashMap<Long, ArrayList<DateInfo>> getMonthMap(Calendar initialCalendar, int monthNum, int validSelNumLimit) {
        //key：当前loop月的时间戳 value：当前月的所有日期list
        LinkedHashMap<Long, ArrayList<DateInfo>> dateMap = new LinkedHashMap<>();
        ArrayList<DateInfo> dateInMonthList = new ArrayList<>();

        if (initialCalendar == null)
            initialCalendar = Calendar.getInstance();

        Calendar c = Calendar.getInstance();
        //设置开始遍历的date
        //也可以不使用下面的set，直接c.setTime(initialCalendar.getTime())，只是第一个月的数据可能不足那个月的最大天数（ui不美观）
        //告诉calendar从指定月的第一天开始，而不是精确到某一天
        c.set(initialCalendar.get(Calendar.YEAR), initialCalendar.get(Calendar.MONTH), 1);

        int loopMonthCount = 0;
        int validDateNum = 0;//有效的可选日期数量
        while (loopMonthCount < monthNum) {
            // 获取当前月最大天数
            int maxDayNum = c.getActualMaximum(Calendar.DATE);

            int month = c.get(Calendar.MONTH);
            DateInfo dateInfo = new DateInfo();
            dateInfo.year = c.get(Calendar.YEAR);
            dateInfo.month = month + 1;
            dateInfo.day = c.get(Calendar.DAY_OF_MONTH);
            dateInfo.timestamp = c.getTimeInMillis();
            dateInfo.week = c.get(Calendar.DAY_OF_WEEK);
            dateInfo.isSelected = initialCalendar.get(Calendar.MONTH) == c.get(Calendar.MONTH) && initialCalendar.get(Calendar.DATE) == c.get(Calendar.DATE);
            //是否为可选的日期
            boolean isAvailableDate = (dateInfo.isSelected || c.after(initialCalendar)) && dateInfo.week != Calendar.SUNDAY && dateInfo.week != Calendar.SATURDAY;
            if (isAvailableDate)
                validDateNum++;

            dateInfo.canSelect = isAvailableDate && validDateNum <= validSelNumLimit;

            if (dateInMonthList.isEmpty()) {//在每一月的第一周前面补空data
                for (int j = 0; j < dateInfo.week - 1; j++) {
                    DateInfo d = new DateInfo();
                    dateInMonthList.add(0, d);
                }
            }
            dateInMonthList.add(dateInfo);

            if (maxDayNum == dateInfo.day) {//每loop到月底put一次
                dateMap.put(dateInfo.timestamp, new ArrayList<>(dateInMonthList));
                dateInMonthList.clear();

                if (validDateNum < validSelNumLimit) {
                    loopMonthCount++;
                } else {
                    break;//达到最大可选数后自动跳出looper
                }
            }

            //条件满足时一直add天数即可
            c.add(Calendar.DATE, 1);
        }
        return dateMap;
    }

    public static @NonNull
    LinkedHashMap<Long, ArrayList<DateInfo>> getMonthMap(String startDate, String endDate) {
        //key：当前loop月的时间戳 value：当前月的所有日期list
        LinkedHashMap<Long, ArrayList<DateInfo>> dateMap = new LinkedHashMap<>();
        ArrayList<DateInfo> dateInMonthList = new ArrayList<>();

        long startTimestamp = getMillisecondByFormat(startDate, DATE);
        long endTimestamp = getMillisecondByFormat(endDate, DATE);
        if (endTimestamp < startTimestamp || startTimestamp <= 0)
            return dateMap;

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startTimestamp);

        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endTimestamp);

        Calendar c = Calendar.getInstance();
        //设置开始遍历的date
        //也可以不使用下面的set，直接c.setTime(initialCalendar.getTime())，只是第一个月的数据可能不足那个月的最大天数（ui不美观）
        //告诉calendar从指定月的第一天开始，而不是精确到某一天
        c.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), 1);

        boolean needLoop = true;
        while (needLoop) {

            // 获取当前月最大天数
            int maxDayNum = c.getActualMaximum(Calendar.DATE);

            int month = c.get(Calendar.MONTH);
            DateInfo dateInfo = new DateInfo();
            dateInfo.year = c.get(Calendar.YEAR);
            dateInfo.month = month;
            dateInfo.day = c.get(Calendar.DAY_OF_MONTH);
            dateInfo.timestamp = c.getTimeInMillis();
            dateInfo.week = c.get(Calendar.DAY_OF_WEEK);
            //此处理想条件应为 start.getTimeInMillis()==c.getTimeInMillis()，但开始时间戳和查询出来的时间戳不相等（就算是同一天）
            //先用这个判断着
            dateInfo.isSelected = start.get(Calendar.YEAR) == c.get(Calendar.YEAR) && start.get(Calendar.MONTH) == c.get(Calendar.MONTH) && start.get(Calendar.DATE) == c.get(Calendar.DATE);
            boolean isEnd = end.get(Calendar.MONTH) == c.get(Calendar.MONTH) && end.get(Calendar.DATE) == c.get(Calendar.DATE);
            dateInfo.canSelect = (dateInfo.isSelected || c.after(start)) && (c.before(end) || isEnd) && dateInfo.week != Calendar.SUNDAY && dateInfo.week != Calendar.SATURDAY;

            if (dateInMonthList.isEmpty()) {//在每一月的第一周前面补空data
                for (int j = 0; j < dateInfo.week - 1; j++) {
                    DateInfo d = new DateInfo();
                    dateInMonthList.add(0, d);
                }
            }
            dateInMonthList.add(dateInfo);

            if (maxDayNum == dateInfo.day) {//每loop到月底put一次
                dateMap.put(dateInfo.timestamp, new ArrayList<>(dateInMonthList));
                dateInMonthList.clear();

                needLoop = c.before(end);//每到月底判断一下，是否还需继续loop
            }

            //条件满足时一直add天数即可
            c.add(Calendar.DATE, 1);
        }
        return dateMap;
    }

    /**
     * 获取当前日期一周的日期
     */
    @SuppressLint("SimpleDateFormat")
    public static ArrayList<DateInfo> getWeek(String date) {
        ArrayList<DateInfo> result = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); //获取本周一的日期
        for (int i = 0; i < 7; i++) {
            DateInfo entity = new DateInfo();
            entity.timestamp = c.getTimeInMillis();
            entity.day = c.get(Calendar.DATE);
            entity.week = c.get(Calendar.DAY_OF_WEEK);
            c.add(Calendar.DATE, 1);
            result.add(entity);
        }
        return result;
    }

    /**
     * 获取系统当前日期
     */
    public static String getCurrDate(String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }


    /**
     * String类型的日期时间转化为毫秒（1970-）类型.
     *
     * @param strDate String形式的日期时间
     * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @author LY 2015-9-16 上午11:40:26
     */
    public static long getMillisecondByFormat(String strDate, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = mSimpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) return 0;
        return date.getTime();
    }

    /**
     * 毫秒----> format格式的日期
     *
     * @author LY 2015-9-18 下午3:41:32
     */
    public static String getDateByMillisecond(long milliseconds, String format) {
        Date d = new Date(milliseconds);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat(format);
        return f.format(d);
    }

    public static String getCurDateByFormat(String format) {
        return getDateByMillisecond(System.currentTimeMillis(), format);
    }

    public static String getNextMonthByFormat(String format) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.MONTH, 1);

        Date d = c.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat(format);
        return f.format(d);
    }


    public static int getLastDayInMonth(Calendar calendar){
        Calendar newCalendar=Calendar.getInstance();
        newCalendar.setTime(calendar.getTime());
        newCalendar.set(Calendar.DAY_OF_MONTH,1);
        newCalendar.add(Calendar.MONTH,1);
        newCalendar.add(Calendar.DATE,-1);
        return newCalendar.get(Calendar.DAY_OF_MONTH);
    }

}
