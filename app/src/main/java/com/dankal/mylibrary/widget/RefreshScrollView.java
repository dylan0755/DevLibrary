package com.dankal.mylibrary.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.dylan.library.utils.Logger;


/**
 * Author: Dylan
 * Date: 2018/9/4
 * Desc:
 */

public class RefreshScrollView extends ScrollView {
    private int mParentWidth;
    private int mParentHeight;
    private int mHeaderHeight;
    private int mFooterHeight;
    private final int INTERVEL_DURATION = 200;
    private boolean isDragRefresh = false;
    private boolean isDragLoad = false;
    private float downY;
    private View headerView;
    private View footerView;
    private RefreshScrollView mScrollView;
    private int distanceY;
    private boolean isRefreshing;
    private int touchSlop;





    public RefreshScrollView(Context context) {
        this(context, null);
    }

    public RefreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (isDragRefresh || isDragLoad) {
            return;
        }
        Logger.e("scrollY " + scrollY + " clampedY " + clampedY);
        if (scrollY == 0) {//顶部下拉
            isDragRefresh = true;
            isDragLoad=false;
            requestDisallowInterceptTouchEvent(true);
        } else if (scrollY != 0 && clampedY) {//顶部上拉
            isDragLoad = true;
            isDragRefresh = false;
            requestDisallowInterceptTouchEvent(true);
        } else {
            isDragRefresh = false;
            isDragLoad = false;
            requestDisallowInterceptTouchEvent(false);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Logger.e("ACTION_DOWN");
                downY = ev.getY();
                distanceY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                Logger.e("ACTION_MOVE");
                float moveY = ev.getY() - downY;
                Logger.e("getScrollY()=" + getScrollY());
                if (distanceY==0){
                    if (Math.abs(moveY)<touchSlop)return true;
                }
                if (isDragRefresh) {
                    if (distanceY != 0 && getScrollY() > 0 && getScrollY() >= mHeaderHeight) {
                        getParentLayout().scrollTo(0, mHeaderHeight);
                        return true;
                    }
                    distanceY += moveY;
                    ( (LinearLayout) getParent()).scrollBy(0, -(int) moveY);
                    if (getScrollY() < 0 && Math.abs(getScrollY()) > mHeaderHeight / 2) {
//                        if (refreshListener != null) {
//                            refreshListener.onToRefresh();
//                        }
                    } else {
//                        if (refreshListener != null) {
//                            refreshListener.onPullRefresh();
//                        }
                    }
                } else if (isDragLoad) {
                    Logger.e("isDragLoad "+isDragLoad);
                    if (distanceY != 0 && getScrollY() > 0 && getScrollY() <= mFooterHeight) {
                        getParentLayout().scrollTo(0, mHeaderHeight);
                        return true;
                    }
                    distanceY += moveY;
                    scrollBy(0, -(int) moveY);
                    if (getScrollY() < 0 && Math.abs(getScrollY()) > mFooterHeight / 2) {
//                        if (loadListener != null) {
//                            loadListener.onToLoad();
//                        }
                    } else {
//                        if (loadListener != null) {
//                            loadListener.onPullLoad();
//                        }
                    }
                }
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                Logger.e("ACTION_UP");
                distanceY = 0;
                Logger.e(getScrollY());
                if (isDragRefresh){
                    if (getScrollY() < 0 && getScrollY() < -mHeaderHeight / 2) {
                        smoothscrollTo(0);
//                        if (refreshListener != null) {
//                            isRefreshing = true;
//                            refreshListener.onRefreshing();
//                        }
                    } else {
                        smoothscrollTo(mHeaderHeight);
                    }
                }else if (isDragLoad){
                    if (getScrollY() < 0 && getScrollY() < -mFooterHeight / 2) {
                        smoothscrollTo(0);
//                        if ( loadListener!= null) {
//                            loadListener.onLoading();
//                        }
                    } else {
                        smoothscrollTo(mHeaderHeight);
                    }
                }
                isDragRefresh = false;
                isDragLoad = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void smoothscrollTo(int y) {
        ValueAnimator animator = ValueAnimator.ofFloat(getScrollY(), y);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                getParentLayout().scrollTo(0, (int) value);
            }
        });
        animator.setDuration(INTERVEL_DURATION);
        animator.start();
    }


    private LinearLayout getParentLayout(){
        return (LinearLayout) getParent();
    }



    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mPullListener!=null)mPullListener.onScrollChanged(l,t,oldl,oldt);
    }

    public interface OnPullLisetener {
        void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY);
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    private OnPullLisetener mPullListener;

    public void setOnPullLisetener(OnPullLisetener lisetener) {
        mPullListener = lisetener;
    }
}

