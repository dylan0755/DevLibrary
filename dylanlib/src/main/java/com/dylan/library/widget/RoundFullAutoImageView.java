package com.dylan.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.dylan.library.R;


/**
 * Author: Dylan
 * Date: 2020/2/17
 * Desc:
 */
public class RoundFullAutoImageView extends AppCompatImageView {
    private Path mPath;
    private RectF mRectF;
    /**
     * 利用clip剪切的四个角半径，八个数据分别代表左上角（x轴半径，y轴半径），右上角（**），右下角（**），左下角（**）
     */
    private float[] rids = new float[8];
    private PaintFlagsDrawFilter paintFlagsDrawFilter;
    public RoundFullAutoImageView(Context context) {
        this(context, null,0);
    }

    public RoundFullAutoImageView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RoundFullAutoImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        /**
         * 此处圆角半径为四个角的半径
         */
        float mRadius = array.getDimension(R.styleable.RoundImageView_roundRadius, 10);
        rids[0] = mRadius;
        rids[1] = mRadius;
        rids[2] = mRadius;
        rids[3] = mRadius;
        rids[4] = mRadius;
        rids[5] = mRadius;
        rids[6] = mRadius;
        rids[7] = mRadius;
        array.recycle();
        mPath = new Path();
        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = this.getDrawable();
        if (d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int)Math.ceil((double)((float)width * (float)d.getIntrinsicHeight() / (float)d.getIntrinsicWidth()));
            this.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        mPath.addRoundRect(mRectF, rids, Path.Direction.CW);
        canvas.setDrawFilter(paintFlagsDrawFilter);
        canvas.save();
        canvas.clipPath(mPath);
        super.onDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF = new RectF(0, 0, w, h);
    }

}
