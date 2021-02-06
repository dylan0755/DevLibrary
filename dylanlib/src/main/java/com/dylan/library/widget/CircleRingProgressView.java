package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.dylan.library.screen.ScaleUtils;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.TextDrawer;


/**
 * Author： Dylan
 * Date: 2018/7/28
 * Des: 圆环进度条
 */

public class CircleRingProgressView extends View {
    private TextDrawer textDrawer;
    private String mCenterText;

    private int backgroundColor=Color.TRANSPARENT;

    //圆轮颜色
    private int mRingProgressColor;
    private int mRingBackgroundColor;
    //圆轮宽度
    private float mRingStrokeWidth;
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
    private int startAngle = -90;

    public CircleRingProgressView(Context context) {
        this(context, null);
    }

    public CircleRingProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleRingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRingBackgroundColor = mRingBackgroundColor == 0 ? Color.parseColor("#d0d0d0") : mRingBackgroundColor;
        mRingProgressColor = mRingProgressColor == 0 ? Color.parseColor("#999999") : mRingProgressColor;
        scaleUtils = new ScaleUtils(context);
        mRingStrokeWidth = scaleUtils.toScaleSize(5);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        textDrawer=new TextDrawer();
        textDrawer.setTextColor(Color.WHITE);
        textDrawer.setTextSize(DensityUtils.dp2px(context, 14));
        this.setWillNotDraw(false);
    }

    public void setCenterBackgroundColor(int color){
        backgroundColor=color;
    }

    public void setRingProgressColor(int color) {
        mRingProgressColor = color;
    }

    public void setRingBackgroundColor(int color) {
        mRingBackgroundColor = color;
    }


    public void setRingStrokeWidth(int px) {
        mRingStrokeWidth = scaleUtils.toScaleSize(px);
    }

    public void setCenterText(String centerText) {
        mCenterText = centerText;
    }

    public void setCenterTextBold(float fraction){
        textDrawer.setTextBold(fraction);
    }

    public void setCenterTextSize(float sp) {
        textDrawer.setTextSize(DensityUtils.dp2px(getContext(),sp));
    }
    public void setCenterTextColor(int color) {
        textDrawer.setTextColor(color);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRectF = new RectF(0 + mRingStrokeWidth / 2, 0 + mRingStrokeWidth / 2,
                mWidth - mRingStrokeWidth / 2, mHeight - mRingStrokeWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //背景颜色不透明
        if (backgroundColor!=0){
            //背景颜色
            mPaint.setColor(backgroundColor);
            //空心
            mPaint.setStyle(Paint.Style.FILL);
            //宽度
            mPaint.setStrokeWidth(mRingStrokeWidth);
            canvas.drawArc(mRectF, -90, 360, false, mPaint);
        }


        //颜色
        mPaint.setColor(mRingBackgroundColor);
        //空心
        mPaint.setStyle(Paint.Style.STROKE);
        //宽度
        mPaint.setStrokeWidth(mRingStrokeWidth);
        canvas.drawArc(mRectF, -90, 360, false, mPaint);



        /**
         *圆环
         */
        //颜色
        mPaint.setColor(mRingProgressColor);
        //空心
        mPaint.setStyle(Paint.Style.STROKE);
        //宽度
        mPaint.setStrokeWidth(mRingStrokeWidth);
        canvas.drawArc(mRectF, startAngle, mCurrentProgress, false, mPaint);

        //绘制文字
        if (!TextUtils.isEmpty(mCenterText)) {
         textDrawer.draw(canvas,mCenterText,getMeasuredWidth(),getMeasuredHeight());
        }

    }


    public void setProgress(int progress) {
        mCurrentProgress = (int) (360 * (progress / 100f));
        invalidate();
    }

    public void setProgress(int progress,String centerText) {
        mCurrentProgress = (int) (360 * (progress / 100f));
        mCenterText=centerText;
        invalidate();
    }



    public void refresh() {
        isRefreshing = true;
        startAngle += 10;
        mCurrentProgress = (int) (360 * (30 / 100f));
        invalidate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRefreshing) refresh();
            }
        }, 20);

    }

    public void completeRefresh() {
        isRefreshing = false;
        removeCallbacks(null);
        startAngle = -90;
        mCurrentProgress = 0;
        invalidate();
    }


}
