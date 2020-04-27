package com.dylan.library.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;


/**
 * 验证码倒计时工具类
 */

public class SmsCodeCounter {

    private TextView tvValidateCode;
    private Context context;
    private int count;
    private   int mDuration =60;
    private String reGetCodeText ="获取验证码";
    private Drawable normalDrawable;
    private Drawable countDownDrawable;
    private int normalTextColor;
    private int countDownTextColor;

    public SmsCodeCounter(Context context, TextView tvValidateCode) {
        this.context = context;
        this.tvValidateCode = tvValidateCode;
    }

    public void setReGetCodeText(String reGetCodeText){
        this.reGetCodeText=reGetCodeText;
    }

    public void setNormalTextColor(int color){
        normalTextColor=color;
    }

    public void setCountDownTextColor(int color){
        countDownTextColor=color;
    }

    public void setNormalDrawable(Drawable drawable){
        normalDrawable=drawable;
    }

    public void setCountDownDrawable(Drawable drawable){
        countDownDrawable=drawable;
    }





    private Handler counterHandler = new Handler(Looper.getMainLooper()) {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            if (count == 0) {
                tvValidateCode.setEnabled(true);
                tvValidateCode.setText(reGetCodeText);
                tvValidateCode.setTextColor(normalTextColor);
                tvValidateCode.setBackground(normalDrawable);
            } else {
                String text = String.format("%1$ds", count);
                tvValidateCode.setText(text);
                count--;
                counterHandler.sendEmptyMessageDelayed(0, 1000L);
            }
        }
    };


    public  boolean start(PageExitRecord record){
        if (record==null)return false;
        if (record.getExitMillsTime()==0)return false;
        long recordDuration= (System.currentTimeMillis()-record.getExitMillsTime())+record.getSurplusMillisDuration();
        if (recordDuration<mDuration*1000){
            int startCount=(int) ((mDuration*1000-recordDuration)/1000L);
            return start(startCount);
        }
        return false;
    }

    public boolean  start(){
        count = 59;
        return start(count);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean start(int count) {
        this.count=count;
        String text = String.format("%1$ds", count);
        tvValidateCode.setText(text);
        tvValidateCode.setTextColor(countDownTextColor);
        tvValidateCode.setBackground(countDownDrawable);
        tvValidateCode.setEnabled(false);
        counterHandler.sendEmptyMessageDelayed(0, 1000L);
        return true;
    }

    public void stop() {
        counterHandler.removeCallbacksAndMessages(null);
    }


    public int getCount(){
        return count;
    }

    public long getSurplusMillsDuration(){
        return (mDuration-getCount())*1000L;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration){
        this.mDuration = duration;
    }










    public static class PageExitRecord {
        private long exitMillsTime;
        private long surplusMillisDuration;//剩余时长

        public long getExitMillsTime() {
            return exitMillsTime;
        }

        public void setExitMillsTime(long exitMillsTime) {
            this.exitMillsTime = exitMillsTime;
        }

        public long getSurplusMillisDuration() {
            return surplusMillisDuration;
        }

        public void setSurplusMillisDuration(long surplusMillisDuration) {
            this.surplusMillisDuration = surplusMillisDuration;
        }
    }

}
