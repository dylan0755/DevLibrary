package com.dylan.library.widget.photoview;

import android.animation.TypeEvaluator;

/**
 * Author: Dylan
 * Date: 2020/3/3
 * Desc:
 */
public class ScaleEvaluator implements TypeEvaluator<ScaleValue> {
    @Override
    public ScaleValue evaluate(float fraction, ScaleValue startValue, ScaleValue endValue) {

        float startScaleX=startValue.getScaleX();
        float startScaleY=startValue.getScaleY();


        float currentScaleX=startScaleX+fraction*(endValue.getScaleX()-startScaleX);
        float currentScaleY=startScaleY+fraction*(endValue.getScaleY()-startScaleY);


        ScaleValue scaleValue=new ScaleValue();
        scaleValue.setScaleX(currentScaleX);
        scaleValue.setScaleY(currentScaleY);
        return scaleValue;
    }
}
