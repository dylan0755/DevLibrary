package com.dylan.library.widget.irecycler.decoration;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Author: Dylan
 * Date: 2020/3/23
 * Desc:
 */
public class IRecyclerViewGridDecoration extends RecyclerView.ItemDecoration {
    protected int mHorizontalSpace;
    protected int mVerticalSpace;

    private boolean mIncludeEdge ;
    private boolean hasHeaderView;

    private int mSpanCount=-1;

    public IRecyclerViewGridDecoration(Context context, int horizontalSpace, int verticalSpace, boolean hasHeaderView, boolean includeEdge) {
        mHorizontalSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                horizontalSpace, context.getResources().getDisplayMetrics());

        mVerticalSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                verticalSpace, context.getResources().getDisplayMetrics());

        this.mIncludeEdge = includeEdge;
        this.hasHeaderView=hasHeaderView;
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

        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();



        if (mSpanCount==-1){
            GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
            mSpanCount = gridLayoutManager.getSpanCount();
        }



        //列数
        int position =params.getSpanIndex();
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