package com.dylan.library.widget.shape;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dylan.library.R;

/**
 * Author: Dylan
 * Date: 2020/4/19
 * Desc:
 */

public class ShadowLayout extends ViewGroup {

    private float shadowWidth;
    private float cornerRadius;
    private Paint mShadowPaint;
    private Path clipPath;
    private PaintFlagsDrawFilter  paintFlagsDrawFilter;
    public ShadowLayout(Context context) {
        this(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout);
        int shadowColor = a.getColor(R.styleable.ShadowLayout_shadowColor, Color.parseColor("#37000000"));
        shadowWidth = a.getDimension(R.styleable.ShadowLayout_shadowWith, 0);
        cornerRadius = a.getDimension(R.styleable.ShadowLayout_cornerRadius, 0);
        float dx = a.getDimension(R.styleable.ShadowLayout_shadowDX, 0);
        float dy = a.getDimension(R.styleable.ShadowLayout_shadowDY, 0);
        a.recycle();
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(shadowColor);
        mShadowPaint.setShadowLayer(shadowWidth, dx, dy, shadowColor);
        paintFlagsDrawFilter= new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() != 1) {
            throw new IllegalStateException("子View只能有一个");
        }
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        View child = getChildAt(0);
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        int childBottomMargin = (int) (Math.max(shadowWidth, layoutParams.bottomMargin) + 1);
        int childLeftMargin = (int) (Math.max(shadowWidth, layoutParams.leftMargin) + 1);
        int childRightMargin = (int) (Math.max(shadowWidth, layoutParams.rightMargin) + 1);
        int childTopMargin = (int) (Math.max(shadowWidth, layoutParams.topMargin) + 1);
        int widthMeasureSpecMode;
        int widthMeasureSpecSize;
        int heightMeasureSpecMode;
        int heightMeasureSpecSize;
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthMeasureSpecMode = MeasureSpec.UNSPECIFIED;
            widthMeasureSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                widthMeasureSpecMode = MeasureSpec.EXACTLY;
                widthMeasureSpecSize = measuredWidth - childLeftMargin - childRightMargin;
            } else if (LayoutParams.WRAP_CONTENT == layoutParams.width) {
                widthMeasureSpecMode = MeasureSpec.AT_MOST;
                widthMeasureSpecSize = measuredWidth - childLeftMargin - childRightMargin;
            } else {
                widthMeasureSpecMode = MeasureSpec.EXACTLY;
                widthMeasureSpecSize = layoutParams.width;
            }
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightMeasureSpecMode = MeasureSpec.UNSPECIFIED;
            heightMeasureSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                heightMeasureSpecMode = MeasureSpec.EXACTLY;
                heightMeasureSpecSize = measuredHeight - childBottomMargin - childTopMargin;
            } else if (LayoutParams.WRAP_CONTENT == layoutParams.height) {
                heightMeasureSpecMode = MeasureSpec.AT_MOST;
                heightMeasureSpecSize = measuredHeight - childBottomMargin - childTopMargin;
            } else {
                heightMeasureSpecMode = MeasureSpec.EXACTLY;
                heightMeasureSpecSize = layoutParams.height;
            }
        }
        measureChild(child, MeasureSpec.makeMeasureSpec(widthMeasureSpecSize, widthMeasureSpecMode), MeasureSpec.makeMeasureSpec(heightMeasureSpecSize, heightMeasureSpecMode));
        int parentWidthMeasureSpec = MeasureSpec.getMode(widthMeasureSpec);
        int parentHeightMeasureSpec = MeasureSpec.getMode(heightMeasureSpec);
        int height = measuredHeight;
        int width = measuredWidth;
        int childHeight = child.getMeasuredHeight();
        int childWidth = child.getMeasuredWidth();
        if (parentHeightMeasureSpec == MeasureSpec.AT_MOST) {
            height = childHeight + childTopMargin + childBottomMargin;
        }
        if (parentWidthMeasureSpec == MeasureSpec.AT_MOST) {
            width = childWidth + childRightMargin + childLeftMargin;
        }
        if (width < childWidth + 2 * shadowWidth) {
            width = (int) (childWidth + 2 * shadowWidth);
        }
        if (height < childHeight + 2 * shadowWidth) {
            height = (int) (childHeight + 2 * shadowWidth);
        }
        if (height != measuredHeight || width != measuredWidth) {
            setMeasuredDimension(width, height);
        }
    }

    static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child = getChildAt(0);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int childMeasureWidth = child.getMeasuredWidth();
        int childMeasureHeight = child.getMeasuredHeight();
        child.layout((measuredWidth - childMeasureWidth) / 2, (measuredHeight - childMeasureHeight) / 2, (measuredWidth + childMeasureWidth) / 2, (measuredHeight + childMeasureHeight) / 2);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        clipPath = new Path();
        clipPath.reset();
        clipPath.addRoundRect(new RectF(shadowWidth, shadowWidth, getMeasuredWidth() - shadowWidth, getMeasuredHeight() - shadowWidth), cornerRadius, cornerRadius, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (getLayerType() != LAYER_TYPE_SOFTWARE) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        View child = getChildAt(0);

        int left = child.getLeft();
        int top = child.getTop();
        int right = child.getRight();
        int bottom = child.getBottom();

        //默认白色背景
        Paint bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //绘制阴影
            canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, mShadowPaint);
            canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, bgPaint);
        } else {
            drawRoundRect(canvas, left, top, right, bottom,mShadowPaint);
            drawRoundRect(canvas, left, top, right, bottom,bgPaint);
        }


        if (cornerRadius>0){
            canvas.setDrawFilter(paintFlagsDrawFilter);
            canvas.save();
            canvas.clipPath(clipPath);//裁剪，若设置了圆角，则子View只能为圆角
            super.dispatchDraw(canvas);
            canvas.restore();

            //消除锯齿
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            canvas.drawPath(clipPath, paint);
            canvas.restoreToCount(saveCount);
            paint.setXfermode(null);
        }else{
            super.dispatchDraw(canvas);
        }


    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(paintFlagsDrawFilter);
        super.onDraw(canvas);
    }

    private void drawRoundRect(Canvas canvas, int left, int top, int right, int bottom, Paint paint) {
        Path drawablePath = new Path();
        drawablePath.moveTo(left + cornerRadius, top);
        drawablePath.arcTo(new RectF(left, top, left + 2 * cornerRadius, top + 2 * cornerRadius), -90, -90, false);
        drawablePath.lineTo(left, bottom - cornerRadius);
        drawablePath.arcTo(new RectF(left, bottom - 2 * cornerRadius, left + 2 * cornerRadius, bottom), 180, -90, false);
        drawablePath.lineTo(right - cornerRadius, bottom);
        drawablePath.arcTo(new RectF(right - 2 * cornerRadius, bottom - 2 * cornerRadius, right, bottom), 90, -90, false);
        drawablePath.lineTo(right, top + cornerRadius);
        drawablePath.arcTo(new RectF(right - 2 * cornerRadius, top, right, top + 2 * cornerRadius), 0, -90, false);
        drawablePath.close();
        canvas.drawPath(drawablePath, paint);
    }


}