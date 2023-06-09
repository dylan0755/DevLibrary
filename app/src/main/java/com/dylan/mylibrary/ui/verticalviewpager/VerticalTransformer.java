package com.dylan.mylibrary.ui.verticalviewpager;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import android.view.View;

/**
 * Author: Dylan
 * Date: 2019/8/1
 * Desc:
 */

public class VerticalTransformer implements ViewPager.PageTransformer {
//    private float MIN_SCALE = 0.5f;
//
//    @Override
//    public void transformPage(View view, float position) {
//        if (position < -1) { // [-Infinity,-1)
//
//
//        } else if (position <= 1) { // [-1,1]
////            view.setAlpha(1 + position);
//            view.setTranslationX(-view.getWidth() * position);
//            view.setPivotY(view.getMeasuredHeight());
//            view.setPivotX(view.getMeasuredWidth() / 2);
//            view.setTranslationY(position * view.getHeight());
////            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
////                    * (1 - Math.abs(position));
////            view.setScaleX(scaleFactor);
////            view.setScaleY(scaleFactor);
//
//        }  else { // (1,+Infinity]
//            // view.setAlpha(0);
//            //  view.setTranslationY( position * view.getHeight());
//        }
//    }


    @Override
    public void transformPage(@NonNull View view, float position) {
        float alpha = 0;
        if (0 <= position && position <= 1) {
            alpha = 1 - position;
        } else if (-1 < position && position < 0) {
            alpha = position + 1;
        }
        //view.setAlpha(alpha);
        view.setTranslationX(view.getWidth() * -position);
        float yPosition = position * view.getHeight();
        view.setTranslationY(yPosition);
    }
}
