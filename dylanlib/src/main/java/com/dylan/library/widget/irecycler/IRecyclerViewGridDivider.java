package com.dylan.library.widget.irecycler;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dylan.library.utils.Logger;

/**
 * Author: Dylan
 * Date: 2020/3/23
 * Desc:
 */
public class IRecyclerViewGridDivider extends RecyclerView.ItemDecoration {
    protected int mSpace = 10;

    private boolean mIncludeEdge = true;
    private boolean hasHeaderView=false;

    public IRecyclerViewGridDivider(int space) {
        this.mSpace = space;
    }

    public IRecyclerViewGridDivider(int space, boolean hasHeaderView, boolean includeEdge) {
        this.mSpace = space;
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

        Logger.e("adapterPosition "+adapterPosition);


        GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        //列数
        int spanCount = gridLayoutManager.getSpanCount();
        int position = parent.getChildLayoutPosition(view);
        int column = (position) % spanCount;
        if (mIncludeEdge) {
            outRect.left = mSpace - column * mSpace / spanCount;
            outRect.right = (column + 1) * mSpace / spanCount;
            if (position < spanCount) {
                outRect.top = mSpace;
            }
            outRect.bottom = mSpace;
        } else {
            outRect.left = column * mSpace / spanCount;
            outRect.right = mSpace - (column + 1) * mSpace / spanCount;
            if (position >= spanCount) {
                outRect.top = mSpace;
            }
        }
    }
}