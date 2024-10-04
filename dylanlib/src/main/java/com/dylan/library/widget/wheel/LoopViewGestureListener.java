package com.dylan.library.widget.wheel;

import android.view.GestureDetector;
import android.view.MotionEvent;


 class LoopViewGestureListener extends GestureDetector.SimpleOnGestureListener {
    final WheelView loopView;

    LoopViewGestureListener(WheelView loopview) {
        this.loopView = loopview;
    }

    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //回调velocityY有设备差异性
        float startY = e1.getY();
        float endY = e2.getY();
        long startTime = e1.getEventTime();
        long endTime = e2.getEventTime();
        float distanceX = endY - startY;
        long timeDiff = endTime - startTime;
        float velocity = distanceX / timeDiff;
        this.loopView.scrollBy(velocity*1000L);
        return true;
    }
}
