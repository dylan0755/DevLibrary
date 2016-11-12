package com.dylan.library.media;

/**
 * Created by Dylan on 2016/9/22.
 */
public class MediaTime {
    public static int HOURSE_TIME = 1;
    public static int MINUTE_TIME = 2;

    /**
     * @param time       long时间戳
     * @param timeformat 最高位是时还是分钟
     * @return
     */
    public static String getMediaDurtionTime(long time, int timeformat) {//
        long secondTime = time / 1000;
        int hour = (int) (secondTime / 3600);
        int minute = (int) (secondTime % 3600);
        if (minute != 0) {
            minute = minute / 60;
        }
        int second = (int) (secondTime % 60);
        String houstr = null;
        String minuteStr = null;
        String secondStr = null;
        if (hour < 10) {
            houstr = "0" + hour;
        } else {
            houstr = String.valueOf(hour);
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

        String endtime = null;
        if (timeformat == MINUTE_TIME&&houstr.equals("00")) {//只有时位为0才输出分位
            endtime = minuteStr + ":" + secondStr;
        } else {//默认最高位是时位
            endtime = houstr + ":" + minuteStr + ":" + secondStr;
        }
        return endtime;
    }


    public static String getMediaDurtionTime(long time) {//
        long secondTime = time / 1000;
        int hour = (int) (secondTime / 3600);
        int minute = (int) (secondTime % 3600);
        if (minute != 0) {
            minute = minute / 60;
        }
        int second = (int) (secondTime % 60);
        String houstr = null;
        String minuteStr = null;
        String secondStr = null;
        if (hour < 10) {
            houstr = "0" + hour;
        } else {
            houstr = String.valueOf(hour);
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


        String endtime = houstr + ":" + minuteStr + ":" + secondStr;

        return endtime;
    }
}
