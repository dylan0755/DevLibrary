package com.dylan.library.widget.callback;

import android.view.animation.Animation;

/**
 * Author: Dylan
 * Date: 2019/9/10
 * Desc:
 */
public abstract class AnimationEndListener implements Animation.AnimationListener {
    @Override
    public void onAnimationStart(Animation animation) {

    }



    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public abstract void onAnimationEnd(Animation animation);
}
