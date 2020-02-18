package com.dylan.library.widget.rebound;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Scroller;


/**
 * Author: Dylan
 * Date: 2020/2/18
 * Desc: 拖拽回弹ListView
 */
public class ReboundListView extends ListView {
     private ReboundHelper reboundHelper;

    public ReboundListView(Context context) {
        this(context,null);
    }

    public ReboundListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        reboundHelper=new ReboundHelper(this);
    }

    //设置阻尼系数
    public void setDampingRatio(float ratio) {
        reboundHelper.setDampingRatio(ratio);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean bl=reboundHelper.onTouchEvent(event);
        if (!bl){
            return super.onTouchEvent(event);
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
