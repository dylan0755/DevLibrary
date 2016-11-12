package com.dylan.library.util;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Dylan on 2016/10/12.
 */
public class GridHorizontalSpace extends RecyclerView.ItemDecoration {

    private int mSpace;
    private int mSpanCount; // RecyclerView有多少列
    private boolean mHasPadding; // RecyclerView是否有Padding

    public GridHorizontalSpace(int mSpace) {
        this.mSpace = mSpace;
        this.mHasPadding = true;
    }

    public GridHorizontalSpace(int mSpace, boolean hasPadding) {
        this.mSpace = mSpace;
        this.mHasPadding = hasPadding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 初始化列数
        if (mSpanCount == 0) {
            this.mSpanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
        }
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % mSpanCount; // item column

        if (mHasPadding) {
            outRect.left = mSpace - column * mSpace / mSpanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * mSpace / mSpanCount; // (column + 1) * ((1f / spanCount) * spacing)
            if (position < mSpanCount) { // top edge
                outRect.top = mSpace;
            }
            outRect.bottom = mSpace; // item bottom
        } else {
            outRect.left = column * mSpace / mSpanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = mSpace - (column + 1) * mSpace / mSpanCount; // spacing - (column + 1) * ((1f /  spanCount) * spacing)
            if (position >= mSpanCount) {
                outRect.top = mSpace; // item top
            }
        }
    }

    public void setHasPadding(boolean hasPadding) {
        this.mHasPadding = hasPadding;
    }
}
