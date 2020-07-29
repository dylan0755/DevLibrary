package com.dylan.library.widget.shape;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Author: Administrator
 * Date: 2020/7/29
 * Desc:
 */
public class ShapeFrameLayout extends FrameLayout {
    private ShapeViewHelper shapeViewHelper;



    public ShapeFrameLayout(Context context) {
        super(context);

    }

    public ShapeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }

    public ShapeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }





}
