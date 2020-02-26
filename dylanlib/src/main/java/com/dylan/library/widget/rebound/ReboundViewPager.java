package com.dylan.library.widget.rebound;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Author: Dylan
 * Date: 2020/2/26
 * Desc:
 */
public class ReboundViewPager extends ViewPager {
    private ReboundHelper reboundHelper;
    public ReboundViewPager(@NonNull Context context) {
        this(context,null);
    }

    public ReboundViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        reboundHelper=new ReboundHelper(this);
        reboundHelper.setOrient(ReboundHelper.HORIZONTAL);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result=reboundHelper.onTouchEvent(ev);
        if (!result){
            return super.onTouchEvent(ev);
        }else{
            return true;
        }
    }


    @Override
    public void computeScroll() {
        reboundHelper.computeScroll();
        super.computeScroll();
    }
}
