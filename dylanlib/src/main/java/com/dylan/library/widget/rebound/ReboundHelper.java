package com.dylan.library.widget.rebound;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Scroller;

import com.dylan.library.utils.ViewUtils;


/**
 * Author: Dylan
 * Date: 2020/2/18
 * Desc:
 */
public class ReboundHelper {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int currentOrient = 1;
    private boolean isScrollLeftOver;
    private boolean isScrollRightOver;
    private boolean isScrollTopOver;
    private boolean isScrollBottomOver;
    private boolean needChangeParentBack;
    private boolean isDragging;
    private boolean haveDragged;
    private float downX, downY;
    private float dampingRatio = 0.5f;//阻尼系数
    private Scroller mScroller;
    private GestureDetector gestureDetector;
    private ViewGroup mParent;
    private View scrollTarget;


    public ReboundHelper(View scrollTarget) {
        this.scrollTarget = scrollTarget;
        scrollTarget.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mScroller = new Scroller(scrollTarget.getContext());
        gestureDetector = new GestureDetector(scrollTarget.getContext(), new GestureListener());
    }

    public void setOrient(int orient) {
        currentOrient = orient;
    }

    //设置阻尼系数
    public void setDampingRatio(float ratio) {
        if (ratio > 0 && ratio <=1) {
            dampingRatio = ratio;
        }
    }

    public void setChangeParentBackground(boolean bl) {
        needChangeParentBack = bl;
    }


    public View getParentView() {
        if (mParent == null) {
            mParent = (ViewGroup) scrollTarget.getParent();
            if (needChangeParentBack) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (mParent != null && mParent.getBackground() == null) {
                        Drawable backGround = scrollTarget.getBackground();
                        if (backGround != null) {
                            mParent.setBackground(backGround);
                        }
                    }
                }
            }
        }
        return mParent;

    }

    public boolean isDragging() {
        return isDragging;
    }

    public boolean haveDragged() {
        return haveDragged;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downY = event.getRawY();
            haveDragged = false;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float deltX = downX - event.getRawX();
            float deltY = downY - event.getRawY();
            if (isDragging) {
                haveDragged = true;
                if (currentOrient == VERTICAL) {
                    deltY = deltY * dampingRatio;
                    beginScroll(0, (int) deltY);
                } else {
                    deltX = deltX * dampingRatio;
                    beginScroll((int) deltX, 0);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isDragging = false;
            isScrollLeftOver = false;
            isScrollRightOver = false;
            isScrollTopOver = false;
            isScrollBottomOver = false;
            prepareScroll(0, 0);
        }
        downX = event.getRawX();
        downY = event.getRawY();
        gestureDetector.onTouchEvent(event);


        if (isDragging) {
            return true;
        }


        if (scrollTarget instanceof AbsListView) {
            scrollTarget.setPressed(false);
            return haveDragged;
        } else {
            return false;
        }


    }

    private void prepareScroll(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        beginScroll(dx, dy);
    }

    private void beginScroll(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        scrollTarget.invalidate();
    }


    //滚动的逻辑   mScroller startScroll(xxxx)之后，下方的方法响应
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            int currY = mScroller.getCurrY();
            if (isDragging) {
                if (currentOrient == VERTICAL) {
                    if (isScrollTopOver) {
                        //开始向下拉currY 为负，如果下拉再往上拉  就得判断是否有内容可以向下滚动，
                        // 有则事件交由RecyclerView、ScrollView 去滚动显示
                        if (currY > 0) {
                            if (scrollTarget.canScrollVertically(1)) {
                                currY = 0;
                                isDragging = false;
                                isScrollTopOver = false;
                                isScrollBottomOver = false;
                            }
                        }
                    } else if (isScrollBottomOver) {
                        //向上拉  currY 为正，如果上拉再往下拉  就得判断是否有内容可以向上滚动，
                        // 有则事件交由RecyclerView 去滚动显示
                        if (currY < 0) {
                            if (scrollTarget.canScrollVertically(-1)) {
                                currY = 0;
                                isDragging = false;
                                isScrollTopOver = false;
                                isScrollBottomOver = false;
                            }
                        }

                    }
                } else {
                    if (isScrollLeftOver) {
                        //向右拉然后再向左拉，当前位置位于起始位置的左边，如果RecyclerView内容还有没有展示的，
                        // 那么事件交由RecyclerView去执行滚动显示，而不能消费
                        if (currX > 0) {
                            if (scrollTarget.canScrollHorizontally(1)) {
                                currX = 0;
                                isDragging = false;
                                isScrollLeftOver = false;
                                isScrollRightOver = false;
                            }
                        }
                    } else if (isScrollRightOver) {
                        //在右边尽头往左拉再往右拉在起始位置的右边，如果左边还有数据没显示，
                        // 如果不处理，这个RecyclerView就往右移动而不是RecyclerView自身内容向右滚动
                        if (currX < 0) {
                            if (scrollTarget.canScrollHorizontally(-1)) {
                                currX = 0;
                                isDragging = false;
                                isScrollLeftOver = false;
                                isScrollRightOver = false;
                            }
                        }

                    }
                }

            }
            getParentView().scrollTo(currX, currY);
            scrollTarget.postInvalidate();
        }

    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isDragging) {
                if (currentOrient == VERTICAL) {
                    if (distanceY < 0) {//向下滑动
                        if (isOnTop(scrollTarget)) {
                            isDragging = true;
                            isScrollTopOver = true;
                            isScrollBottomOver = false;
                        }
                    } else {
                        if (isOnBottom(scrollTarget)) {
                            isDragging = true;
                            isScrollBottomOver = true;
                            isScrollTopOver = false;
                        }
                    }
                } else {
                    if (distanceX < 0) {//向右滑动
                        if (isOnLeft(scrollTarget)) {
                            isDragging = true;
                            isScrollLeftOver = true;
                            isScrollRightOver = false;
                        }
                    } else {
                        if (isOnRight(scrollTarget)) {
                            isDragging = true;
                            isScrollRightOver = true;
                            isScrollLeftOver = false;
                        }
                    }
                }
            }
            return false;
        }
    }


    public static boolean isOnBottom(View scrollTarget) {
       return ViewUtils.isOnBottom(scrollTarget);
    }


    private static boolean isOnTop(View scrollTarget) {
       return ViewUtils.isOnTop(scrollTarget);
    }


    private static boolean isOnRight(View scrollTarget) {
        return ViewUtils.isOnRight(scrollTarget);
    }

    private static boolean isOnLeft(View scrollTarget) {
        return ViewUtils.isOnLeft(scrollTarget);
    }

}
