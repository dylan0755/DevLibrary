package com.dylan.library.media;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.util.HashMap;

/**
 * Created by Dylan on 2016/9/22.
 */
public class MediaTools {
    public static int HOUR_TIME = 1;
    public static int MINUTE_TIME = 2;

    //xx分：xx秒
    public static String getDurationMinuteFormat(long time){
        return getMediaDurationTime(time,MINUTE_TIME);
    }
    //xx时：xx分：xx秒
    public static String getDurationHourFormat(long time){
        return getMediaDurationTime(time, HOUR_TIME);
    }
    //xx天xx时：xx分：xx秒
    public static String getDurationDayHourMinuteSecond(long time){
        long secondTime = time / 1000L;
        int hour = (int)(secondTime / 3600L);
        int minute = (int)(secondTime % 3600L);
        if (minute != 0) {
            minute /= 60;
        }

        int day=hour/24;
        hour=hour%24;



        int second = (int)(secondTime % 60L);
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

        String dayStr="";
        if (day<10&&day!=0){
            dayStr="0"+day;
        }else{
            dayStr=""+day;
        }
        return dayStr+"天"+ houstr + ":" + minuteStr + ":" + secondStr;
    }



    /**
     * @param time       long时间戳
     * @param timeformat 最高位是时还是分钟
     * @return
     */
    private static String getMediaDurationTime(long time, int timeformat) {//
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


    private static String getMediaDurationTime(long time) {//
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


    public static String formatHmsS(long ms){
        return formatHmsS(ms,false);
    }
    public static String formatHmsS(long ms,boolean isVideoClip) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day>0)sb.append(day + ":");
        if (hour > 0) {
            sb.append(hour >= 10 ? (hour + ":") : ("0" + hour + ":"));
        } else {
            sb.append(0 + "0:");
        }
        if (minute > 0) {
            sb.append(minute >= 10 ? (minute + ":") : ("0" + minute + ":"));
        } else {
            sb.append(0 + "0:");
        }
        String lastTag=isVideoClip?".":":";
        if (second > 0) {
            sb.append(second >= 10 ? (second +lastTag) : ("0" + second +lastTag));
        } else {
            sb.append(0 + "0"+lastTag);
        }
        sb.append(milliSecond);
        return sb.toString();
    }












    public static Bitmap createThumbnailAtTime(String filePath, int timeInSeconds){
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        return mMMR.getFrameAtTime(timeInSeconds*1000000, MediaMetadataRetriever.OPTION_CLOSEST);
    }





    public static Bitmap getVideoThumbnailMiniKind(String videoPath) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
        return bitmap;
    }

    public static Bitmap getVideoThumbnailMicroKind(String videoPath){
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
        return bitmap;
    }

    public static Bitmap getVideoThumbnailFullScreenKind(String videoPath){
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        return bitmap;
    }

    /**
     * 不支持m3u8 格式
     * @param videoPath
     * @return
     */
    public static int[] getVideoSpec(String videoPath){
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        if (videoPath.startsWith("http")){
            retr.setDataSource(videoPath,new HashMap<String, String>());
        }else{
            retr.setDataSource(videoPath);
        }
        String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
        String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方
        int[] infos=new int[3];
        infos[0]=Integer.parseInt(width);
        infos[1]=Integer.parseInt(height);
        infos[2]=Integer.parseInt(rotation);
        if (infos[2]==90){//画面经过旋转的视频
            int temp=infos[1];
            infos[1]=infos[0];
            infos[0]=temp;
        }
        retr.release();
        return infos;
    }




}
