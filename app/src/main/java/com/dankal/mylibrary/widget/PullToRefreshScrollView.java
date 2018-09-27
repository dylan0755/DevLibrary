package com.dankal.mylibrary.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.dankal.mylibrary.R;
import com.dylan.library.utils.Logger;

/**
 * Author: Dylan
 * Date: 2018/9/4
 * Desc:
 */

public class PullToRefreshScrollView extends LinearLayout {
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
    public PullToRefreshScrollView(Context context) {
        this(context, null);
    }

    public PullToRefreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        touchSlop=ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mParentWidth = MeasureSpec.getSize(widthMeasureSpec);
        mParentHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mParentHeight + mHeaderHeight+mFooterHeight, MeasureSpec.EXACTLY));
        LinearLayout.LayoutParams lp=  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
        lp.weight=1;
        mScrollView.setLayoutParams(lp);
    }





    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }

        //xml 布局
        View layoutWrapperView = getChildAt(0);
        removeView(layoutWrapperView);

        //头布局
        headerView = View.inflate(getContext(), R.layout.header_scrollview_refresh, null);
        headerView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mHeaderHeight = headerView.getMeasuredHeight();
        addView(headerView);

        //ScrollView
        mScrollView = new RefreshScrollView(getContext());
        mScrollView.setId(R.id.scrollview);
        mScrollView.setFillViewport(true);
        // mScrollView 添加xml 布局
        mScrollView.addView(layoutWrapperView);
        addView(mScrollView);


        //底部局
        footerView = View.inflate(getContext(), R.layout.footer_scrollview_loadmore, null);
        footerView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mFooterHeight = footerView.getMeasuredHeight();
        addView(footerView);
        scrollBy(0, mHeaderHeight);


    }




    public interface OnPullRefreshListener {
        void onPullRefresh();//下拉刷新

        void onRefreshing();//正在刷新

        void onToRefresh();//释放刷新
    }

    private OnPullRefreshListener refreshListener;

    public void setOnPullRefreshListener(OnPullRefreshListener listener) {
        refreshListener = listener;

    }


    public interface OnPullLoadListener {
        void onPullLoad();//下拉刷新

        void onLoading();//正在刷新

        void onToLoad();//释放刷新
    }

    private OnPullLoadListener loadListener;

    public void setOnPullLoadListener(OnPullLoadListener listener) {
        loadListener = listener;
    }


    public View getRefreshView() {
        return headerView;
    }

    public ScrollView getScrollView() {
        return mScrollView;
    }

    public void setRefresh(boolean bl) {
        if (bl) {
            isRefreshing = true;
            if (refreshListener != null) refreshListener.onRefreshing();
        } else {
            isRefreshing = false;
            isDragLoad=false;
            if (refreshListener != null) refreshListener.onPullRefresh();
        }
    }

}
