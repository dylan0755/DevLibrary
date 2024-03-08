package com.dylan.library.bean;


import com.dylan.library.utils.CalendarUtils;

import java.util.Calendar;

/**
 * @author ly
 * date 2020/4/24 9:52
 */
public class DateInfo {
    public long timestamp; //时间戳
    public int year;
    public int month;
    public int day;
    public int week;//一周中第几天，非中式
    //是否选中
    public boolean isSelected;
    //日期是否可选
    public boolean canSelect;

    /**
     * 根据美式周末到周一 返回
     */
    public String getWeekName() {
        String name = "";
        switch (week) {
            case Calendar.SUNDAY:
                name = "周日";
                break;
            case Calendar.MONDAY:
                name = "周一";
                break;
            case Calendar.TUESDAY:
                name = "周二";
                break;
            case Calendar.WEDNESDAY:
                name = "周三";
                break;
            case Calendar.THURSDAY:
                name = "周四";
                break;
            case Calendar.FRIDAY:
                name = "周五";
                break;
            case Calendar.SATURDAY:
                name = "周六";
                break;
            default:
                break;
        }
        return name;
    }

    public String getDay() {
        if (day > 0) {
            return String.valueOf(day);
        } else {
            return "";
        }
    }

    public int getMonth() {
        return month;
    }

    public String getDate() {
        return CalendarUtils.getDateByMillisecond(timestamp, CalendarUtils.DATE);
    }

}
