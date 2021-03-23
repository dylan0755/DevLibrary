package com.dylan.library.widget.shape;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.widget.EditText;


/**
 * Author: Administrator
 * Date: 2020/7/29
 * Desc:
 */
@SuppressLint("AppCompatCustomView")
public class ShapeLetterNumberEditText extends EditText {
    private ShapeViewHelper shapeViewHelper;


    public ShapeLetterNumberEditText(Context context) {
        this(context, null);
    }

    public ShapeLetterNumberEditText(Context context, AttributeSet attrs) {
        super (context,attrs);
        setDigits();
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
    }

    public ShapeLetterNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDigits();
        shapeViewHelper=new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);


    }


    private void setDigits(){
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }


}
