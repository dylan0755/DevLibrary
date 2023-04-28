package com.dylan.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatEditText;

import android.text.TextPaint;
import android.util.AttributeSet;

import com.dylan.library.R;

/**
 * Author: Dylan
 * Date: 2021/1/30
 * Desc:
 */

public class MediumEditText extends AppCompatEditText {
    private float mStrokeWidth=0.5f;//0.0f表示常规画笔的宽度，相当于默认情况。

    public MediumEditText(Context context) {
        this(context, null);
    }

    public MediumEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediumEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MediumTextView, defStyleAttr, 0);
        mStrokeWidth = array.getFloat(R.styleable.MediumTextView_textBold, mStrokeWidth);
        array.recycle();
    }


    public void setBold(float fraction){
        mStrokeWidth=fraction;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint textPaint=getPaint();
        textPaint.setStrokeWidth(mStrokeWidth);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        super.onDraw(canvas);
    }
}
