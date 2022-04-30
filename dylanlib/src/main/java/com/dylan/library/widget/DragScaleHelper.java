package com.dylan.library.widget;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.dylan.library.utils.Logger;
import com.dylan.library.utils.ViewTouchUtils;
import com.dylan.library.utils.ViewUtils;

/**
 * Author: Dylan
 * Date: 2021/04/02
 * Desc:
 */
public class DragScaleHelper {
    private float downX, downY;
    private int touchSlop;
    private boolean intercept;
    private boolean multiPointer;
    private boolean isZoom;
    private Handler mHandler;
    private float oldDist;
    private int minHeight=-1;


    private PointF mMidPoint = new PointF();
    private View targetView;
    private int targetViewParentWidth,targetViewParentHeight;
    //默认不允许移动和缩放
    private boolean allowDrag;
    private boolean allowScale;

    public void setAllowDragAndScale(boolean allowDragAndScale) {
        this.allowDrag = allowDragAndScale;
        this.allowScale = allowDragAndScale;
        if (!allowDragAndScale) {
            resetFlag();
        }
    }

    public void setAllowDrag(boolean allowDrag) {
        this.allowDrag = allowDrag;
        if (!allowDrag) {
            resetFlag();
        }
    }

    public void setAllowScale(boolean allowScale) {
        this.allowScale = allowScale;
        if (!allowScale) {
            resetFlag();
        }
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public DragScaleHelper(Context context, View targetView) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mHandler = new Handler(Looper.getMainLooper());
        this.targetView = targetView;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                targetViewParentWidth= ((View)targetView.getParent()).getMeasuredWidth();
                targetViewParentHeight= ((View)targetView.getParent()).getMeasuredHeight();
                downX = ev.getRawX();
                downY = ev.getRawY();
                if (allowDrag) {
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                targetViewParentWidth= ((View)targetView.getParent()).getMeasuredWidth();
                targetViewParentHeight= ((View)targetView.getParent()).getMeasuredHeight();
                multiPointer = true;
                mHandler.removeCallbacksAndMessages(null);
                oldDist = ViewTouchUtils.spacing(ev);
                if (oldDist > 10.0f) {
                    isZoom = true;
                    midPoint(mMidPoint, ev);//记录缩放中心
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (ev.getPointerCount() > 2) return true;

                if (isZoom) {
                    if (!allowScale) break;
                    //等比例
                    float newDist = ViewTouchUtils.spacing(ev);
                    double space = newDist - oldDist;
                    float scale = (float) (targetView.getScaleX() + space / targetView.getWidth());
                    if (scale * targetView.getWidth() >= targetViewParentWidth) break;
                    if (scale * targetView.getWidth() <=(minHeight==-1?targetView.getWidth()*0.3f:minHeight) ) break;
                    targetView.setScaleX(scale);
                    targetView.setScaleY(scale);
                    targetView.getParent().requestLayout();



                }else if (allowDrag){
                    float offsetX = ev.getRawX() - this.downX;
                    float offsetY = ev.getRawY() - this.downY;
                    this.downX = ev.getRawX();
                    this.downY = ev.getRawY();
                    if (offsetX == 0 && offsetY == 0) {
                        break;
                    }
                    float tranX=targetView.getTranslationX()+ offsetX;
                    float tranY=targetView.getTranslationY()+offsetY;
                    targetView.setTranslationX(tranX);
                    targetView.setTranslationY(tranY);
                }else{
                    float offsetX = ev.getRawX() - downX;
                    float offsetY = ev.getRawY() - downY;
                    if (Math.abs(offsetY) > touchSlop) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                resetFlag();
                break;
        }
        return intercept || multiPointer;
    }

    private void resetFlag() {
        intercept = false;
        multiPointer = false;
        isZoom = false;
        mHandler.removeCallbacksAndMessages(null);
    }


    public static float spacingX(MotionEvent event) {
        float x = 0.0F;

        try {
            x = event.getX(0) - event.getX(1);
        } catch (IllegalArgumentException var4) {
        }
        return Math.abs(x);
    }

    public static float spacingY(MotionEvent event) {
        float y = 0.0F;

        try {
            y = event.getY(0) - event.getY(1);
        } catch (IllegalArgumentException var4) {
        }
        return Math.abs(y);
    }

    public static void midPoint(PointF pointF, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        pointF.set(x / 2.0F, y / 2.0F);
    }



}
