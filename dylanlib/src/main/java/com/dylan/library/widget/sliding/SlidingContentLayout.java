package com.dylan.library.widget.sliding;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import com.dylan.library.widget.callback.AnimatorEndListener;

/**
 * Author: Dylan
 * Date: 2019/8/8
 * Desc:
 */
public class SlidingContentLayout extends LinearLayout {
    public boolean isOnTop = false;
    private int mTouchSlap;
    private float downY;
    private float deltaY;
    private boolean isMoving;
    private int offsetY;
    private OffsetChangedListener offsetChangedListener;

    public SlidingContentLayout(Context context) {
        super(context);
    }

    public SlidingContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mTouchSlap = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }




    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                deltaY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy=event.getY()-downY;
               if (!isOnTop&&Math.abs(dy)>mTouchSlap) return true;

        }

        return super.onInterceptTouchEvent(event);
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                deltaY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                //没有拉到最上面的时候只能网上偏移
                float dy = event.getY() - downY;
                deltaY += dy;
                if (!isOnTop) {
                    if (deltaY < 0) {
                        //当没有触动布局移动，那么要移动这么长距离才能触发滑动
                        if (!isMoving && Math.abs(deltaY) > 5 * mTouchSlap) {
                            deltaY = 0;
                            isMoving = true;
                        }
                        if (!isMoving) break;
                        if (deltaY < -offsetY) {
                            deltaY = -offsetY;//超过边界则等于边界距离
                            isOnTop = true;
                        }
                        setTranslationY(deltaY);
                        if (offsetChangedListener!=null)offsetChangedListener.offsetChange(offsetY,Math.abs(deltaY/offsetY));
                        break;
                    }

                }
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                isMoving = false;
                if (isOnTop) break;

                if (deltaY < 0) {
                    if (Math.abs(deltaY) <= offsetY * 0.35) {
                        slidedown();
                    } else {
                        slideup();
                    }
                }
                break;
        }


        return super.onTouchEvent(event);
    }

    public void slidedown() {
        ValueAnimator animator = ValueAnimator.ofFloat(getTranslationY(), 0);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float dy = (float) animation.getAnimatedValue();
                setTranslationY(dy);
                if (offsetChangedListener!=null)offsetChangedListener.offsetChange(offsetY,Math.abs(getTranslationY()/offsetY));
            }
        });
        animator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isOnTop = false;
            }
        });
        animator.start();

    }


    public void slideup() {
        ValueAnimator animator = ValueAnimator.ofFloat(getTranslationY(), -offsetY);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float dy = (float) animation.getAnimatedValue();
                setTranslationY(dy);
                if (offsetChangedListener!=null)offsetChangedListener.offsetChange(offsetY,Math.abs(getTranslationY()/offsetY));
            }
        });
        animator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isOnTop) {
                    isOnTop = true;
                }
            }
        });
        animator.start();
    }




    public void addOffsetChangedListener(OffsetChangedListener listener) {
        offsetChangedListener = listener;
    }
}
