package com.dylan.library.widget.irecycler.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Author: Dylan
 * Date: 2020/3/23
 * Desc:
 */
public class IRecyclerViewGridDecoration2 extends RecyclerView.ItemDecoration {
    protected int mHorizontalSpace;
    protected int mVerticalSpace;
    protected int mEdgeMargin;

    private int mSpanCount = -1;

    public IRecyclerViewGridDecoration2(Context context, int edgeMargin, int horizontalSpace, int verticalSpace) {
        mEdgeMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                edgeMargin, context.getResources().getDisplayMetrics());

        mHorizontalSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                horizontalSpace, context.getResources().getDisplayMetrics());

        mVerticalSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                verticalSpace, context.getResources().getDisplayMetrics());

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int adapterPosition = parent.getChildAdapterPosition(view);

        if (adapterPosition == 0 || adapterPosition == 1) {
            return;
        }


        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();


        if (mSpanCount == -1) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
            mSpanCount = gridLayoutManager.getSpanCount();
        }


        //列数
        int position = params.getSpanIndex();
        int column = (position) % mSpanCount;
        if (mEdgeMargin > 0) {
            if (column == 0) {//最左边
                outRect.left = mEdgeMargin;
                outRect.right = (column + 1) * mHorizontalSpace / mSpanCount;
            } else if (column == mSpanCount - 1) {//最右边
                outRect.left = mHorizontalSpace - column * mHorizontalSpace / mSpanCount;
                outRect.right = mEdgeMargin;
            } else {
                outRect.left = mHorizontalSpace - column * mHorizontalSpace / mSpanCount;
                outRect.right = (column + 1) * mHorizontalSpace / mSpanCount;
            }
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