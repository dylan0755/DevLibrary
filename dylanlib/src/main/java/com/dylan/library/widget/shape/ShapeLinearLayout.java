package com.dylan.library.widget.shape;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Author: Administrator
 * Date: 2020/7/29
 * Desc:
 */
public class ShapeLinearLayout extends LinearLayout {
    private ShapeViewHelper shapeViewHelper;



    public ShapeLinearLayout(Context context) {
        super(context);

    }

    public ShapeLinearLayout(Context context,  AttributeSet attrs) {
        super(context, attrs);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }

    public ShapeLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }




}
