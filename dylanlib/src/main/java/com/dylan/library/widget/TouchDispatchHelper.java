package com.dylan.library.widget;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Author: Dylan
 * Date: 2021/04/02
 * Desc:
 */
public class TouchDispatchHelper {
    private float downX, downY;
    private int touchSlop;
    private boolean intercept;
    private boolean multiPointer;
    private boolean isZoom;
    private Handler mHandler;
    private float oldXDist;
    private float oldYDist;
    private PointF mMidPoint = new PointF();
    private OnDispatchTouchInterceptListener touchInterceptListener;
    private static float dampRatio=0.9f;
    private View parentView;
    private ViewGroup.LayoutParams layoutParams;
    private boolean isLock;

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public TouchDispatchHelper(Context context, View parentView){
        touchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
        mHandler=new Handler(Looper.getMainLooper());
        this.parentView=parentView;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                this.layoutParams=parentView.getLayoutParams();

                downX = ev.getRawX();
                downY = ev.getRawY();
                if (isLock){
                    intercept = true;
                }
                if (touchInterceptListener != null && !multiPointer)
                    touchInterceptListener.onActionDown(ev);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                multiPointer = true;
                mHandler.removeCallbacksAndMessages(null);
                oldXDist = spacingX(ev);
                oldYDist = spacingY(ev);
                if (oldXDist > 10.0f || oldYDist > 10.0f) {
                    isZoom = true;
                    midPoint(mMidPoint, ev);//记录缩放中心
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (ev.getPointerCount() > 2) return true;
                if (!intercept) {
                    if (isZoom) {
                        float newXDist = spacingX(ev);
                        float newYDist = spacingY(ev);
                        float scaleXRatio = newXDist / oldXDist;//放大的倍数
                        float scaleYRatio = newYDist / oldYDist;
 //                       Logger.e("scaleXRatio=" + scaleXRatio+" scaleYRatio="+scaleYRatio);

                        if (newXDist > 10f && scaleXRatio > 0&&newYDist>10f&&scaleYRatio>0) {
                            if (touchInterceptListener != null) {
                                touchInterceptListener.onActionZoom(scaleXRatio, scaleYRatio, mMidPoint);
                            }
                        }

                        if (newXDist > touchSlop || newYDist >touchSlop) {
                            if (newXDist > newYDist) {//横向
                                if (touchInterceptListener != null) {
                                    touchInterceptListener.onActionZoom(scaleXRatio, 1, mMidPoint);
                                }
                            } else {//纵向
                                if (touchInterceptListener != null) {
                                    touchInterceptListener.onActionZoom(1, scaleYRatio, mMidPoint);
                                }
                            }
                        }


                        oldXDist = newXDist;
                        oldYDist = newYDist;
                    } else {
                        float offsetX = ev.getRawX() - downX;
                        float offsetY = ev.getRawY() - downY;
                        if (Math.abs(offsetY) > touchSlop) {
                          //  Logger.e("滑动... ");
                            mHandler.removeCallbacksAndMessages(null);
                        }
                    }
                } else {
                    if (touchInterceptListener != null && !multiPointer)
                        touchInterceptListener.onActionDrag(ev);
                }

                break;
            case MotionEvent.ACTION_UP:
                //Logger.e("ACTION_UP");
                intercept = false;
                multiPointer = false;
                isZoom = false;
                mHandler.removeCallbacksAndMessages(null);
                if (touchInterceptListener != null) touchInterceptListener.onActionUp(ev);
                break;
        }

        if (intercept || multiPointer) return true;

        return false;

    }



    public interface OnDispatchTouchInterceptListener {
        void onActionDown(MotionEvent ev);

        void onActionDrag(MotionEvent ev);

        void onActionUp(MotionEvent ev);

        void onActionZoom(float xRatio, float yRatio, PointF midPoint);
    }

    public void setDispatchTouchInterceptListener(OnDispatchTouchInterceptListener listener) {
        touchInterceptListener = listener;
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
