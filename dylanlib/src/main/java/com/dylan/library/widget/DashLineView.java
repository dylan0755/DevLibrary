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
    private int dashLineColor =defaultColor;
    private float dashPaddingLeft;
    private float dashPaddingRight;
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
        dashLineColor =typedArray.getColor(R.styleable.DashLineView_dashLineColor, defaultColor);
        dashPaddingLeft =typedArray.getDimension(R.styleable.DashLineView_android_paddingLeft, 0);
        dashPaddingRight =typedArray.getDimension(R.styleable.DashLineView_android_paddingRight,0);
        dashWith=typedArray.getDimension(R.styleable.DashLineView_dashWith,24);
        dashGap=typedArray.getDimension(R.styleable.DashLineView_dashGap,10);
        typedArray.recycle();
<<<<<<< HEAD
        Logger.e("dashWith "+dashWith+" dashGap "+dashGap);
=======
>>>>>>> 312398ff8298740c7633828dd6eadfabc4adab75
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(dashLineColor);
        mPaint.setPathEffect(new DashPathEffect(new float[]{dashWith,dashGap},0));
        mPath=new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int lineWidth=getWidth();
        int lineHeight=getHeight();
        int centerY=lineHeight/2;
        mPaint.setStrokeWidth(lineHeight);
        mPath.moveTo(dashPaddingLeft,centerY);
        mPath.lineTo(lineWidth- dashPaddingRight,centerY);
        canvas.drawPath(mPath, mPaint);
    }


    public int getDashLineColor() {
        return dashLineColor;
    }

    public void setDashLineColor(int dashLineColor) {
        this.dashLineColor = dashLineColor;
        mPaint.setColor(dashLineColor);
        invalidate();
    }

    public float getDashPaddingLeft() {
        return dashPaddingLeft;
    }

    public void setDashPaddingLeft(float dashPaddingLeft) {
        this.dashPaddingLeft = dashPaddingLeft;
        invalidate();
    }

    public float getDashPaddingRight() {
        return dashPaddingRight;
    }

    public void setDashPaddingRight(float dashPaddingRight) {
        this.dashPaddingRight = dashPaddingRight;
        invalidate();
    }

    public float getDashWith() {
        return dashWith;
    }

    public void setDashWith(float dashWith) {
        this.dashWith = dashWith;
        DashPathEffect effect= new DashPathEffect(new float[]{dashWith,dashGap},0);
        mPaint.setPathEffect(effect);
        invalidate();
    }

    public float getDashGap() {
        return dashGap;
    }

    public void setDashGap(float dashGap) {
        this.dashGap = dashGap;
        DashPathEffect effect= new DashPathEffect(new float[]{dashWith,dashGap},0);
        mPaint.setPathEffect(effect);
        invalidate();
    }
}
