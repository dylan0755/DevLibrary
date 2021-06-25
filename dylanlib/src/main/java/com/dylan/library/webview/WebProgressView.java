package com.dylan.library.webview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.dylan.library.screen.ScaleUtils;

/**
 * Created by admin on 2017/6/17
 */
public class WebProgressView extends View {

    private Paint paint;//进度条的画笔
    private int progress = 1;//进度默认为1
    private  static int HEIGHT ;//进度条高度为5

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

    private void initPaint(Context context) {
        HEIGHT=new ScaleUtils(context).toScaleSize(3);
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);//填充方式为描边
        paint.setStrokeWidth(HEIGHT);//设置画笔的宽度
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

    public void setHeight(int px){
        HEIGHT=new ScaleUtils(getContext()).toScaleSize(px);
        paint.setStrokeWidth(HEIGHT);//设置画笔的宽度
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth() * progress / 100, HEIGHT, paint);//画矩形从（0.0）开始到(progress,height)的区域
    }

}
