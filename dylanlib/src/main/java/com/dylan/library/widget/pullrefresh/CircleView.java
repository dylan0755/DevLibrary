package com.dylan.library.widget.pullrefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.dylan.library.R;
import com.dylan.library.screen.ScaleUtils;


/**
 * Author： Dylan
 * Date: 2018/7/28
 * Des:
 */

public class CircleView extends View {
    //圆轮颜色
    private int mOutRingColor;
    private int mInnerRingColor;
    //圆轮宽度
    private float mRingWidth;
    //宽度
    private int mWidth;
    //高度
    private int mHeight;
    private Paint mPaint;
    //圆环的矩形区域
    private RectF mRectF;
    //
    private float mCurrentProgress;
    ScaleUtils scaleUtils;
    private boolean isRefreshing;
    private int startAngle=-90;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInnerRingColor=mInnerRingColor==0? ContextCompat.getColor(context, R.color.innerRingColor) :mInnerRingColor;
        mOutRingColor =mOutRingColor==0? ContextCompat.getColor(context,R.color.outRingColor):mOutRingColor;
        scaleUtils =new ScaleUtils(context);
        mRingWidth = scaleUtils.toScaleSize(5);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        this.setWillNotDraw(false);
    }

    public void setOutRingColor(int color){
        mOutRingColor =color;
    }

    public void setInnerRingColor(int color){
       mInnerRingColor=color;
    }



    public void setRingWidth(int px){
        mRingWidth = scaleUtils.toScaleSize(px);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRectF = new RectF(0 + mRingWidth / 2, 0 + mRingWidth / 2,
                mWidth - mRingWidth / 2, mHeight - mRingWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //颜色
        mPaint.setColor(mInnerRingColor);
        //空心
        mPaint.setStyle(Paint.Style.STROKE);
        //宽度
        mPaint.setStrokeWidth(mRingWidth);
        canvas.drawArc(mRectF, -90, 360 , false, mPaint);


        /**
         *圆环
         */
        //颜色
        mPaint.setColor(mOutRingColor);
        //空心
        mPaint.setStyle(Paint.Style.STROKE);
        //宽度
        mPaint.setStrokeWidth(mRingWidth);
        canvas.drawArc(mRectF, startAngle, mCurrentProgress , false, mPaint);

    }


    public void setProgress(int progress){
        mCurrentProgress = (int) (360 * (progress / 100f));
        invalidate();
    }

    public void refresh(){
        isRefreshing=true;
        startAngle+=10;
        mCurrentProgress=(int) (360 * (30 / 100f));
        invalidate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRefreshing) refresh();
            }
        },20);

    }

    public void completeRefresh(){
        isRefreshing=false;
        removeCallbacks(null);
        startAngle=-90;
        mCurrentProgress=0;
        invalidate();
    }











}
