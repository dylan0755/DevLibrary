package com.dylan.library.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.LinearLayout;

import com.dylan.library.screen.ScaleUtils;
import com.dylan.library.screen.ScreenUtils;


/**
 * Created by Dylan on 2017/11/6.
 */

public class HorizontalScrollBackLayout extends LinearLayout {
    private float downX;
    private float downY;
    private float totalDistance;
    private int screenWidth;
    private boolean isOrigin = true;
    private OnActivitySmoothCallBack mCallBack;
    private ValueAnimator mAnimator;
    private VelocityTracker velocityTracker;
    private int mAlpa = 88;
    private float distanceY;
    private Window mWindow;
    private int FLING_MIN_DISTANCE_X;
    private int FLING_MIN_DISTANCE_Y;
    private int FLING_FINISHING_MIN_VELOCITY;


    public HorizontalScrollBackLayout(Context context) {
        this(context, null);
    }

    public HorizontalScrollBackLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        screenWidth = ScreenUtils.getScreenWidth(context);
        FLING_MIN_DISTANCE_X = 4 * ViewConfiguration.get(context).getScaledTouchSlop();
        ScaleUtils scale = new ScaleUtils(context);
        FLING_MIN_DISTANCE_Y = scale.toScaleSize(100);
        FLING_FINISHING_MIN_VELOCITY = scale.toScaleSize(500);
    }

    public void setStartAlpa(int alpa){
        mAlpa=alpa;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //进过测试  每次速度计放在down初始化，up销毁计算出来是最准的
                velocityTracker = VelocityTracker.obtain();
                if (mAnimator != null) mAnimator.cancel();
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                float moveX = event.getX();
                float distanceX = moveX - downX;
                distanceY = event.getY() - downY;

                Log.e("ACTION_MOVE ", "distanceX " + distanceX + " distanceY " + distanceY);
                if (isOrigin) {//在原位

                    if (distanceY>=-FLING_MIN_DISTANCE_Y&&distanceY<=FLING_MIN_DISTANCE_Y
                            && distanceX > FLING_MIN_DISTANCE_X) {
                        totalDistance += distanceX;
                        setTranslationX(totalDistance);
                        adjustWindowApla();
                        isOrigin = false;
                        return true;
                    }
                } else {
                    if (totalDistance <= 0) {
                        setTranslationX(0);
                        adjustWindowApla();
                        totalDistance = 0;
                        isOrigin = true;
                    } else {
                        totalDistance += distanceX;
                        setTranslationX(totalDistance);
                        adjustWindowApla();
                        isOrigin = false;
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isOrigin) {
                    recyleVelocityTracker();
                    break;
                } else {
                    velocityTracker.computeCurrentVelocity(1000);//计算速度,1秒内移动过多少像素
                    float xVelocity = velocityTracker.getXVelocity();
                    recyleVelocityTracker();
                    Log.e("ACTION_UP", "xVelocity " + xVelocity);
                    if (Math.abs(xVelocity) > FLING_FINISHING_MIN_VELOCITY) {
                        smoothToFinish(totalDistance);
                        return true;
                    } else {
                        if (totalDistance > screenWidth / 2) {//关闭
                            smoothToFinish(totalDistance);
                        } else if (totalDistance <= screenWidth / 2) {
                            smoothToComBack(totalDistance);
                        }

                    }
                    return true;
                }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private void recyleVelocityTracker() {
        velocityTracker.clear();//重置
        velocityTracker.recycle();//
        velocityTracker = null;
    }


    private void smoothToComBack(final float fromX) {
        if (mAnimator != null) {
            mAnimator.removeAllUpdateListeners();
            mAnimator = null;
        }
        mAnimator = ValueAnimator.ofFloat(fromX, 0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setTranslationX(value);
                totalDistance = value;
                if (value <= 0) {
                    setTranslationX(0);
                    adjustWindowApla();
                    totalDistance = 0;
                    isOrigin = true;
                }
            }
        });
        mAnimator.setDuration(250);
        mAnimator.start();
    }


    private void smoothToFinish(final float fromX) {
        if (mWindow != null) {//销毁前先设置透明，否则Activity关闭会有遮罩层闪过
            mWindow.getDecorView().getBackground().setAlpha(0);
        }

        if (mAnimator != null) {
            mAnimator.removeAllUpdateListeners();
            mAnimator = null;
        }
        mAnimator = ValueAnimator.ofFloat(fromX, screenWidth);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setTranslationX(value);
                if (value >= (screenWidth - screenWidth * 0.1f)) {
                    if (mCallBack != null) mCallBack.closeActivity();
                }
            }
        });
        mAnimator.setDuration(300);
        mAnimator.start();
    }


    public void adjustWindowApla() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                float raito = totalDistance / screenWidth;
                if (mWindow == null) {
                    if (mCallBack != null) {
                        mWindow = mCallBack.attchActivityWindow();
                        if (mWindow != null) {
                            mWindow.getDecorView().setBackgroundColor(Color.BLACK);
                            mWindow.getDecorView().getBackground().setAlpha(mAlpa);
                        }
                    }
                } else {
                    mWindow.getDecorView().getBackground().setAlpha(mAlpa - (int) (raito * mAlpa));
                }
            }
        } catch (Exception e) {

        }

    }


    public interface OnActivitySmoothCallBack {
        void closeActivity();

        Window attchActivityWindow();
    }


    public void setonActivitySmoothCallBack(OnActivitySmoothCallBack callBack) {
        mCallBack = callBack;
    }


}
