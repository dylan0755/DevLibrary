package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Author: Dylan
 * Date: 2022/07/01
 * Desc:
 */
public class CropTopImageView extends AppCompatImageView {

    public CropTopImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
    }

    public CropTopImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.MATRIX);
    }

    public CropTopImageView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b)
    {
        if (getDrawable() == null) {
            return super.setFrame(l, t, r, b);
        }
        Matrix matrix = getImageMatrix();
        float scaleWidth = getWidth()/(float)getDrawable().getIntrinsicWidth();
        float scaleHeight = getHeight()/(float)getDrawable().getIntrinsicHeight();
        float scaleFactor = (scaleWidth > scaleHeight) ? scaleWidth : scaleHeight;
        matrix.setScale(scaleFactor, scaleFactor, 0, 0);
        if (scaleFactor == scaleHeight) {
            float tanslateX = ((getDrawable().getIntrinsicWidth() * scaleFactor) - getWidth()) / 2;
            matrix.postTranslate(-tanslateX, 0);
        }
        setImageMatrix(matrix);
        return super.setFrame(l, t, r, b);
    }
}
