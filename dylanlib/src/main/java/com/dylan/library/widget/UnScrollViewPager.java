package com.dylan.library.widget;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Dylan on 2017/4/22.
 */

public class UnScrollViewPager extends ViewPager {
    private boolean canScroll = true;

    public UnScrollViewPager(Context context) {
        super(context);
    }

    public UnScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (canScroll) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (canScroll){
            return super.onInterceptTouchEvent(ev);
        }else{
           return false;
        }
    }

    public void setCanScroll(boolean canScroll){
        this.canScroll=canScroll;
    }
}
