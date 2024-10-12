package com.dylan.library.widget.shape;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.Nullable;

import com.dylan.library.widget.DrawableTextView;


public class ShapeDrawableTextView extends DrawableTextView {
    public ShapeDrawableTextView(Context context) {
        this(context, null);
    }

    public ShapeDrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShapeDrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ShapeViewHelper shapeViewHelper = new ShapeViewHelper();
        shapeViewHelper.init(context,attrs);
        shapeViewHelper.setCustomBackground(this);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {

    }

    @Override
    protected void onDraw(Canvas canvas) {

        int drawablePadding = getCompoundDrawablePadding();
        translateText(canvas, drawablePadding);
        super.onDraw(canvas);


        float centerX = (getWidth() + getPaddingLeft() - getPaddingRight()) / 2;
        float centerY = (getHeight() + getPaddingTop() - getPaddingBottom()) / 2;

        float halfTextWidth = getPaint().measureText(getText().toString().isEmpty() ? getHint().toString() : getText().toString()) / 2;
        Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        float halfTextHeight = (fontMetrics.descent - fontMetrics.ascent) / 2;



        if (leftDrawable != null) {
            int left = (int) (centerX - drawablePadding - halfTextWidth - leftDrawableWidth);
            int top = (int) (centerY - leftDrawableHeight / 2);
            leftDrawable.setBounds(
                    left,
                    top,
                    left + leftDrawableWidth,
                    top + leftDrawableHeight);
            canvas.save();
            leftDrawable.draw(canvas);
            canvas.restore();
        }


        if (rightDrawable != null) {
            int left = (int) (centerX + halfTextWidth + drawablePadding);
            int top = (int) (centerY - rightDrawableHeight / 2);
            rightDrawable.setBounds(
                    left,
                    top,
                    left + rightDrawableWidth,
                    top + rightDrawableHeight);
            canvas.save();
            rightDrawable.draw(canvas);
            canvas.restore();
        }

        if (topDrawable != null) {
            int left = (int) (centerX - topDrawableWidth / 2);
            int bottom = (int) (centerY - halfTextHeight - drawablePadding);
            topDrawable.setBounds(
                    left,
                    bottom - topDrawableHeight,
                    left + topDrawableWidth,
                    bottom);
            canvas.save();
            topDrawable.draw(canvas);
            canvas.restore();
        }


        if (bottomDrawable != null) {
            int left = (int) (centerX - bottomDrawableWidth / 2);
            int top = (int) (centerY + halfTextHeight + drawablePadding);
            bottomDrawable.setBounds(
                    left,
                    top,
                    left + bottomDrawableWidth,
                    top + bottomDrawableHeight);
            canvas.save();
            bottomDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private void translateText(Canvas canvas, int drawablePadding) {

        int translateWidth = 0;
        if (leftDrawable != null && rightDrawable != null) {
            translateWidth = (leftDrawableWidth - rightDrawableWidth) / 2;
        } else if (leftDrawable != null) {
            translateWidth = (leftDrawableWidth + drawablePadding) / 2;
        } else if (rightDrawable != null) {
            translateWidth = -(rightDrawableWidth + drawablePadding) / 2;
        }

        int translateHeight = 0;
        if (topDrawable != null && bottomDrawable != null) {
            translateHeight = (topDrawableHeight - bottomDrawableHeight) / 2;
        } else if (topDrawable != null) {
            translateHeight = (topDrawableHeight + drawablePadding) / 2;
        } else if (bottomDrawable != null) {
            translateHeight = -(bottomDrawableHeight - drawablePadding) / 2;
        }

        canvas.translate(translateWidth, translateHeight);
    }

}
