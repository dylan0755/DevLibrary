package com.dylan.library.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Author: Dylan
 * Date: 2020/3/23
 * Desc:
 */
public class CompatCardView extends CardView {
    public CompatCardView(@NonNull Context context) {
        this(context,null);
    }

    public CompatCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setUseCompatPadding(true);
        setPreventCornerOverlap(false);
    }
}
