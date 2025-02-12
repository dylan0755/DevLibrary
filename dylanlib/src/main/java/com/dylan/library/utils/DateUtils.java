package com.dylan.library.utils;

import android.widget.DatePicker;

import com.dylan.library.media.MediaTools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by Dylan on 2016/12/16.
 */

public class DateUtils {

    private DateUtils() {

    }


    private static Calendar calendar = Calendar.getInstance();


    /**
     * 多少天之后的日期
     */
    public static Date getDateByDaysAfter(int dayafter) {
        resetCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, dayafter);
        Date date = calendar.getTime();
        return date;
    }


    public static long parseYMDHM(String dateStr) {
        String formatText = "yyyy-MM-dd HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatText);
        try {
            long selectTime = dateFormat.parse(dateStr).getTime();
            return selectTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static long parseYMDHMS(String dateStr) {
        String formatText = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatText);
        try {
            long selectTime = dateFormat.parse(dateStr).getTime();
            return selectTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 多少月之后的日期
     */
    public static Date getDateByMonthsAfter(int monthafter) {
        resetCalendar();
        calendar.add(Calendar.MONTH, monthafter);
        Date date = calendar.getTime();
        return date;
    }

    public static void resetCalendar() {
        calendar.setTime(new Date());
    }


    public static String getYear() {
        Date date = new Date();
        return String.format("%tY", date);
    }

    public static String getMonth() {
        Date date = new Date();
        return String.format("%tm", date);// 获取当前月份
    }


    public static String getDayOfMonth() {
        Date date = new Date();
        return String.format("%td", date);

    }

    public static int getHourFor24Format(){
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.getDefault());
          Date date = new Date();
          String time = simpleDateFormat.format(date);
          return Integer.parseInt(time);
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

    public static String getWeek(String time) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "星期天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "星期一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "星期二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "星期三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "星期四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "星期五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "星期六";
        }
        return Week;
    }


    public static interface DatePickerListener {
        void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth);
    }


    public static String getCurrentDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd  HH:mm:ss");
        return format.format(date);
    }


    public static String getCurrentData(String pattern) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }


    public static boolean isToday(Date paramDate) {
        //今天的日期
        Date todayDate = new Date();
        String todayYear = String.format("%tY", todayDate);
        String todayMonth = String.format("%tm", todayDate);
        String todayDay = String.format("%td", todayDate);


        String paramDateYear = String.format("%tY", paramDate);
        String paramDateMonth = String.format("%tm", paramDate);
        String paramDateDay = String.format("%td", paramDate);

        return todayYear.equals(paramDateYear)
                && todayMonth.equals(paramDateMonth)
                && todayDay.equals(paramDateDay);
    }


    public static boolean isTomorrow(Date paramDate) {
        //明天的日期
        Date tomorrowDate = getDateByDaysAfter(1);
        String tomorrowYear = String.format("%tY", tomorrowDate);
        String tomorrowMonth = String.format("%tm", tomorrowDate);
        String tomorrowDay = String.format("%td", tomorrowDate);


        String paramDateYear = String.format("%tY", paramDate);
        String paramDateMonth = String.format("%tm", paramDate);
        String paramDateDay = String.format("%td", paramDate);

        return tomorrowYear.equals(paramDateYear)
                && tomorrowMonth.equals(paramDateMonth)
                && tomorrowDay.equals(paramDateDay);
    }


    //是否为今天
    public static boolean isToday(long desTimeStamp) {
        Date currentDate = new Date();
        Date desDate = new Date(desTimeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayTime = format.format(currentDate);
        String desTime = format.format(desDate);
        if (todayTime.equals(desTime)) {
            return true;
        } else {
            return false;
        }
    }

    //是否为明天
    public static boolean isTomorrow(long timeStamp) {
        Calendar todayCalendar = Calendar.getInstance();
        Date currentDay = new Date(System.currentTimeMillis());
        todayCalendar.setTime(currentDay);
        Calendar tomarrowCalendar = Calendar.getInstance();
        Date date = new Date(timeStamp);
        tomarrowCalendar.setTime(date);
        if (todayCalendar.get(Calendar.YEAR) == (tomarrowCalendar.get(Calendar.YEAR))) {
            int diffDay = tomarrowCalendar.get(Calendar.DAY_OF_YEAR) - todayCalendar.get(Calendar.DAY_OF_YEAR);
            if (diffDay == 1) {
                return true;
            }
        }
        return false;
    }

    //是否为后天
    public static boolean isDayAfterTomorrow(long timeStamp) {
        Calendar todayCalendar = Calendar.getInstance();
        Date currentDay = new Date(System.currentTimeMillis());
        todayCalendar.setTime(currentDay);
        Calendar desCalendar = Calendar.getInstance();
        Date date = new Date(timeStamp);
        desCalendar.setTime(date);
        if (todayCalendar.get(Calendar.YEAR) == (desCalendar.get(Calendar.YEAR))) {
            int diffDay = desCalendar.get(Calendar.DAY_OF_YEAR) - todayCalendar.get(Calendar.DAY_OF_YEAR);
            if (diffDay == 2) {
                return true;
            }
        }
        return false;
    }


    //多少毫秒之后是否还是今天
    public static boolean isTodayAfterMilliSecond(long millisecond) {
        //6 小时后有没有过晚上十二点
        long afterSixHourTime = System.currentTimeMillis() + millisecond;
        return isToday(new Date(afterSixHourTime));
    }


    public static String getYM() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String getWantDateString(String yyyMMddHHmmss, String wantFormat) throws ParseException {
        if (yyyMMddHHmmss == null || "".equals(yyyMMddHHmmss)) return yyyMMddHHmmss;
        SimpleDateFormat sdf = new SimpleDateFormat(wantFormat);
        SimpleDateFormat sdfStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdfStr.parse(yyyMMddHHmmss);
        return sdf.format(date);
    }


    @Deprecated
    public static String getWantDateStr(String yyyMMddHHmmss, String wantFormat) {
        if (!"".equals(yyyMMddHHmmss) && yyyMMddHHmmss != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(wantFormat);
            try {
                SimpleDateFormat sdfStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdfStr.parse(yyyMMddHHmmss);
                return sdf.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public static DateTime getCurrentDateTime() {
        //今天的日期
        Date todayDate = new Date();
        String todayYear = String.format("%tY", todayDate);
        String todayMonth = String.format("%tm", todayDate);
        String todayDay = String.format("%td", todayDate);

        //明天的日期
        Date tomorrowDate = getDateByDaysAfter(1);
        String tomorrowYear = String.format("%tY", tomorrowDate);
        String tomorrowMonth = String.format("%tm", tomorrowDate);
        String tomorrowDay = String.format("%td", tomorrowDate);


        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");


        Date date = new Date(System.currentTimeMillis());
        DateTime time = new DateTime();
        String year = String.format("%tY", date);// 获取当前年份
        String month = String.format("%tm", date);// 获取当前月份
        String day = String.format("%td", date);// 获取当前日份的日期号码
        String house = String.format("%tH", date);
        String minute = String.format("%tM", date);
        String second = String.format("%tS", date);

        String dateString = sim.format(date);
        String week = getWeek(dateString);

        if (todayYear.equals(year) && todayMonth.equals(month) && todayDay.equals(day)) {
            time.setIsToday(true);
        } else if (tomorrowYear.equals(year) && tomorrowMonth.equals(month) && tomorrowDay.equals(day)) {
            time.setIsTomorrow(true);
        }


        time.setDayInWeek(week);
        time.setYear(year);
        time.setMonth(month);
        time.setDay(day);
        time.setHour(house);
        time.setMinute(minute);
        time.setSecond(second);


        return time;
    }


    public static List<DateTime> getAllDaysAfter(int days, boolean inCludeToday) {
        //今天的日期
        Date todayDate = new Date();
        String todayYear = String.format("%tY", todayDate);
        String todayMonth = String.format("%tm", todayDate);
        String todayDay = String.format("%td", todayDate);

        //明天的日期
        Date tomorrowDate = getDateByDaysAfter(1);
        String tomorrowYear = String.format("%tY", tomorrowDate);
        String tomorrowMonth = String.format("%tm", tomorrowDate);
        String tomorrowDay = String.format("%td", tomorrowDate);


        List<DateTime> dateTimes = new ArrayList<>();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
        int start = inCludeToday ? 0 : 1;
        for (int i = start; i < days + start; i++) {
            Date date = getDateByDaysAfter(i);
            DateTime time = new DateTime();
            String year = String.format("%tY", date);// 获取当前年份
            String month = String.format("%tm", date);// 获取当前月份
            String day = String.format("%td", date);// 获取当前日份的日期号码
            String house = String.format("%tH", date);
            String minute = String.format("%tM", date);
            String second = String.format("%tS", date);

            String dateString = sim.format(date);
            String week = getWeek(dateString);

            if (todayYear.equals(year) && todayMonth.equals(month) && todayDay.equals(day)) {
                time.setIsToday(true);
            } else if (tomorrowYear.equals(year) && tomorrowMonth.equals(month) && tomorrowDay.equals(day)) {
                time.setIsTomorrow(true);
            }


            time.setDayInWeek(week);

            time.setYear(year);
            time.setMonth(month);
            time.setDay(day);
            time.setHour(house);
            time.setMinute(minute);
            time.setSecond(second);
            dateTimes.add(time);
        }
        return dateTimes;
    }


    public static String getYMD(long dateL) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(dateL));
    }

    public static String getYMDHHmm(long dateL) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        return format.format(new Date(dateL));
    }
    public static String getYMDHHmmss(long dateL) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        return format.format(new Date(dateL));
    }

    public static class DateTime {

        private String year;
        private String month;
        private String day;
        private String hour;
        private String minute;
        private String second;
        private String dayInWeek;
        private boolean isToday;
        private boolean isTomorrow;


        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getDayInWeek() {
            return dayInWeek;
        }

        public void setDayInWeek(String dayInWeek) {
            this.dayInWeek = dayInWeek;
        }


        public boolean isToday() {
            return isToday;
        }

        public void setIsToday(boolean today) {
            isToday = today;
        }


        public boolean isTomorrow() {
            return isTomorrow;
        }


        public void setIsTomorrow(boolean tomorrow) {
            isTomorrow = tomorrow;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }

        public String getMinute() {
            return minute;
        }

        public void setMinute(String minute) {
            this.minute = minute;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }


    }



    public static boolean isSameDay(Date firstDate,Date secondDate) {
        if (firstDate == null) {
            throw new IllegalArgumentException("date is null");
        }
        if (secondDate == null) {
            throw new IllegalArgumentException("date is null");
        }

        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.setTime(firstDate);
        Calendar secondCalendar = Calendar.getInstance();
        secondCalendar.setTime(secondDate);
        return (firstCalendar.get(Calendar.ERA) == secondCalendar.get(Calendar.ERA) &&
                firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR) &&
                firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR));
    }


    public static String formatHmsS(long ms){
        return MediaTools.formatHmsS(ms,false);
    }


    public static String transIsoLocalDateTimeTo(String isoLocalDateTime,String pattern){
        //String dateTimeString = "2024-10-24T11:23:44";
        SimpleDateFormat formatterFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = formatterFrom.parse(isoLocalDateTime);
            // String pattern="yyyy/MM/dd HH:mm:ss";
            SimpleDateFormat formatterTo = new SimpleDateFormat(pattern);
            return formatterTo.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date getDateFromIsoLocalDateTime(String isoLocalDateTime){
        //String dateTimeString = "2024-10-24T11:23:44";
        SimpleDateFormat formatterFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = formatterFrom.parse(isoLocalDateTime);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
