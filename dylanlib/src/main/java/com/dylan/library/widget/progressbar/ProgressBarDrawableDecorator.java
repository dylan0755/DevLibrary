package com.dylan.library.widget.progressbar;

import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.widget.ProgressBar;

import com.dylan.library.utils.DensityUtils;

/**
 * Author: Dylan
 * Date: 2020/8/22
 * Desc: 进度条设置
 */
public class ProgressBarDrawableDecorator {
    GradientDrawable bgDrawable;
    GradientDrawable progressDrawable;
    GradientDrawable secondaryProgress;
    ProgressBar progressBar;

    public ProgressBarDrawableDecorator(ProgressBar progressBar) {
        this.progressBar = progressBar;
        bgDrawable = new GradientDrawable();
        secondaryProgress=new GradientDrawable();
        progressDrawable = new GradientDrawable();
    }


    public ProgressBarDrawableDecorator setBGStroke(int width, int color) {
        width = DensityUtils.dp2px(progressBar.getContext(), width);
        bgDrawable.setStroke(width, color);
        return this;
    }
    public ProgressBarDrawableDecorator setBGStroke(int width, String colorString) {
       return setBGStroke(width,Color.parseColor(colorString));
    }


    public ProgressBarDrawableDecorator setBGSolidColor(int color) {
        bgDrawable.setColor(color);
        return this;
    }
    public ProgressBarDrawableDecorator setBGSolidColor(String colorString) {
       return setBGSolidColor(Color.parseColor(colorString));
    }


    public ProgressBarDrawableDecorator setCornerRadius(float roundRadius) {
        roundRadius = DensityUtils.dp2px(progressBar.getContext(), roundRadius);
        bgDrawable.setCornerRadius(roundRadius);
        progressDrawable.setCornerRadius(roundRadius);
        secondaryProgress.setCornerRadius(roundRadius);
        return this;
    }


    public ProgressBarDrawableDecorator setProgressColor(int color) {
        progressDrawable.setColor(color);
        return this;
    }


    public ProgressBarDrawableDecorator setProgressColor(String colorString) {
       return setProgressColor(Color.parseColor(colorString));
    }


    public ProgressBarDrawableDecorator setSecondaryProgressColor(int color){
        secondaryProgress.setColor(color);
        return this;
    }

    public ProgressBarDrawableDecorator setSecondaryProgressColor(String colorString){
       return setSecondaryProgressColor(Color.parseColor(colorString));
    }


    public LayerDrawable decorate() {
        //ClipDrawable是对一个Drawable进行剪切操作，可以控制这个drawable的剪切区域，以及相相对于容器的对齐方式
        ClipDrawable progressClip = new ClipDrawable(progressDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        ClipDrawable secondaryProgressClip = new ClipDrawable(secondaryProgress, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        //待设置的Drawable数组
        Drawable[] progressDrawables = {bgDrawable,secondaryProgressClip, progressClip};
        LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
        //根据ID设置progressBar对应内容的Drawable
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
        progressLayerDrawable.setId(2, android.R.id.progress);
        //设置progressBarDrawable
        progressBar.setProgressDrawable(progressLayerDrawable);
        return progressLayerDrawable;
    }


}