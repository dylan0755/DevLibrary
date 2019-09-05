package com.dylan.library.utils.helper;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/3/19
 * Desc:
 */

public class VerticalTextSwitcherHelper {
    private static final int DURATION = 1000;

    private TextSwitcher textSwitcher;
    private List<String> stringList;
    private int currentPosition;
    private AnimationSet InAnimationSet;
    private AnimationSet OutAnimationSet;

    private int delayTime = 4000;
    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            nextView();
            handler.postDelayed(task, delayTime);
        }
    };

    public VerticalTextSwitcherHelper(final TextSwitcher textSwitcher, List<String> stringList) {
        if (textSwitcher==null)return;
        this.textSwitcher = textSwitcher;
        this.stringList = stringList;
        this.textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(textSwitcher.getContext());
            }
        });
    }

    public void appendStringList(List<String> stringList, boolean isFirstIndex) {
        if (stringList == null || stringList.isEmpty()) return;
        if (isFirstIndex)
            this.stringList.addAll(0, stringList);
        else
            this.stringList.addAll(stringList);
    }

    public void reStart() {
        stop();
        handler.postDelayed(task, delayTime);
    }

    public void stop() {
        handler.removeCallbacks(task);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public VerticalTextSwitcherHelper setStringList(List<String> stringList) {
        this.stringList = stringList;
        return this;
    }

    /**
     * 毫秒
     * @param delayTimeMills
     */
    public void setSwitchDuration(int delayTimeMills) {
        this.delayTime = delayTimeMills;
    }

    public void toStartSwitch() {
        currentPosition = 0;
        if (stringList == null) {
            Log.w("" + this.getClass().getSimpleName(), "stringList is null");
            return;
        }
        if (textSwitcher == null) {
            Log.w("" + this.getClass().getSimpleName(), "textSwitcher is null");
            return;
        }
        textSwitcher.setText(stringList.get(0));
        createAnimation();
        textSwitcher.setInAnimation(InAnimationSet);
        textSwitcher.setOutAnimation(OutAnimationSet);
        reStart();
    }

    private void createAnimation() {
        AlphaAnimation alphaAnimation;
        TranslateAnimation translateAnimation;

        int h = textSwitcher.getHeight();
        if (h <= 0) {
            textSwitcher.measure(0, 0);
            h = textSwitcher.getMeasuredHeight();
        }

        InAnimationSet = new AnimationSet(true);
        OutAnimationSet = new AnimationSet(true);

        alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, h, Animation.ABSOLUTE, 0);
        InAnimationSet.addAnimation(alphaAnimation);
        InAnimationSet.addAnimation(translateAnimation);
        InAnimationSet.setDuration(DURATION);

        alphaAnimation = new AlphaAnimation(1, 0);
        translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0, Animation.ABSOLUTE, -h);
        OutAnimationSet.addAnimation(alphaAnimation);
        OutAnimationSet.addAnimation(translateAnimation);
        OutAnimationSet.setDuration(DURATION);
    }

    private void nextView() {
        currentPosition = ++currentPosition % stringList.size();
        textSwitcher.setText(stringList.get(currentPosition));
    }

}
