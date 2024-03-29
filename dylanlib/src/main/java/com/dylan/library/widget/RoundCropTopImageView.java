//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;

public class RoundCropTopImageView extends RoundImageView {
    public RoundCropTopImageView(Context context) {
        this(context, (AttributeSet)null);
    }

    public RoundCropTopImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundCropTopImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setScaleType(ScaleType.CENTER_CROP);
        this.setScaleType(ScaleType.MATRIX);
    }

    protected boolean setFrame(int frameLeft, int frameTop, int frameRight, int frameBottom) {
        if (getDrawable() == null) {
            return super.setFrame(frameLeft, frameTop, frameRight, frameBottom);
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
        return super.setFrame(frameLeft, frameTop, frameRight, frameBottom);
    }
}
