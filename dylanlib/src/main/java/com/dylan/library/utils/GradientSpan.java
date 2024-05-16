package com.dylan.library.utils;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.style.ReplacementSpan;

public class GradientSpan extends ReplacementSpan {
    private int mSize;
    private int startColor;
    private int endColor;
    private Orient orient;

    public enum Orient {
        LEFT_RIGHT, TOP_BOTTOM, LEFT_TOP_RIGHT_BOTTOM
    }

    public GradientSpan(int startColor, int endColor, Orient orient) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.orient = orient;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        mSize = (int) (paint.measureText(text, start, end));

        Paint.FontMetricsInt metrics = paint.getFontMetricsInt();
        if (fm != null) {
            fm.top = metrics.top;
            fm.ascent = metrics.ascent;
            fm.descent = metrics.descent;
            fm.bottom = metrics.bottom;
        }

        return mSize;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        RectF rect = new RectF(x, top, x + paint.measureText(text, start, end), bottom);
        LinearGradient lg;
        if (orient.equals(Orient.TOP_BOTTOM)) {
            lg = new LinearGradient(0, 0, 0, rect.bottom,
                    startColor,
                    endColor,
                    Shader.TileMode.REPEAT); //从上到下
        } else if (orient.equals(Orient.LEFT_TOP_RIGHT_BOTTOM)) {
            lg = new LinearGradient(0, 0, rect.right, rect.bottom,
                    startColor,
                    endColor,
                    Shader.TileMode.REPEAT); //从左到右
        } else {
            lg = new LinearGradient(0, 0, rect.right, 0,
                    startColor,
                    endColor,
                    Shader.TileMode.REPEAT); //从左到右
        }
        paint.setShader(lg);

        canvas.drawText(text, start, end, x, y, paint);//绘制文字
    }

}