package com.dylan.library.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Dylan on 2017/1/20.
 */

public class VPScroller {


    public VPScroller(View viewPager){
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ChangeSpeedScroller scroller = new ChangeSpeedScroller(viewPager.getContext(), new AccelerateInterpolator());
            scroller.setDuration(800);
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
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
