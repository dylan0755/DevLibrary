package com.dylan.library.callback;

import android.animation.Animator;

/**
 * Author: Dylan
 * Date: 2019/9/30
 * Desc:
 */
public  abstract class EndAnimatorCallBack implements Animator.AnimatorListener {



    @Override
    public abstract void onAnimationEnd(Animator animation);

    @Override
    public void onAnimationStart(Animator animation) {

    }


    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
