package com.dylan.library.widget.rebound;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Author: Dylan
 * Date: 2020/2/17
 * Desc:  阻尼回弹 RecyclerView,RecyclerView 是利用父布局的内容移动，
 * 如果父布局内还有其他的控件元素，则其它元素也会跟着一起移动，所以
 * 如果只想要RecyclerView 单独有滑动效果，则单独用一个布局包裹ReboundRecyclerView
 */
public class ReboundRecyclerView extends RecyclerView {
    private ReboundHelper reboundHelper
            ;


    public ReboundRecyclerView(@NonNull Context context) {
        this(context,null);
    }

    public ReboundRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        reboundHelper =new ReboundHelper(this);
        //防止RecyclerView 中设置了点击事件导致 RecyclerView 自身接收不到ACTION_DOWN 的情况
        addOnItemTouchListener(new SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    RecyclerView.LayoutManager layoutManager = getLayoutManager();
                    if (layoutManager == null) {
                        Log.e(ReboundRecyclerView.this.getClass().getSimpleName(), "layoutManager is null");
                    } else {
                        //   GridLayoutManager 是 LinearLayoutManager 的子类,条件也成立
                        int orientation=ReboundHelper.VERTICAL;
                        if (layoutManager instanceof LinearLayoutManager) {
                            orientation= ((LinearLayoutManager) layoutManager).getOrientation();
                        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
                        }
                        reboundHelper.setOrient(orientation);
                    }
                    reboundHelper.onTouchEvent(e);
                }
                return false;
            }
        });

    }

    //设置阻尼系数
    public void setDampingRatio(float ratio) {
        reboundHelper.setDampingRatio(ratio);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean bl=reboundHelper.onTouchEvent(event);
        if (!bl) {
            return super.onTouchEvent(event);
        } else {
            return true;
        }
    }


    //滚动的逻辑   mScroller startScroll(xxxx)之后，下方的方法响应
    @Override
    public void computeScroll() {
        reboundHelper.computeScroll();
        super.computeScroll();

    }


}
