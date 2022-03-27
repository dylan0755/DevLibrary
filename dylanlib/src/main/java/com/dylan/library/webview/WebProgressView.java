package com.dylan.library.webview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by admin on 2017/6/17
 */
public class WebProgressView extends View {

    private Paint paint;//进度条的画笔
    private int progress = 1;//进度默认为1
    private int height;
    public WebProgressView(Context context) {
        this(context, null);
    }

    public WebProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        height=getMeasuredHeight();
        paint.setStrokeWidth(height);
    }

    private void initPaint(Context context) {
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);//填充方式为描边
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);//使用抖动效果
        paint.setColor(Color.parseColor("#f15701"));//画笔设置颜色
    }

    //设置进度
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();//刷新画笔
    }

    public void setColor(int colorValue){
        paint.setColor(colorValue);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth() * progress / 100, height, paint);//画矩形从（0.0）开始到(progress,height)的区域
    }

}
