package com.dylan.library.widget.shape;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


/**
 * Author: Administrator
 * Date: 2020/7/29
 * Desc:
 */
public class ShapeRelativeLayout extends RelativeLayout {

    private ShapeViewHelper shapeViewHelper;



    public ShapeRelativeLayout(Context context) {
        super(context);

    }

    public ShapeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }

    public ShapeRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }




}
