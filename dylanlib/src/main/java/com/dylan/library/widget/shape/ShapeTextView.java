package com.dylan.library.widget.shape;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Author: Dylan
 * Date: 2020/3/26
 * Desc:
 */
@SuppressLint("AppCompatCustomView")
public class ShapeTextView extends TextView {
    private ShapeViewHelper shapeViewHelper;


    public ShapeTextView(Context context) {
        this(context, null);
    }

    public ShapeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }

    public ShapeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }

}