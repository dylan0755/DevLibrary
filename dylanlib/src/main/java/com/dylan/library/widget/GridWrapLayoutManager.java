package com.dylan.library.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/**
 * Created by Dylan on 2017/1/12.
 */

public class GridWrapLayoutManager extends GridLayoutManager {

    public GridWrapLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }



    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
