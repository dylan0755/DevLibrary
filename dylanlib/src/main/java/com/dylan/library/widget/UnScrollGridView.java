package com.dylan.library.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Dylan on 2016/9/30.
 */
public class UnScrollGridView extends GridView{
    public UnScrollGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandHeight=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandHeight);
    }
}
