package com.dylan.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.dylan.library.R;
import com.dylan.library.utils.Logger;

/**
 * Author: Dylan
 * Date: 2019/9/10
 * Desc:
 */
public class DashLineView extends View {
    private static final int defaultColor=Color.parseColor("#d8d8d8");
    private int dividerLineColor=defaultColor;
    private float dividerPaddingLeft;
    private float dividerPaddingRight;
    private float dashWith;
    private float dashGap;
    private Paint mPaint;
    private Path mPath;

    public DashLineView(Context context){
       super(context);
    }

    public DashLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
    }


    private void initAttr(Context context,AttributeSet attrs){
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.DashLineView);
        dividerLineColor=typedArray.getColor(R.styleable.DashLineView_dashLineColor, defaultColor);
        dividerPaddingLeft=typedArray.getDimension(R.styleable.DashLineView_android_paddingLeft, 0);
        dividerPaddingRight=typedArray.getDimension(R.styleable.DashLineView_android_paddingRight,0);
        dashWith=typedArray.getDimension(R.styleable.DashLineView_dashWith,24);
        dashGap=typedArray.getDimension(R.styleable.DashLineView_dashGap,10);
        typedArray.recycle();
        Logger.e("dashWith "+dashWith+" dashGap "+dashGap);
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(dividerLineColor);
        mPaint.setPathEffect(new DashPathEffect(new float[]{dashWith,dashGap},0));
        mPath=new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int lineWidth=getWidth();
        int lineHeight=getHeight();
        int centerY=lineHeight/2;
        mPaint.setStrokeWidth(lineHeight);
        mPath.moveTo(dividerPaddingLeft,centerY);
        mPath.lineTo(lineWidth-dividerPaddingRight,centerY);
        canvas.drawPath(mPath, mPaint);
    }
}
