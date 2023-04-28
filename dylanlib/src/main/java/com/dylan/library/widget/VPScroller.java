package com.dylan.library.widget;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Dylan on 2017/1/20.
 */

public class VPScroller {
    Field mScroller;
    private int duration=400;
    public VPScroller(int duration){
        try {
            this.duration=duration;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }


    public void attachViewPager(ViewPager viewPager){
        ChangeSpeedScroller scroller = new ChangeSpeedScroller(viewPager.getContext(), new AccelerateInterpolator());
        scroller.setDuration(duration);
        try {
            mScroller.set(viewPager, scroller);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }





    public class ChangeSpeedScroller extends Scroller {
        private int mDuration=250;

        public void setDuration(int duration) {
            mDuration = duration;
        }

        public ChangeSpeedScroller(Context context){
            super(context);
        }
        public ChangeSpeedScroller(Context context, Interpolator interpolator){
            super(context,interpolator);
        }
        public ChangeSpeedScroller(Context context, Interpolator interpolator,boolean flywheel){
            super(context,interpolator,flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy);
        }
    }
}
