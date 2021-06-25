package com.dylan.library.widget.rebound;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Scroller;



/**
 * Author: Dylan
 * Date: 2020/2/17
 * Desc:
 */
public class ReboundLayout extends LinearLayout {
    private Scroller mScroller;
    private GestureDetector gestureDerector;
    private boolean isHorizontal=false;
    private boolean isDragging;
    public ReboundLayout(Context context) {
        super(context, null);

    }

    public ReboundLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);

        gestureDerector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
      {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                int disY = (int) ((distanceY-0.5)/2);
                int disX= (int) ((distanceX-0.5)/2);
                if (isVertical()){
                    beginScroll(0,disY);
                }else{
                    beginScroll(disX,0);
                }
                isDragging=true;
                return false;
            }
        });
    }

    private void beginScroll(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(),mScroller.getFinalY(),dx,dy);
        invalidate();
    }

    private void prepareScroll(int fx, int fy) {
        int dx=fx-mScroller.getFinalX();
        int dy=fy-mScroller.getFinalY();
        beginScroll(dx,dy);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
        case MotionEvent.ACTION_UP:
            isDragging=false;
            prepareScroll(0,0);
            break;
        default:
            return gestureDerector.onTouchEvent(event);
    }
        return isDragging;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset())
        {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }



    public void setScrollOrient(int orient){
        if (orient==1){
            isHorizontal=false;
        }else if (orient==0){
            isHorizontal=true;
        }
    }

    private boolean isVertical(){
        return !isHorizontal;
    }
    public void restore(){
        prepareScroll(0,0);
    }




}