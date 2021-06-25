package com.dylan.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.dylan.library.R;


/**
 * Author: Dylan
 * Date: 2019/9/10
 * Desc:
 */
public class VerticalDashLineView extends View {
    private static final int defaultColor= Color.parseColor("#d8d8d8");
    private Paint mDashPaint;
    private Rect mRect;
    public VerticalDashLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.DashLineView);
        int dashLineColor =typedArray.getColor(R.styleable.DashLineView_dashLineColor, defaultColor);
        float dashGap =typedArray.getDimension(R.styleable.DashLineView_dashGap,10);
        float dashWidth =typedArray.getDimension(R.styleable.DashLineView_dashHeight,24);
        typedArray.recycle();
        mDashPaint = new Paint();
        mDashPaint.setColor(dashLineColor);
        //DashPathEffect是Android提供的虚线样式API，具体的使用可以参考下面的介绍
        mDashPaint.setPathEffect(new DashPathEffect(new float[] { dashWidth, dashGap }, 0));
        mRect = new Rect();

    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //取出线条的位置（位置的定义放在XML的layout中，具体如下xml文件所示）
        mRect.left = left;
        mRect.top = top;
        mRect.right = right;
        mRect.bottom = bottom;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setStrokeWidth(getMeasuredWidth());
        mDashPaint.setAntiAlias(true);


        float x0 = (mRect.right - mRect.left) / 2f;
        float y0 = 0;
        float x1 = x0;
        float y1 = y0 + mRect.bottom - mRect.top;
        canvas.drawLine(x0, y0, x1, y1, mDashPaint);
    }
}
