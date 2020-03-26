package com.dylan.library.widget.irecycler;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;

/**
 * Author: Dylan
 * Date: 2020/3/23
 * Desc:
 */
public class IRecyclerViewStaggeredGridDivider extends RecyclerView.ItemDecoration {
    protected int mHorizontalSpace;
    protected int mVerticalSpace;
    private boolean mIncludeEdge = true;
    private boolean hasHeaderView=false;
    private int mSpanCount=-1;



    public IRecyclerViewStaggeredGridDivider(Context context, int horizontalSpace, int verticalSpace, boolean hasHeaderView, boolean includeEdge) {
        mHorizontalSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                horizontalSpace, context.getResources().getDisplayMetrics());

        mVerticalSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                verticalSpace, context.getResources().getDisplayMetrics());

        this.mIncludeEdge = includeEdge;
        this.hasHeaderView=hasHeaderView;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int adapterPosition= parent.getChildAdapterPosition(view);


        if (hasHeaderView){
            if (adapterPosition==1){
                return;
            }
            if (mIncludeEdge){
                if (adapterPosition==0)return;
            }
        }




        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();



        //列数
        if (mSpanCount==-1){
            StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            mSpanCount = gridLayoutManager.getSpanCount();
        }




        // 获取item在span中的下标
        //int spanIndex = params.getSpanIndex();
        //int position = parent.getChildLayoutPosition(view);
        //使用 原position 会导致item间隔的错乱  将position替换为span的下标判断item的位置，RecyclerView 滚动监听中还要  recyclerView.invalidateItemDecorations();
        int position=params.getSpanIndex();
        int column = (position) % mSpanCount;
        if (mIncludeEdge) {
            outRect.left = mHorizontalSpace - column * mHorizontalSpace / mSpanCount;
            outRect.right = (column + 1) * mHorizontalSpace / mSpanCount;
//            if (position < mSpanCount) {
//                outRect.top = mVerticalSpace;
//            }
            outRect.bottom = mVerticalSpace;
        } else {
            outRect.left = column * mHorizontalSpace / mSpanCount;
            outRect.right = mHorizontalSpace - (column + 1) * mHorizontalSpace / mSpanCount;
//            if (position >= mSpanCount) {
//                outRect.top = mVerticalSpace;
//            }
            outRect.bottom = mVerticalSpace;
        }
    }
}