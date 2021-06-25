package com.dylan.library.widget.shape;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

/**
 * Author: Dylan
 * Date: 2021/05/18
 * Desc:
 */
public class HighLightStateDrawableGroup {
    private GradientDrawable normalDrawable;
    private GradientDrawable highLightDrawable;
    private TextView textView;
    private String normalTextColor, highLightTextColor;

    public HighLightStateDrawableGroup(GradientDrawable normalDrawable, GradientDrawable highLightDrawable) {
        this.normalDrawable = normalDrawable;
        this.highLightDrawable = highLightDrawable;
    }

    public void attachTextView(TextView textView, String normalTextColor, String highLightTextColor) {
        this.textView = textView;
        this.normalTextColor = normalTextColor;
        this.highLightTextColor = highLightTextColor;
    }


    public void changeDrawableState(boolean isHighLight) {
        if (textView == null) return;
        if (isHighLight) {
            textView.setBackground(highLightDrawable);
            textView.setTextColor(Color.parseColor(highLightTextColor));
        } else {
            textView.setBackground(normalDrawable);
            textView.setTextColor(Color.parseColor(normalTextColor));
        }
    }


}
