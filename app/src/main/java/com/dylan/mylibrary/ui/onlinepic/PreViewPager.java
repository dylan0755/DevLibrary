package com.dylan.mylibrary.ui.onlinepic;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.dylan.library.utils.Logger;
import com.dylan.mylibrary.ui.onlinepic.glide.progress.ProgressImageLayout;

import java.util.ArrayList;

/**
 * Author: Dylan
 * Date: 2019/7/24
 * Desc:
 */

public class PreViewPager extends ViewPager implements OnTouchCallBack {
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
    private ArrayList<ClickViewPoint.Point> sourceViewLocations;  //点击Item 打开图片预览前item的屏幕坐标


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


    public void setSourceViewLocation(ArrayList<ClickViewPoint.Point> list) {
        sourceViewLocations = list;
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
                    if (getPhotoView() == null) break;
                    if (!isDragging && ev.getY() >= downY + 5 * mTouchSlop) {//斜向下触发拖动事件
                        //图片预览处在缩放拖拉阶段不能触发下拉返回
                        if (getPhotoView() != null && getPhotoView().isOnDragMode()) {
                            // Logger.e("当前在Drag模式");
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
                            if (alpha < 0) alpha = 0;
                        }
                        getPhotoView().setPivotX(ev.getX());
                        getPhotoView().setPivotY(ev.getY());
                        setContentViewAlpha(alpha);
                        getPhotoView().setTranslationX(deltaX);
                        getPhotoView().setTranslationY(deltaY);
                        if (deltaY > 0) {
                            float scale = 1 - deltaY / finishDeltaY * 0.2f;
                            getPhotoView().setScaleX(scale);
                            getPhotoView().setScaleY(scale);
                        } else {
                            getPhotoView().setScaleX(1.0f);
                            getPhotoView().setScaleY(1.0f);
                        }
                    }
                    startX = ev.getX();
                    startY = ev.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                isDragging = false;
                if (deltaY > finishDeltaY) {//下拉某个距离则退出Activity
                    startFinishActivityAnimation(ev);
                } else {
                    if (getPhotoView() != null) {
                        getPhotoView().setTranslationX(0);
                        getPhotoView().setTranslationY(0);
                        getPhotoView().setScaleX(1.0f);
                        getPhotoView().setScaleY(1.0f);
                        setContentViewAlpha(255);
                    }
                }
                deltaX = 0;
                deltaY = 0;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    private void startFinishActivityAnimation(MotionEvent event) {
        if (getPhotoView() != null) {
            setContentViewAlpha(0);
            if (mListener != null) mListener.hideIndicator();
            //当前移动后图片的左上角顶点坐标
            float[] lastMoveLocation = getCurrentPicLeftTopPoint();

            //点击View传过来的位置
            float sourceViewX;
            float sourceViewY;

            //如果拖动图片超过了屏幕底部，又或者没有传坐标进来 则直接从底部消失了
            if ((sourceViewLocations == null || sourceViewLocations.isEmpty())||lastMoveLocation[1]>=mScreenHeight*0.8f){
                sourceViewX = (event != null ? event.getX() : mScreenWidth);
                sourceViewY = mScreenHeight ;
            }else{
                sourceViewX = sourceViewLocations.get(getCurrentItem()).x;
                sourceViewY = sourceViewLocations.get(getCurrentItem()).y;
            }






            /**
             * 更改缩放中心点Pivot，原本PivotY 是大于0的，现在改成0，
             * 那么整个PhotoView的位置会上移，但translationY是没有变的
             * 所以要把PhotoView 的位置移动到原来的位置上
             */
            getPhotoView().setPivotX(0);
            getPhotoView().setPivotY(0);
            float[] changePivotPoint = getCurrentPicLeftTopPoint();
            //位置改变的差值
            float changeWidth = lastMoveLocation[0] - changePivotPoint[0];
            float changeHight = lastMoveLocation[1] - changePivotPoint[1];
            final float hasTranslationX = getPhotoView().getTranslationX() + changeWidth;
            final float hasTranslationY = getPhotoView().getTranslationY() + changeHight;
            //移动到运来的位置
            getPhotoView().setTranslationX(hasTranslationX);
            getPhotoView().setTranslationY(hasTranslationY);


            //设置图片滚回到点击控件位置然后消失的 路径
            final float preScale = getPhotoView().getScaleX();
            final PointF startPoint;
            final float[] originLocation = getPhotoView().getOriginMatrixLocation();
            //拖动
            if (getPhotoView().getTranslationY()!=0||getPhotoView().getTranslationX()!=0) {
                startPoint = new PointF(lastMoveLocation[0], lastMoveLocation[1]);
            } else {//未Tanslation，只是单击退出
                startPoint = new PointF(0, 0);
            }
            final PointF endPoint = new PointF(sourceViewX, sourceViewY);
            final float deltaY = (endPoint.y - startPoint.y);
            ValueAnimator animator = ValueAnimator.ofFloat(0, deltaY);
            animator.setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float moveY = (float) animation.getAnimatedValue();
                    //由偏移量计算出Y轴坐标,由Y轴坐标得到X轴坐标
                    float pointY = startPoint.y + moveY;
                    float pointX = MathUtils.getInnerPointX(startPoint, endPoint, new PointF(0, pointY)).x;
                    float moveX = pointX - startPoint.x;
                    float ratio = moveY / deltaY;
                    //整个View移动到目的位置，View里面的图片移动到顶点位置，两个操作同时进行
                    if (hasTranslationY != 0 || hasTranslationX != 0) {
                        getPhotoView().setTranslationX(hasTranslationX + moveX);
                        getPhotoView().setTranslationY(hasTranslationY + moveY);
                        getPhotoView().scrollTo(0, (int) (ratio * originLocation[1]));
                    } else {
                        getPhotoView().setTranslationX(moveX);
                        getPhotoView().setTranslationY(moveY);
                        getPhotoView().scrollTo(0, (int) (ratio * originLocation[1]));
                    }
                    //缩放
                    float scale = preScale - ((preScale - 0.2f) * ratio);
                    getPhotoView().setScaleX(scale);
                    getPhotoView().setScaleY(scale);
                }
            });

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    getPhotoView().setVisibility(INVISIBLE);
                    if (mListener != null) mListener.toFinishActivity();
                }
            });
            animator.start();

        }

    }


    public float[] getCurrentPicLeftTopPoint() {
        float[] point = new float[2];
        if (getPhotoView().getScaleX() != 1 || getPhotoView().getScaleY() != 1) {
            float[] values = new float[9];
            getPhotoView().getMatrix().getValues(values);
            point[0] = values[2];
            point[1] = values[5];
        } else {
            //没有缩放情况下，原点坐标+TranslationX,TranslationY 偏移的量
            point[0] = (getPhotoView().getOriginMatrixLocation()[0] + getPhotoView().getTranslationX());
            point[1] = (getPhotoView().getOriginMatrixLocation()[1] + getPhotoView().getTranslationY());
        }
        return point;
    }

    public PhotoView getPhotoView() {
        if (getCurrentView() != null) {
            getCurrentView().getPhotoView().addOnTouchCallBack(this);
           // getCurrentView().getPhotoView().setBackgroundColor(Color.BLACK);
            return getCurrentView().getPhotoView();
        }
        return null;
    }

    private void setContentViewAlpha(int alpha) {
        if (getBackground() != null) {
            getBackground().setAlpha(alpha);
        }
    }


    public ProgressImageLayout getCurrentView() {
        if (mDataAdapter == null) {
            if (getAdapter() != null && getAdapter() instanceof DataAdapter) {
                mDataAdapter = (DataAdapter) getAdapter();
                return mDataAdapter.getCurrentItemView();
            }
        } else {
            return mDataAdapter.getCurrentItemView();
        }
        return null;
    }

    @Override
    public void singleActionUp() {
//        if (mListener != null) mListener.toFinishActivity();
        startFinishActivityAnimation(null);
    }

    public interface onFinishActivityListener {
        void toFinishActivity();

        void hideIndicator();
    }

    public void setOnFinishActivityListener(onFinishActivityListener listener) {
        mListener = listener;
    }


}
