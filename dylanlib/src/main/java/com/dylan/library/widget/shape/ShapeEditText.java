package com.dylan.library.widget.shape;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;



/**
 * Author: Administrator
 * Date: 2020/7/29
 * Desc:
 */
@SuppressLint("AppCompatCustomView")
public class ShapeEditText extends EditText {
    private ShapeViewHelper shapeViewHelper;


    public ShapeEditText(Context context) {
        this(context, null);
    }

    public ShapeEditText(Context context, AttributeSet attrs) {
        super (context,attrs);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }

    public ShapeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);


    }




}
