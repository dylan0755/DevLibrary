package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

/**
 * Author: Dylan
 * Date: 2021/07/19
 * Desc:
 */
public class DrawableCenterButton extends TextView {
    private Drawable[] drawables;
    private float textWidth;
    private float bodyWidth;

    public DrawableCenterButton(Context context) {
        super(context);
        init();
    }

    public DrawableCenterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawableCenterButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawables = getCompoundDrawables();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        textWidth = getPaint().measureText(getText().toString());
        Drawable drawableRight = drawables[0];
        int totalWidth = getWidth();
        if (drawableRight != null) {
            int drawableWidth = drawableRight.getIntrinsicWidth();
            int drawablePadding = getCompoundDrawablePadding();
            bodyWidth = textWidth + drawableWidth + drawablePadding;
            //  setPadding(0,0,(int)(totalWidth - bodyWidth),0);
        }
    }

    public void setText(String text) {
        if (text.equals(getText().toString()))
            return;
        super.setText(text);
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        canvas.translate((width - bodyWidth) / 2, 0);
        super.onDraw(canvas);
    }
}
