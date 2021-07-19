package com.dylan.library.widget.shape;

import android.content.Context;
import android.util.AttributeSet;

import com.dylan.library.widget.DrawableCenterTextView;


/**
 * Author: Dylan
 * Date: 2021/07/19
 * Desc:
 */
public class ShapeDrawableCenterTextView extends DrawableCenterTextView {
    private ShapeViewHelper shapeViewHelper;

    public ShapeDrawableCenterTextView(Context context) {
        this(context,null,0);
    }

    public ShapeDrawableCenterTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShapeDrawableCenterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);

    }



}
