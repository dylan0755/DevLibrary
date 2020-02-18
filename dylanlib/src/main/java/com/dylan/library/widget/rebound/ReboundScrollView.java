package com.dylan.library.widget.rebound;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Scroller;


/**
 * Author: Dylan
 * Date: 2020/2/18
 * Desc: 拖拽回弹ScrollView
 */
public class ReboundScrollView extends ScrollView {
   private ReboundHelper reboundHelper;

    public ReboundScrollView(Context context) {
        this(context, null);
    }

    public ReboundScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        reboundHelper=new ReboundHelper(this);
        reboundHelper.setChangeParentBackground(true);
    }

    //设置阻尼系数
    public void setDampingRatio(float ratio) {
        reboundHelper.setDampingRatio(ratio);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
       boolean bl= reboundHelper.onTouchEvent(event);
        if (!bl) {
            return super.onTouchEvent(event);
        } else {
            return true;
        }

    }



    //滚动的逻辑   mScroller startScroll(xxxx)之后，下方的方法响应
    @Override
    public void computeScroll() {
        reboundHelper.computeScroll();
        super.computeScroll();
    }





}
