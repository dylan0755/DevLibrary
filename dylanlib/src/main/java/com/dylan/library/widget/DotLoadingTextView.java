package com.dylan.library.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.dylan.library.utils.SpannableStringUtils;

public class DotLoadingTextView extends AppCompatTextView {
    private String dotString = "...";
    private int dotCount = 4;
    private ValueAnimator anim;

    public DotLoadingTextView(Context context) {
        super(context);
    }

    public DotLoadingTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public void startLoading(String loadingTip) {
        cancelAnim();
        anim = ValueAnimator.ofInt(0, dotCount);
        anim.setDuration(1000); // 动画持续时间
        anim.setRepeatCount(ValueAnimator.INFINITE); // 设置动画无限重复
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    int progress = (int) animation.getAnimatedValue();
                    if (progress > dotCount) {
                        progress = dotCount;
                    }
                    String fullText = loadingTip + dotString;
                    SpannableString spannableString = SpannableStringUtils.getAbsoluteSizeSpannableString(fullText, 24, fullText.indexOf(dotString), fullText.length());
                    if (progress <= 1) {
                        int start = fullText.indexOf(dotString) + 1;
                        spannableString = SpannableStringUtils.getTintSpannableString(spannableString, start, fullText.length(), Color.TRANSPARENT);
                    } else if (progress == 2) {
                        int start = fullText.indexOf(dotString) + 2;
                        spannableString = SpannableStringUtils.getTintSpannableString(spannableString, start, fullText.length(), Color.TRANSPARENT);
                    } else if (progress == 3) {
                        int start = fullText.indexOf(dotString) + 3;
                        spannableString = SpannableStringUtils.getTintSpannableString(spannableString, start, fullText.length(), Color.TRANSPARENT);
                    }
                    setText(spannableString);
                } catch (Exception ignore) {

                }
                // 更新TextView的文本
            }
        });
// 启动动画
        anim.start();
    }


    public void cancelAnim() {
        try {
            if (anim != null) anim.cancel();
        } catch (Exception ignore) {
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnim();
    }
}
