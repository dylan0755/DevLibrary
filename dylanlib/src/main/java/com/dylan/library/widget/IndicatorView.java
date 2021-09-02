package com.dylan.library.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import com.dylan.library.utils.DensityUtils;

/**
 * Author: Dylan
 * Date: 2021/09/02
 * Desc:
 */
public class IndicatorView extends LinearLayoutCompat {
    private Context context;

    private Drawable selectColor;
    private Drawable normalColor;
    private float alpha=1;

    public IndicatorView(Context context) {
        super(context);
        this.context = context;
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public IndicatorView initView(int indicatorCount,int selectPosition,Drawable selectDrawble,Drawable normalDrawble,float alpha){
        this.selectColor = selectDrawble;
        this.normalColor = normalDrawble;
        this.alpha = alpha;
        for (int i=0;i<indicatorCount;i++){
            AppCompatImageView ivIndicator = new AppCompatImageView(context);
            LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.leftMargin = i == 0 ? 0 : DensityUtils.dp2px(context,6);
            ivIndicator.setLayoutParams(lp);
            ivIndicator.setBackgroundDrawable(i == selectPosition ? selectDrawble : normalDrawble);
            ivIndicator.setAlpha(i == selectPosition ? 1 : alpha);
            addView(ivIndicator);
        }
        return this;
    }

    public void changeIndicator(int position){
        int count = getChildCount();
        for (int i=0;i<count;i++){
            AppCompatImageView ivIndecator  = (AppCompatImageView) getChildAt(i);
            ivIndecator.setBackgroundDrawable(normalColor);
            ivIndecator.setAlpha(alpha);
        }
        AppCompatImageView ivIndecator  = (AppCompatImageView) getChildAt(position);
        ivIndecator.setBackgroundDrawable(selectColor);
        ivIndecator.setAlpha(1.0f);
    }

    public static class Builder{
        private Context context;
        private int position;
        private int indicatorCount;
        private Drawable selectColor;
        private Drawable normalColor;
        private float alpha=1;

        public Builder setContext(Context context){
            this.context = context;
            return this;
        }

        public Builder setSelectPosition(int position){
            this.position = position;
            return this;
        }

        public Builder setIndicatorCount(int indicatorCount){
            this.indicatorCount = indicatorCount;
            return this;
        }

        public Builder setSelectColor(Drawable selectColor){
            this.selectColor = selectColor;
            return this;
        }

        public Builder setNormalColor(Drawable normalColor){
            this.normalColor = normalColor;
            return this;
        }

        public Builder setNormalAlpha(float alpha){
            this.alpha = alpha;
            return this;
        }

        public IndicatorView builder(){
            return  new IndicatorView(context).initView(indicatorCount,position,selectColor,normalColor,alpha);
        }
    }
}
