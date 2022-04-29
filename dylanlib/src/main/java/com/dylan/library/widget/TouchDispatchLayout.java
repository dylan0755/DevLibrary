package com.dylan.library.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.dylan.library.utils.Logger;
import com.dylan.library.widget.shape.ShapeRelativeLayout;

/**
 * Author: Dylan
 * Date: 2021/04/02
 * Desc:
 */
public class TouchDispatchLayout extends ShapeRelativeLayout {
    private TouchDispatchHelper dispatchHelper;
    private boolean isLock;



    public TouchDispatchLayout(Context context) {
        this(context,null);

    }

    public boolean isLock() {
        return isLock;
    }

    public TouchDispatchLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        dispatchHelper=new TouchDispatchHelper(context,this);
        dispatchHelper.setDispatchTouchInterceptListener(new TouchDispatchHelper.OnDispatchTouchInterceptListener() {
            private int downX;
            private int downY;

            @Override
            public void onActionDown(MotionEvent ev) {
                downX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
            }

            @Override
            public void onActionDrag(MotionEvent event) {
                int rawX = (int) event.getRawX();
                int rawY = (int) event.getRawY();
                int offsetX = rawX - this.downX;
                int offsetY = rawY - this.downY;
                this.downX = rawX;
                this.downY = rawY;
                if (offsetX == 0 && offsetY == 0) {
                    return;
                }
                if (mDispatchCallBack!=null)mDispatchCallBack.onDrag(offsetX,offsetY);
            }


            @Override
            public void onActionUp(MotionEvent ev) {
            }

            @Override
            public void onActionZoom(float xRatio, float yRatio, PointF midPoint) {
                if (mDispatchCallBack!=null)mDispatchCallBack.onScale(xRatio,yRatio,midPoint);
            }


        });
    }


    public void setLock(boolean isLock){
        this.isLock=isLock;
        dispatchHelper.setLock(isLock);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mDispatchCallBack!=null)mDispatchCallBack.dispatchTouchEvent(ev);
        if (isLock){
            dispatchHelper.dispatchTouchEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    private DispatchCallBack mDispatchCallBack;
    public interface DispatchCallBack{
        void dispatchTouchEvent(MotionEvent ev);

        void onDrag(int offsetX, int offsetY);

        void onScale(float xRatio, float yRatio, PointF midPoint);
    }


    public void setDispatchCallBack(DispatchCallBack callBack){
        mDispatchCallBack=callBack;
    }






}
