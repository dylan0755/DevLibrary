package com.dylan.library.widget.shape;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dylan.library.widget.MediumTextView;

/**
 * Author: Dylan
 * Date: 2020/3/26
 * Desc:
 */
@SuppressLint("AppCompatCustomView")
public class ShapeMediumTextView extends MediumTextView {
    private ShapeViewHelper shapeViewHelper;


    public ShapeMediumTextView(Context context) {
        this(context, null);
    }

    public ShapeMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }

    public ShapeMediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }

}