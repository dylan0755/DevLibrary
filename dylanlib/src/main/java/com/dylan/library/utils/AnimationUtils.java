package com.dylan.library.utils;

import android.content.Context;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.dylan.library.R;

/**
 * Author: Dylan
 * Date: 2020/2/17
 * Desc:
 */
public class AnimationUtils {


    public static RotateAnimation getRotateInSef360(int durationMills){
        RotateAnimation rotate  = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(durationMills);//设置动画持续时间
        rotate.setRepeatCount(-1);//设置重复次数
        rotate.setFillAfter(true);//动画执行完后是否停留在执
        return rotate;
    }



    //-----View 动画

    public static Animation getLeftSlideOutAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_400_left_enter_slide_out);
    }

    public static Animation getLeftSlideBackAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_400_left_exit_slide_back);
    }


    public static Animation getTopSlideOutAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_400_top_enter_slide_out);
    }


    public static Animation getTopSlideBackAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_400_top_exit_slide_back);
    }


    public static Animation getRightSlideOutAnimation(Context context){
         return android.view.animation.AnimationUtils.loadAnimation(context,R.anim.anim_400_right_enter_slide_out);
    }


    public static Animation getRightSlideBackAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context,R.anim.anim_400_right_exit_slide_back);
    }

    public static Animation getBottomSlideOutAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_400_bottom_enter_slide_out);
    }

    public static Animation getBottomSlideBackAnimation(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context, R.anim.anim_400_bottom_exit_slide_back);
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
    //-----View 动画



    public static @StyleRes int getLeftSlideWindowAnim400StyleResId(){
        return R.style.dl_anim_400_left_sliding;
    }
    public static @StyleRes int getTopSlideWindowAnim400StyleResId(){
        return R.style.dl_anim_400_top_sliding;
    }


    public static @StyleRes int getRightSlideWindowAnim400StyleResId(){
        return R.style.dl_anim_400_right_sliding;
    }

    public static @StyleRes int getBottomSlideWindowAnim400StyleResId(){
        return R.style.dl_anim_400_bottom_sliding;
    }

    public static @StyleRes int getLeftSlideWindowAnim300StyleResId(){
        return R.style.dl_anim_300_left_sliding;
    }
    public static @StyleRes int getTopSlideWindowAnim300StyleResId(){
        return R.style.dl_anim_300_top_sliding;
    }


    public static @StyleRes int getRightSlideWindowAnim300StyleResId(){
        return R.style.dl_anim_300_right_sliding;
    }

    public static @StyleRes int getBottomSlideWindowAnim300StyleResId(){
        return R.style.dl_anim_300_bottom_sliding;
    }

}
