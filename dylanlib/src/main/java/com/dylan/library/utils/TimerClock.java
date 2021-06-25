package com.dylan.library.utils;

import android.os.CountDownTimer;

import com.dylan.library.media.MediaTools;

/**
 * Author: Dylan
 * Date: 2021/04/10
 * Desc:
 */
public class TimerClock {
    private CountDownTimer mCountDownTimer;
    private long millisInFuture =Integer.MAX_VALUE;
    private long countDownDuration;
    private boolean isFirstInit=true;
    private CountDownTimerCallBack mCountDownTimerCallBack;

    public TimerClock() {
        initCountDown();
    }

    private void initCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        isFirstInit=true;
        mCountDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isFirstInit){
                    isFirstInit=false;
                    return;
                }
                countDownDuration += 1000L;
                String countDownText = MediaTools.getDurationHourFormat(countDownDuration);
                if (mCountDownTimerCallBack!=null)mCountDownTimerCallBack.onTick(countDownDuration,countDownText);
            }

            @Override
            public void onFinish() {

            }
        };
    }


    public void start(CountDownTimerCallBack callBack) {
        mCountDownTimerCallBack=callBack;
        mCountDownTimer.start();
    }


    public void restart(){
        initCountDown();
        mCountDownTimer.start();
    }


    public void pause() {
        mCountDownTimer.cancel();
    }

    public void stop() {
        mCountDownTimer.cancel();
    }




    public interface CountDownTimerCallBack{
        void onTick(long countDownDuration, String countDownDurationText);
    }


}
