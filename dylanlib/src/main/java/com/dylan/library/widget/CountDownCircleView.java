package com.dylan.library.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.dylan.library.R;
import com.dylan.library.screen.ScaleUtils;


/**
 * Author： Dylan
 * Date: 2018/7/28
 * Des:
 */

public class CountDownCircleView extends View {
    //圆轮颜色
    private int mRingColor;
    //圆轮宽度
    private float mRingWidth;
    //宽度
    private int mWidth;
    //高度
    private int mHeight;
    private Paint mPaint;
    private Paint textPaint;
    //圆环的矩形区域
    private RectF mRectF;
    //
    private int mCountdownTime = 10;
    private float mCurrentProgress;
    private OnCountDownFinishListener mListener;
    private ValueAnimator valueAnimator;
    private CountDownTimer timer;
    ScaleUtils scaleUtils;
    public static final int TYPE_SKIP_TEXT=1;
    public static final int TYPE_SECOND_TEXT=2;
    private int textType=TYPE_SKIP_TEXT;
    private int textSize=38;
    private long currentMills=0;
    private Rect txtRec;




    public CountDownCircleView(Context context) {
        this(context, null);
    }

    public CountDownCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRingColor = Color.WHITE;
        scaleUtils =new ScaleUtils(context);
        mRingWidth = scaleUtils.toScaleSize(5);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        textSize=scaleUtils.toScaleSize(textSize);
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.WHITE);
        setBackgroundResource(R.drawable.bg_countdown_skip);
        getBackground().setAlpha(125);
        txtRec=new Rect();
       this.setWillNotDraw(false);
    }

    public void setRingColor(int color){
        mRingColor=color;
    }

    public void setRingWidth(int px){
        mRingWidth = scaleUtils.toScaleSize(px);
    }

    public void setTextType(int type){
        if (type==TYPE_SECOND_TEXT){
            textType=type;
            setTextSize(40);
        }else if (type==TYPE_SKIP_TEXT){
            textType=type;
            setTextSize(38);
        }
    }

    public void setTextSize(int px){
        textSize=scaleUtils.toScaleSize(px);
        textPaint.setTextSize(textSize);
    }


    public void setTextColor(int colorValue){
        textPaint.setColor(colorValue);
    }

    public void setCountDownDuration(int secondDuration) {
        this.mCountdownTime = secondDuration;
        this.currentMills=secondDuration;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        int border=scaleUtils.toScaleSize(1);
        mRectF = new RectF(0 + mRingWidth / 2+border, 0 + mRingWidth / 2+border,
                mWidth - mRingWidth / 2-border, mHeight - mRingWidth / 2-border);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         *圆环
         */
        //颜色
        mPaint.setColor(mRingColor);
        //空心
        mPaint.setStyle(Paint.Style.STROKE);
        //宽度
        mPaint.setStrokeWidth(mRingWidth);
        canvas.drawArc(mRectF, -90, mCurrentProgress - 360, false, mPaint);

        if (textType==TYPE_SKIP_TEXT){
            String skipText="跳过";
            textPaint.getTextBounds(skipText,0,skipText.length(),txtRec);
            float textWidth=textPaint.measureText(skipText);
            float baseLineY=getHeight()*1.0f/2+txtRec.height()*1.0f/2-txtRec.bottom;
            canvas.drawText(skipText,(getWidth()*1.0f-textWidth)/2,baseLineY,textPaint);
        }else if (textType==TYPE_SECOND_TEXT){
             String secondMillText=currentMills+"s";
            textPaint.getTextBounds(secondMillText,0,secondMillText.length(),txtRec);
            float textWidth=textPaint.measureText(secondMillText);
            float baseLineY=getHeight()*1.0f/2+txtRec.height()*1.0f/2-txtRec.bottom;
            canvas.drawText(secondMillText,(getWidth()*1.0f-textWidth)/2,baseLineY,textPaint);
        }
    }

    private ValueAnimator getValA(long countdownTime) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 100);
        valueAnimator.setDuration(countdownTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }



    public void startCountDown() {
        timer=new CountDownTimer(mCountdownTime * 1000,1000) {
             @Override
             public void onTick(long millisUntilFinished) {
                 currentMills=millisUntilFinished/1000L+1; //开始显示 设置时间， 结束是1
             }

             @Override
             public void onFinish() {
                 currentMills=0;
                 timer.cancel();
                 valueAnimator.end();
                 timer=null;
                 valueAnimator=null;
                 //倒计时结束回调
                 if (mListener != null) {
                     mListener.countDownFinished();
                 }
             }
         };
        valueAnimator= getValA(mCountdownTime * 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float i = Float.valueOf(String.valueOf(animation.getAnimatedValue()));
                mCurrentProgress = (int) (360 * (i / 100f));
                invalidate();
            }
        });
        valueAnimator.start();
        timer.start();
    }

    public void setAddCountDownListener(OnCountDownFinishListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCountDownFinishListener {
        void countDownFinished();
    }

    public void stopCountDown() {
        if (valueAnimator==null)return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            valueAnimator.pause();
            timer.cancel();
        }else{
            valueAnimator.end();
            timer.cancel();
        }
    }




}
