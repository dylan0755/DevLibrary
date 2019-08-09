package com.dylan.library.widget.pullrefresh;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.dylan.library.widget.callback.AnimatorEndListener;


/**
 * Created by Dylan on 2019/8/9.
 */
public abstract class BaseRefreshLayout extends FrameLayout {
    //支持下拉的高度
    protected float mMaxScrollHeight;

    //头部的高度
    protected float mHeadHeight;

    protected float mRefreshHeight;

    //子控件
    private View mChildView;

    //头部layout
    protected FrameLayout mHeadLayout;

    //刷新的状态
    protected boolean isRefreshing;

    //触摸获得Y的位置
    private float mTouchY;

    //当前Y的位置
    private float mCurrentY;

    //动画的变化率
    private DecelerateInterpolator decelerateInterpolator;

    private boolean hasMeasured;
    private boolean backWhileMaxScrollRange;
    protected float maxFraction;
    private float currentFraction;

    public BaseRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode())   return;

        if (getChildCount() > 1) {
            throw new RuntimeException("only can has one child!!!");
        }
        //在动画开始的地方快然后慢;
        decelerateInterpolator = new DecelerateInterpolator(10);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!hasMeasured) {
            post(new Runnable() {
                @Override
                public void run() {

                    mMaxScrollHeight=getMaxScrollHeight();
                    mHeadHeight=getHeaderHeight();
                    mRefreshHeight=getRefreshHeight();
                    View headView = getHeaderView();
                    //添加头部
                    FrameLayout headViewLayout = new FrameLayout(getContext());
                    LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
                    layoutParams.gravity = Gravity.TOP;
                    headViewLayout.setLayoutParams(layoutParams);

                    mHeadLayout = headViewLayout;
                    mHeadLayout.addView(headView);
                    addView(mHeadLayout);
                    maxFraction=mMaxScrollHeight*0.85f/mHeadHeight;

                }
            });
            hasMeasured = true;
        }
        //获得子控件
        mChildView = getChildAt(0);
    }




    /**
     * 拦截事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isRefreshing) return true;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mCurrentY = mTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                float dy = currentY - mTouchY;
                if (dy > 0 && !canChildScrollUp()) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 响应事件
     *
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isRefreshing||mChildView==null) {
            return super.onTouchEvent(e);
        }

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = e.getY();
                float dy = mCurrentY - mTouchY;
                dy = Math.min(mMaxScrollHeight * 2, dy);
                dy = Math.max(0, dy);

                float offsetY = decelerateInterpolator.getInterpolation(dy / mMaxScrollHeight / 2) * dy / 2;
                mChildView.setTranslationY(offsetY);
                mHeadLayout.getLayoutParams().height = (int) offsetY;
                mHeadLayout.requestLayout();
                float fraction = offsetY / mHeadHeight;
                currentFraction = fraction;
                if (pullListener != null) {
                    pullListener.onPulling(fraction);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mChildView.getTranslationY() >= mHeadHeight) {
                    if (backWhileMaxScrollRange &&currentFraction>=maxFraction){
                        setChildViewTransLationY(0,false,true);
                    }else{
                        setChildViewTransLationY(mRefreshHeight,false,false);
                    }
                } else {
                    setChildViewTransLationY(0,false,false);
                }
                currentFraction=0;
                return true;
        }
        return super.onTouchEvent(e);
    }



    private void setChildViewTransLationY(final float toY,final boolean isCompleteRefresh,final boolean isMaxBack) {

        final float fraction = mChildView.getTranslationY() / mHeadHeight;
        ValueAnimator animator = ValueAnimator.ofFloat(mChildView.getTranslationY(), toY);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentY = (float) animation.getAnimatedValue();
                mChildView.setTranslationY(currentY);
                mHeadLayout.getLayoutParams().height = (int) currentY;
                mHeadLayout.requestLayout();//重绘
                final float newFraction = mChildView.getTranslationY() / mHeadHeight;
                if (pullListener != null) pullListener.onFractionChanged(newFraction);
            }
        });
        animator.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (toY != 0) {
                    if (pullListener != null) pullListener.onRefresh(fraction);
                }else{
                    if (isMaxBack&&pullListener != null)pullListener.onMaxScrollBack();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (toY==0){
                    isRefreshing = false;
                }
                if (isCompleteRefresh){
                    if (pullListener!=null)pullListener.onRefreshCompleted();
                }

            }
        });
        isRefreshing = true;
        animator.start();
    }



   //用来判断是否可以上拉
    public boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                // return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
                return mChildView.canScrollVertically(-1)||mChildView.getScrollY() > 0;
            }
        } else {

            //return ViewCompat.canScrollVertically(mChildView, -1);
            return mChildView.canScrollVertically(-1);
        }
    }

    /**
     * 设置下拉监听
     */
    private PullToRefreshListener pullListener;

    public void setPullToRefreshListener(PullToRefreshListener listener) {
        this.pullListener = listener;
    }


    /**
     * 刷新结束
     */
    public void finishRefreshing() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mChildView != null) {
                    setChildViewTransLationY(0,true,false);
                }
                isRefreshing = false;
            }
        });

    }

    public void setSupportMaxBack(boolean bl) {
        backWhileMaxScrollRange = bl;
    }

    public boolean isSupportMaxBack(){
        return backWhileMaxScrollRange;
    }

    /**
     * 设置头部View
     */
    public abstract View getHeaderView();

    /**
     * 设置wave的下拉高度
     *
     */
    public abstract float getMaxScrollHeight();

    /**
     * 设置下拉头的高度
     */
    public abstract float getHeaderHeight();


    public abstract float getRefreshHeight();
}
