package com.dylan.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Dylan on 2017/3/22.
 */

public class ListItemWrapperLayout extends LinearLayout {

    public ListItemWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }
}
