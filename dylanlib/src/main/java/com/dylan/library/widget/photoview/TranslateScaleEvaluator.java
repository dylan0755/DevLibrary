package com.dylan.library.widget.photoview;

import android.animation.TypeEvaluator;

/**
 * Author: Dylan
 * Date: 2019/8/1
 * Desc:
 */
public class TranslateScaleEvaluator implements TypeEvaluator<TranslateScaleValue> {
    @Override
    public TranslateScaleValue evaluate(float fraction, TranslateScaleValue startValue, TranslateScaleValue endValue) {
        float startX = startValue.getX();
        float startY = startValue.getY();
        float startWidth = startValue.getWidth();
        float startHeight = startValue.getHeight();
        float startTranX=startValue.getTranX();
        float startTranY=startValue.getTranY();
        float startScaleX=startValue.getScaleX();
        float startScaleY=startValue.getScaleY();





        float currentX = startX + fraction * (endValue.getX() - startX);
        float currentY = startY + fraction * (endValue.getY() - startY);
        float currentWidth = startWidth + fraction * (endValue.getWidth() - startWidth);
        float currentHeight = startHeight + fraction * (endValue.getHeight() - startHeight);
        float currentTranX=startTranX+fraction*(endValue.getTranX()-startTranX);
        float currentTranY=startTranY+fraction*(endValue.getTranY()-startTranY);
        float currentScaleX=startScaleX+fraction*(endValue.getScaleX()-startScaleX);
        float currentScaleY=startScaleY+fraction*(endValue.getScaleY()-startScaleY);








        TranslateScaleValue value = new TranslateScaleValue();
        value.setX(currentX);
        value.setY(currentY);
        value.setWidth(currentWidth);
        value.setHeight(currentHeight);
        value.setTranX(currentTranX);
        value.setTranY(currentTranY);
        value.setScaleX(currentScaleX);
        value.setScaleY(currentScaleY);
        return value;
    }
}
