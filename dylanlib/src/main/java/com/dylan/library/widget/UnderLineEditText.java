package com.dylan.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import com.dylan.library.R;

public class UnderLineEditText extends EditText {
    private Paint paint;
    private int strokeWidth;
    private int lineColor;

    public UnderLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.UnderLineEditText,0,0);
        strokeWidth=typedArray.getDimensionPixelOffset(R.styleable.UnderLineEditText_lineStrokeWith,1);
        lineColor=typedArray.getColor(R.styleable.UnderLineEditText_lineColor,Color.parseColor("#333333"));
        //设置画笔的属性
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        //设置画笔颜色为红色
        paint.setColor(lineColor);
        setBackground(null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(getPaddingLeft(), this.getHeight()-2, this.getWidth()-2-getPaddingRight(), this.getHeight()-2, paint);
    }
}

