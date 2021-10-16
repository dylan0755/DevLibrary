package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;



/**
 * Author: Dylan
 * Date: 2021/10/16
 * Desc:
 */
public class RoundCropTopImageView extends RoundImageView {

    public RoundCropTopImageView(Context context) {
        this(context,null);
    }


    public RoundCropTopImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundCropTopImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.CENTER_CROP);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected boolean setFrame(int frameLeft, int frameTop, int frameRight, int frameBottom)
    {
        if (getDrawable() == null) {
            return super.setFrame(frameLeft, frameTop, frameRight, frameBottom);
        }
        float frameWidth = frameRight - frameLeft;
        float frameHeight = frameBottom - frameTop;

        float originalImageWidth = (float) getDrawable().getIntrinsicWidth();
        float originalImageHeight = (float) getDrawable().getIntrinsicHeight();

        float usedScaleFactor = 1;

        if ((frameWidth > originalImageWidth) || (frameHeight > originalImageHeight)) {
            float fitHorizontallyScaleFactor = frameWidth / originalImageWidth;
            float fitVerticallyScaleFactor = frameHeight / originalImageHeight;

            usedScaleFactor = Math.max(fitHorizontallyScaleFactor, fitVerticallyScaleFactor);
        }

        float newImageWidth = originalImageWidth * usedScaleFactor;
        float newImageHeight = originalImageHeight * usedScaleFactor;

        Matrix matrix = getImageMatrix();
        matrix.setScale(usedScaleFactor, usedScaleFactor, 0, 0); // Replaces the old matrix completly
        // matrix.postTranslate((frameWidth - newImageWidth) / 2, frameHeight - newImageHeight);//BottomCrop
        matrix.postTranslate((frameWidth - newImageWidth) / 2, 0);//Top Crop
        setImageMatrix(matrix);
        return super.setFrame(frameLeft, frameTop, frameRight, frameBottom);
    }



}
