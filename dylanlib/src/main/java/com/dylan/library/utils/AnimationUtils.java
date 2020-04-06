package com.dylan.library.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import com.dylan.library.R;

/**
 * Author: Dylan
 * Date: 2020/2/17
 * Desc:
 */
public class AnimationUtils {



    public static Animation getLeftSlideOutAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_left_enter_slide_out);
    }

    public static Animation getLeftSlideBackAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_left_exit_slide_back);
    }


    public static Animation getTopSlideOutAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_top_enter_slide_out);
    }


    public static Animation getTopSlideBackAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_top_exit_slide_back);
    }


    public static Animation getRightSlideOutAnimation(Context context){
         return android.view.animation.AnimationUtils.loadAnimation(context,R.anim.anim_right_enter_slide_out);
    }


    public static Animation getRightSlideBackAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context,R.anim.anim_right_exit_slide_back);
    }

    public static Animation getBottomSlideOutAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_bottom_enter_slide_out);
    }

    public static Animation getBottomSlideBackAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_bottom_exit_slide_back);
    }

    public static Animation startLeftSlideOutAnimation(View view){
        Animation animation=getLeftSlideOutAnimation(view.getContext());
        view.startAnimation(animation);
        return animation;
    }

    public static Animation startLeftSlideBackAnimation(View view){
        Animation animation=getLeftSlideBackAnimation(view.getContext());
        view.startAnimation(animation);
        return animation;
    }

    public static Animation startTopSlideOutAnimation(View view){
        Animation animation=getTopSlideOutAnimation(view.getContext());
        view.startAnimation(animation);
        return animation;
    }


    public static Animation startTopSlideBackAnimation(View view){
        Animation animation=getTopSlideBackAnimation(view.getContext());
        view.startAnimation(animation);
        return animation;
    }


    public static Animation startRightSlideOutAnimation(View view){
        Animation animation=getRightSlideOutAnimation(view.getContext());
        view.startAnimation(animation);
        return animation;
    }

    public static Animation startRightSlideBackAnimation(View view){
        Animation animation=getRightSlideBackAnimation(view.getContext());
        view.startAnimation(animation);
        return animation;
    }


    public static Animation startBottomSlideOutAnimation(View view){
        Animation animation=getBottomSlideOutAnimation(view.getContext());
        view.startAnimation(animation);
        return animation;
    }

    public static Animation startBottomSlideBackAnimation(View view){
        Animation animation=getBottomSlideBackAnimation(view.getContext());
        view.startAnimation(animation);
        return animation;
    }


}
