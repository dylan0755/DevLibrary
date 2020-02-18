package com.dylan.library.widget.rebound;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;


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
