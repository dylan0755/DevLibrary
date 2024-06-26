package com.dylan.library.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

/**
 * Author: Dylan
 * Date: 2020/3/26
 * Desc:
 */
public class DrawableUtils {

    public static Drawable getTransparentDrawable(){
        return new ColorDrawable(Color.TRANSPARENT);
    }

    public static void resetShapeColor(View view, int backgroundColor) {
        if (view == null) return;
        GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground();
        if (gradientDrawable != null) {
            gradientDrawable.mutate();
            gradientDrawable.setColor(backgroundColor);
        }
    }


    public static void setBackgroundMutateAlpha(View view, int alpha) {
        if (view == null || view.getBackground() == null) return;
        Drawable drawable = view.getBackground();
        drawable.mutate();
        drawable.setAlpha(alpha);
    }


    public static void setSeekBarColor(SeekBar seekBar, int color){
        LayerDrawable layerDrawable = (LayerDrawable)seekBar.getProgressDrawable();
        Drawable dra=layerDrawable.getDrawable(2);
        dra.setColorFilter(color, PorterDuff.Mode.SRC);
        seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        seekBar.invalidate();
    }

    public static void setColorFilter(Drawable drawable,int filterColor){
        PorterDuffColorFilter colorFilter= new PorterDuffColorFilter(filterColor, PorterDuff.Mode.MULTIPLY);
        drawable.setColorFilter(colorFilter);
    }

    public static ColorStateList getColorStateList(int color){
        return new ColorStateList(new int[1][0],new int[]{color});
    }

    public static void gray(ImageView imageView){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(colorFilter);
    }


}
