package com.dylan.library.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Author: Dylan
 * Date: 2019/9/25
 * Desc:
 */
public class ClickableSpanTextView  extends TextView {
    boolean dontConsumeNonUrlClicks = true;
    boolean linkHit;

    public ClickableSpanTextView(Context context) {
        super(context);
    }

    public ClickableSpanTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public ClickableSpanTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs, defStyleAttr);
    }

    @Override
    public boolean hasFocusable() {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        linkHit = false;
        boolean res = super.onTouchEvent(event);

        if (dontConsumeNonUrlClicks)
            return linkHit;
        return res;

    }

    /** 设置movementMethod，并修改TextView不可点击、长按、获取焦点等 */
    public void setLocalLinkMovementMethod(ClickableLinkMovementMethod movementMethod) {
        setMovementMethod(movementMethod);
    }
}