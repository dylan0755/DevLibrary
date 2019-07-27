package com.dylan.mylibrary.ui.onlinepic;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
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

public class PreViewPager extends ViewPager implements OnTouchCallBack{
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
                            Logger.e("当前在Drag模式");
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
                        getPhotoView().setPivotX(0);
                        getPhotoView().setPivotY(0);
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
            if (mListener != null) mListener.hideIndicator();
            float sourceViewX;
            float sourceViewY;
            if (sourceViewLocations != null && sourceViewLocations.size() > 0) {
                sourceViewX = sourceViewLocations.get(getCurrentItem()).x;
                sourceViewY = sourceViewLocations.get(getCurrentItem()).y;
            } else {//没有传坐标进来的话就从屏幕底部消失
                sourceViewX = event.getX();
                sourceViewY = mScreenHeight + 100;
            }
            //最初的顶点的坐标
            final float[] location = getPhotoView().getOriginMatrixLocation();
            setContentViewAlpha(0);
            //当前图片的X,Y 的偏移量
            final float hasTranslationX = getPhotoView().getTranslationX();
            final float hasTranslationY = getPhotoView().getTranslationY();
            //当前移动后图片的左上角顶点坐标
            float picX;
            float picY;
            //获取图片的移动后的左上角坐标
            if (getPhotoView().getScaleX() != 1 || getPhotoView().getScaleY() != 1) {
                float[] values = new float[9];
                getPhotoView().getMatrix().getValues(values);
                picX = values[2];
                picY = values[5];
            } else {
                //没有缩放情况下，原点坐标+TranslationX,TranslationY 偏移的量
                picX = (location[0] + hasTranslationX);
                picY = (location[1] + hasTranslationY);
            }
            final float preScale = getPhotoView().getScaleX();
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
                    if (getPhotoView().getScaleX() == 1 && getPhotoView().getScaleY() == 1) {
                        getPhotoView().setTranslationX(hasTranslationX + deltaX);
                        getPhotoView().setTranslationY(hasTranslationY + deltaY);
                    } else {
                        float ratio = deltaY / yy;
                        //整个View移动到目的位置，View里面的图片移动到顶点位置，两个操作同时进行
                        getPhotoView().setTranslationX(hasTranslationX + deltaX);
                        getPhotoView().setTranslationY(hasTranslationY + deltaY);
                        getPhotoView().scrollTo(0, (int) (ratio * location[1]));
                        //移动过程中缩放
                        float scale = preScale - ((preScale - 0.2f) * ratio);
                        getPhotoView().setScaleX(scale);
                        getPhotoView().setScaleY(scale);
                    }
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
                    getPhotoView().setVisibility(GONE);
                    if (mListener != null) mListener.toFinishActivity();
                }
            });
            animator.start();

        }

    }


    public PhotoView getPhotoView() {
        if (getCurrentView() != null) {
            getCurrentView().getPhotoView().addOnTouchCallBack(this);
            return getCurrentView().getPhotoView();
        }
        return null;
    }

    private void setContentViewAlpha(int alpha) {
        if (getCurrentView() != null && getCurrentView().getBackground() != null) {
            getCurrentView().getBackground().setAlpha(alpha);
        }
    }


    public ProgressImageLayout getCurrentView() {
        if (mDataAdapter == null) {
            if (getAdapter() != null && getAdapter() instanceof DataAdapter) {
                mDataAdapter = (DataAdapter) getAdapter();
            }
        } else {
            return mDataAdapter.getCurrentItemView();
        }
        return null;
    }

    @Override
    public void singleActionUp() {
         if (mListener!=null)mListener.toFinishActivity();
    }

    public interface onFinishActivityListener {
        void toFinishActivity();

        void hideIndicator();
    }

    public void setOnFinishActivityListener(onFinishActivityListener listener) {
        mListener = listener;
    }


}
