package com.dylan.mylibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author: Dylan
 * Date: 2019/8/15
 * Desc:
 */
public class BezierView extends View {
    private int eventX,eventY;
    private int startX,startY;
    private int endX,endY;
    private Paint paint;

    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    //测量大小完成以后回调
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //起点
        startX = 0;
        startY = 0;
        //终点
        endX = w;
        endY = 0;
        //控制点
        eventX = w/2;
        eventY = h/4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.GRAY);
        //画3个点
        canvas.drawCircle(startX,startY,8,paint);
        canvas.drawCircle(endX,endY,8,paint);
        canvas.drawCircle(eventX,eventY,8,paint);

        //绘制连线
        paint.setStrokeWidth(3);
        canvas.drawLine(startX,startY,eventX,eventY,paint);
        canvas.drawLine(eventX,eventY,endX,endY,paint);

        //画二阶贝塞尔曲线
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.moveTo(startX,startY);//移动画笔到某个位置开始绘制
        path.quadTo(eventX,eventY,endX,endY);
        canvas.drawPath(path,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                eventX = (int) event.getX();
                eventY = (int) event.getY();
                invalidate();
                break;
        }
        return true;
    }

}