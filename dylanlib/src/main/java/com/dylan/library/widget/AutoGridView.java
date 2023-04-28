package com.dylan.library.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Dylan on 2016/9/30.
 */
public class AutoGridView extends GridView{
    private boolean hasMeasure;
    public AutoGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandHeight=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        hasMeasure=false;
        super.onMeasure(widthSpec, expandHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        hasMeasure=true;
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 是否测量过了，在Adapter中可以判断测量过后再执行相关代码，
     * 可以防止getView 多次调用带来的问题
     */
    public boolean hasMeasured(){
        return hasMeasure;
    }
}
