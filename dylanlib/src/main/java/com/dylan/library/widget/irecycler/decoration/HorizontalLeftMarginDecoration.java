package com.dylan.library.widget.irecycler.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Author: Dylan
 * Date: 2020/3/28
 * Desc:
 */
public class HorizontalLeftMarginDecoration extends RecyclerView.ItemDecoration {

    private int firstItemLeftMargin;
    public HorizontalLeftMarginDecoration(Context context, int leftMargin){
        firstItemLeftMargin= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,leftMargin,context.getResources().getDisplayMetrics());
    }
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position=parent.getChildAdapterPosition(view);
        if (position==0){
            outRect.set(firstItemLeftMargin,0,0,0);
        }else{
            super.getItemOffsets(outRect, view, parent, state);
        }


    }
}
