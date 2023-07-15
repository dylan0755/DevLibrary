package com.dylan.library.widget.wheel;

import android.view.GestureDetector;
import android.view.MotionEvent;


 class LoopViewGestureListener extends GestureDetector.SimpleOnGestureListener {
    final WheelView loopView;

    LoopViewGestureListener(WheelView loopview) {
        this.loopView = loopview;
    }

    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.loopView.scrollBy(velocityY);
        return true;
    }
}
