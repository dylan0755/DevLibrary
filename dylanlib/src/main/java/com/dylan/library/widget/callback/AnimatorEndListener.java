package com.dylan.library.widget.callback;

import android.animation.Animator;

/**
 * Author: Dylan
 * Date: 2019/8/8
 * Desc:
 */
public abstract class AnimatorEndListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animation) {

    }


    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public abstract void onAnimationEnd(Animator animation);
}
