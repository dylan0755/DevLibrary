package com.dylan.library.widget;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created by Dylan on 2017/1/12.
 */

public class LinearWrapLayoutManager extends LinearLayoutManager {
    public LinearWrapLayoutManager(Context context) {
        super(context);
    }

    public LinearWrapLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
