package com.dylan.mylibrary.ui.onlinepic;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.dylan.library.utils.Logger;
import com.dylan.mylibrary.ui.onlinepic.glide.progress.ProgressImageLayout;

/**
 * Author: Dylan
 * Date: 2019/7/24
 * Desc:
 */

public class PreViewPager extends ViewPager {
    private float startX;
    private float startY;
    private float downX;
    private float downY;
    private float deltaX;
    private float deltaY;
    private int mScreenWidth;
    private int mScreenHeight;
    private int finishDeltaY;
    private DataAdapter mDataAdapter;
    private boolean isDragging;
    private int mTouchSlop;//最小滑动距离
    private onFinishActivityListener mListener;
    private int[] sourceViewLocation;  //点击Item 打开图片预览前item的屏幕坐标
    private ProgressImageLayout mImageLayout;
    private PhotoView mPhotoView;


    public PreViewPager(@NonNull Context context) {
        super(context);
    }

    public PreViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        finishDeltaY = mScreenHeight / 4;
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        super.setAdapter(adapter);
        getCurrentView();
    }

    public void setSourceViewLocation(int[] location) {
        sourceViewLocation = location;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getCurrentView();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDragging && ev.getY() <= downY) {//往上方向拖动 左上，正上，右上
                    ev.setLocation(ev.getX(), downY);
                } else {
                    if (!isDragging && ev.getY() >= downY + 5 * mTouchSlop) {//斜向下触发拖动事件
                        //图片预览处在缩放拖拉阶段不能触发下拉返回
                        if (mPhotoView != null && mPhotoView.isOnDragMode()) {
                            //Logger.e("当前在Drag模式");
                            break;
                        }
                        isDragging = true;
                    }
                    if (isDragging) {
                        float offsetX = ev.getX() - startX;
                        float offsetY = ev.getY() - startY;
                        deltaX += offsetX;
                        deltaY += offsetY;
                        int alpha = 255;
                        if (deltaY >= 0) {//先往下拖最后往上托
                            alpha = 255 - (int) ((deltaY / mScreenHeight * 255) * 1.5);
                        }
                        if (alpha < 0) alpha = 0;
                        if (mImageLayout != null) {
                            mPhotoView.setPivotX(0);
                            mPhotoView.setPivotY(0);
                            setContentViewAlpha(alpha);
                            mPhotoView.setTranslationX(deltaX);
                            mPhotoView.setTranslationY(deltaY);
                            if (deltaY > 0) {
                                float scale = 1 - deltaY / finishDeltaY * 0.2f;
                                mPhotoView.setScaleX(scale);
                                mPhotoView.setScaleY(scale);
                            } else {
                                mPhotoView.setScaleX(1.0f);
                                mPhotoView.setScaleY(1.0f);
                            }
                        }
                        startX = ev.getX();
                        startY = ev.getY();
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                isDragging = false;
                if (deltaY > finishDeltaY) {//下拉某个距离则退出Activity
                    startFinishActivityAnimation();
                } else {
                    if (mPhotoView != null) {
                        mPhotoView.setTranslationX(0);
                        mPhotoView.setTranslationY(0);
                        mPhotoView.setScaleX(1.0f);
                        mPhotoView.setScaleY(1.0f);
                    }
                    setContentViewAlpha(255);
                }
                deltaX = 0;
                deltaY = 0;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    private void startFinishActivityAnimation() {
        if (getCurrentView() != null) {
            int sourceViewX = 0;
            int sourceViewY = 0;
            if (sourceViewLocation != null && sourceViewLocation.length > 0) {
                sourceViewX = sourceViewLocation[0];
                sourceViewY = sourceViewLocation[1];
                Logger.e("sourceViewY " + sourceViewY);
            }
            //最初的顶点的坐标
            final float[] location = new float[2];
            getOriginPoint(location);
            setContentViewAlpha(0);
            //当前图片的X,Y 的偏移量
            final float hasTranslationX = mPhotoView.getTranslationX();
            final float hasTranslationY = mPhotoView.getTranslationY();
            //当前移动后图片的左上角顶点坐标
            float picX = 0;
            float picY = 0;
            //获取图片的移动后的左上角坐标
            if (mPhotoView.getScaleX() != 1 || mPhotoView.getScaleY() != 1) {
                float[] values = new float[9];
                mPhotoView.getMatrix().getValues(values);
                picX = values[2];
                picY = values[5];
            } else {
                //没有缩放情况下，原点坐标+TranslationX,TranslationY 偏移的量
                picX = (location[0] + hasTranslationX);
                picY = (location[1] + hasTranslationY);
            }
            final float preScale = mPhotoView.getScaleX();
            final PointF startPoint = new PointF(picX, picY);
            final PointF endPoint = new PointF(sourceViewX, sourceViewY);
            final float yy = (endPoint.y - startPoint.y);
            ValueAnimator animator = ValueAnimator.ofFloat(0, yy);
            animator.setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float deltaY = (float) animation.getAnimatedValue();
                    //由偏移量计算出Y轴坐标,由Y轴坐标得到X轴坐标
                    float pointY = startPoint.y + deltaY;
                    float pointX = MathUtils.getInnerPointX(startPoint, endPoint, new PointF(0, pointY)).x;
                    float deltaX = pointX - startPoint.x;
                    if (mPhotoView.getScaleX() == 1 && mPhotoView.getScaleY() == 1) {
                        mPhotoView.setTranslationX(hasTranslationX + deltaX);
                        mPhotoView.setTranslationY(hasTranslationY + deltaY);
                    } else {
                        float ratio = deltaY / yy;
                        //整个View移动到目的位置，View里面的图片移动到顶点位置，两个操作同时进行
                        mPhotoView.setTranslationX(hasTranslationX + deltaX);
                        mPhotoView.setTranslationY(hasTranslationY + deltaY);
                        mPhotoView.scrollTo(0, (int) (ratio * location[1]));
                        //移动过程中缩放
                        float scale = preScale - ((preScale - 0.1f) * ratio);
                        mPhotoView.setScaleX(scale);
                        mPhotoView.setScaleY(scale);
                    }
                }
            });

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) { }
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {}
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mListener!=null)mListener.toFinishActivity();
                }
            });
            animator.start();

        }

    }

    //图片获取最初的左上角坐标(注意不是Viw)
    private void getOriginPoint(float[] location) {
        Matrix matrix = mPhotoView.getImageMatrix();
        if (matrix != null) {
            float[] originLocation = new float[9];
            matrix.getValues(originLocation);
            location[0] = originLocation[2];
            location[1] = originLocation[5];
        }
    }

    /**
     * 滑动翻页时重置
     */
    public void reset() {
        setContentViewAlpha(255);
        mImageLayout = null;
        mPhotoView = null;
    }

    private void setContentViewAlpha(int alpha) {
        if (getCurrentView() != null && getCurrentView().getBackground() != null) {
            getCurrentView().getBackground().setAlpha(alpha);
        }
    }


    public View getCurrentView() {
        if (mImageLayout == null) {
            if (getAdapter() != null && getAdapter() instanceof DataAdapter) {
                if (mDataAdapter == null) mDataAdapter = (DataAdapter) getAdapter();
                mImageLayout = mDataAdapter.getCurrentItemView();
                if (mImageLayout != null) {
                    mPhotoView = mImageLayout.getPhotoView();
                    //mPhotoView.setBackgroundColor(Color.BLACK);
                    return mImageLayout;
                }
            }

        }
        return mImageLayout;
    }

    public interface onFinishActivityListener {
        void toFinishActivity();
    }

    public void setOnFinishActivityListener(onFinishActivityListener listener) {
        mListener = listener;
    }


}
