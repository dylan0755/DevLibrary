package com.dylan.library.widget.photoview;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * Author: Dylan
 * Date: 2020/2/26
 * Desc:
 */
public class FlingRunnable implements Runnable {
    private Scroller mScroller;
    private int mCurrentX, mCurrentY;
    private OnFlingCallBack callBack;
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private boolean isScrolling;



    public FlingRunnable(Context context) {
        mScroller = new Scroller(context);
        mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    public void cancelFling() {
        mScroller.abortAnimation();
        mScroller.forceFinished(true);
    }


    public void initVelocityTracker(){
        mVelocityTracker = VelocityTracker.obtain();
        cancelFling();
    }


    public void addMovement(MotionEvent event){
        mVelocityTracker.addMovement(event);
    }



    public void computeCurrentVelocity(){
        mVelocityTracker.computeCurrentVelocity(1000);
        final float vX = mVelocityTracker.getXVelocity();
        final float vY = mVelocityTracker.getYVelocity();
        if (Math.abs(vX) > mMinimumVelocity) {
            cancelFling();
            fling(-(int) vX, -(int) vY);
            callBack.getImageView().post(this);
        }
        recycleVelocityTracker();
    }


    public void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }



    public void fling(int velocityX, int velocityY) {
        ImageView photoView=callBack.getImageView();
        RectF rect = getMatrixRectF(photoView);
        //当前图片的左上角坐标
        int startX = Math.round(-rect.left);
        int startY = Math.round(-rect.top);

        //横向和纵向的滚动范围
        int minScrollRangeX, maxScrollRangeX;
        int minScrollRangeY, maScrollRangeY;
        int photoViewWidth = photoView.getMeasuredWidth();
        if (rect.width() > photoViewWidth) {
            minScrollRangeX = 0;
            maxScrollRangeX = Math.round(rect.width() - photoViewWidth);
        } else {
            minScrollRangeX = maxScrollRangeX = startX;
        }

        int photoViewHeight = photoView.getMeasuredHeight();
        if (photoViewHeight < rect.height()) {
            minScrollRangeY = 0;
            maScrollRangeY = Math.round(rect.height() - photoViewHeight);
        } else {
            minScrollRangeY = maScrollRangeY = startY;
        }

        mCurrentX = startX;
        mCurrentY = startY;

        if (startX != maxScrollRangeX || startY != maScrollRangeY) {
            mScroller.fling(startX, startY, velocityX, velocityY, minScrollRangeX, maxScrollRangeX, minScrollRangeY, maScrollRangeY);
        }

    }


    @Override
    public void run() {
        if (mScroller.isFinished()) {
            isScrolling=false;
            return;
        }


        if (mScroller.computeScrollOffset()) {
            final int newX = mScroller.getCurrX();
            final int newY = mScroller.getCurrY();
            float moveX = mCurrentX - newX;
            float moveY = mCurrentY - newY;
            mCurrentX = newX;
            mCurrentY = newY;
            if (callBack != null) callBack.onFiling(mCurrentX, mCurrentY, moveX, moveY);

            //每16ms调用一次
            if (callBack!=null)callBack.getImageView().postDelayed(this, 16);
            isScrolling=true;
        }else{
            isScrolling=false;
        }

    }

    public boolean isScrolling(){
        return isScrolling;
    }

    private RectF getMatrixRectF(ImageView imageView) {
        RectF rectF = new RectF();
        Drawable d = imageView.getDrawable();
        if (d != null) {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            imageView.getImageMatrix().mapRect(rectF);
        }
        return rectF;
    }


    public interface OnFlingCallBack {
        void onFiling(int currentX, int currentY, float deltaX, float deltaY);

        ImageView getImageView();
    }

    public void setOnFlingCallBack(OnFlingCallBack callBack) {
        this.callBack = callBack;
    }

}