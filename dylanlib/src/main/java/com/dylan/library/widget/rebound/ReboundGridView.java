package com.dylan.library.widget.rebound;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Author: Dylan
 * Date: 2020/2/18
 * Desc:
 */
public class ReboundGridView extends GridView {
    private ReboundHelper reboundHelper;
    public ReboundGridView(Context context) {
        this(context,null);
    }

    public ReboundGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        reboundHelper=new ReboundHelper(this);
    }

    //设置阻尼系数
    public void setDampingRatio(float ratio) {
        reboundHelper.setDampingRatio(ratio);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean bl=reboundHelper.onTouchEvent(ev);
        if (!bl){
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
