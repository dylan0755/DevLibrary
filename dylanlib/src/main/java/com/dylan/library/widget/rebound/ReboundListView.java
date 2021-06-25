package com.dylan.library.widget.rebound;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;



/**
 * Author: Dylan
 * Date: 2020/2/18
 * Desc: 拖拽回弹ListView
 */
public class ReboundListView extends ListView {
    private ReboundHelper reboundHelper;


    public ReboundListView(Context context) {
        this(context, null);
    }

    public ReboundListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        reboundHelper = new ReboundHelper(this);


    }

    //设置阻尼系数
    public void setDampingRatio(float ratio) {
        reboundHelper.setDampingRatio(ratio);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return reboundHelper.isDragging();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean bl = reboundHelper.onTouchEvent(event);
        if (!bl) {
            return super.onTouchEvent(event);
        } else {
            return true;
        }
    }


    @Override
    public void computeScroll() {
        reboundHelper.computeScroll();
        super.computeScroll();

    }


}
