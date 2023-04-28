package com.dylan.library.widget;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
