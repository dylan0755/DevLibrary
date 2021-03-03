package com.dylan.library.widget.shape;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

/**
 * Author: Dylan
 * Date: 2020/10/29
 * Desc:
 */
public class GradientDrawableBuilder {

    private Context mContext;
    private GradientDrawable drawable;
    public GradientDrawableBuilder(Context context){
        mContext=context;
        drawable = new GradientDrawable();
    }


    public GradientDrawableBuilder setShape(int shape){
        drawable.setShape(shape);
        return this;
    }

    public GradientDrawableBuilder setCornerRadius(float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius){
        //单位转换
        topLeftRadius=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                topLeftRadius, mContext.getResources().getDisplayMetrics());
        topRightRadius= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                topRightRadius, mContext.getResources().getDisplayMetrics());
        bottomRightRadius= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                bottomRightRadius, mContext.getResources().getDisplayMetrics());
        bottomLeftRadius= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                bottomLeftRadius, mContext.getResources().getDisplayMetrics());

        float[] radii=new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius};
        drawable.setCornerRadii(radii);
        return this;
    }



    public GradientDrawableBuilder setCornerRadius(float radius){
        radius= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                radius, mContext.getResources().getDisplayMetrics());
        drawable.setCornerRadius(radius);
        return this;
    }


    public GradientDrawableBuilder setStroke(int width, int color){
        width= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                width, mContext.getResources().getDisplayMetrics());

        drawable.setStroke( width, color, 0, 0);
        return this;
    }


    public GradientDrawableBuilder setStroke(int width, @ColorInt int color, float dashWidth, float dashGap){
        //单位转换
        width= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                width, mContext.getResources().getDisplayMetrics());
        dashWidth= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dashWidth, mContext.getResources().getDisplayMetrics());
        dashGap= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dashGap, mContext.getResources().getDisplayMetrics());

        drawable.setStroke( width, color, dashWidth, dashGap);
        return this;
    }

    public GradientDrawableBuilder setSolidColor(int color){
        drawable.setColor(color);
        return this;
    }

    public GradientDrawableBuilder setGradientColors(int[] colors,int angle){
        drawable.setColors(colors);
        if (angle==0||angle==360){
            drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        }else if (angle==90){
            drawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        }else if (angle==180){
            drawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
        }else if (angle==270){//上到下
            drawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        }
        return this;
    }


    public GradientDrawable build() {
        mContext=null;
        return drawable;
    }


}
