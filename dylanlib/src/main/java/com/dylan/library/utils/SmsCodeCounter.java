package com.dylan.library.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Dylan on 2018/4/27.
 * 验证码倒计时工具类
 */

public class SmsCodeCounter {

    private TextView tvValidateCode;
    private Context mActivity;
    private int count;
    private int mDuration = 60;
    private String reGetCodeText = "重新获取";
    private String CountDownNumberText="重新获取";
    private Drawable normalDrawable;
    private Drawable countDownDrawable;
    private int normalTextColor=Color.WHITE;
    private int countDownTextColor= Color.WHITE;
    private static Map<String, PageExitRecord> recordExitMap;
    private OnCountDownListener mOnCountDownListener;
    public SmsCodeCounter(Activity activity, TextView tvValidateCode) {
        mActivity = activity;
        this.tvValidateCode = tvValidateCode;
    }

    public void setNormalTextColor(int color) {
        normalTextColor = color;
    }

    public void setCountDownTextColor(int color) {
        countDownTextColor = color;
    }

    public void setNormalDrawable(Drawable drawable) {
        normalDrawable = drawable;
    }

    public void setCountDownDrawable(Drawable drawable) {
        countDownDrawable = drawable;
    }

    public void setReGetCodeText(String text){
       reGetCodeText=text;
    }

    public void setTextAfterCountDownNumber(String text){
        CountDownNumberText=text;
    }

    public void checkRecord() {
        if (recordExitMap == null) return;
        PageExitRecord record = recordExitMap.get(mActivity.getClass().getSimpleName());
        if (record == null || record.getExitMillsTime() == 0) return;

        long recordDuration = (System.currentTimeMillis() - record.getExitMillsTime()) + record.getSurplusMillisDuration();
        if (recordDuration < mDuration * 1000) {
            int startCount = (int) ((mDuration * 1000 - recordDuration) / 1000L);
            start(startCount);
        }
    }


    private Handler counterHandler = new Handler(Looper.getMainLooper()) {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            if (count == 0) {
                tvValidateCode.setEnabled(true);
                tvValidateCode.setText(reGetCodeText);
                tvValidateCode.setTextColor(normalTextColor);
               if (normalDrawable!=null)tvValidateCode.setBackground(normalDrawable);
                if (mOnCountDownListener!=null)mOnCountDownListener.onFinish();
            } else {
                String text = String.format("%1$ds", count);
                tvValidateCode.setText(CountDownNumberText+" "+text);
                count--;
                counterHandler.sendEmptyMessageDelayed(0, 1000L);
                if (mOnCountDownListener!=null)mOnCountDownListener.onTick(count);
            }
        }
    };


    public boolean start() {
        count = mDuration-1;
        return start(count);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean start(int count) {
        this.count = count;
        String text = String.format("%1$ds", count);
        tvValidateCode.setText(CountDownNumberText+" "+text);
        tvValidateCode.setTextColor(countDownTextColor);
        if (countDownDrawable!=null)tvValidateCode.setBackground(countDownDrawable);
        tvValidateCode.setEnabled(false);
        counterHandler.sendEmptyMessageDelayed(0, 1000L);
        if (mOnCountDownListener!=null)mOnCountDownListener.onStart(count);
        return true;
    }



    public void destroyAndRecordExitTime(boolean needRecord) {
        counterHandler.removeCallbacksAndMessages(null);
        if (!needRecord||getCount() == 0){
            mActivity=null;
            return;
        }
        PageExitRecord record = new PageExitRecord();
        record.setExitMillsTime(System.currentTimeMillis());
        record.setSurplusMillisDuration(getSurplusMillsDuration());
        if (recordExitMap == null) {
            recordExitMap = new HashMap<>();
        }
        recordExitMap.put(mActivity.getClass().getSimpleName(), record);
        mActivity=null;
    }


    public int getCount() {
        return count;
    }

    public long getSurplusMillsDuration() {
        return (mDuration - getCount()) * 1000L;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
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


    public interface OnCountDownListener{
        void onStart(int duration);
        void onTick(int duration);
        void onFinish();
    }

    public void setOnCountDownListener(OnCountDownListener listener){
        mOnCountDownListener=listener;
    }

}
