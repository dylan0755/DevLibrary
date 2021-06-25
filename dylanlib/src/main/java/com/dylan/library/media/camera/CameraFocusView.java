package com.dylan.library.media.camera;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.dylan.library.R;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.Logger;

/**
 * 不支持在ConstraintLayout 布局下，如果在该布局下要套一个常规的布局
 */
public class CameraFocusView extends AppCompatImageView {

    private ValueAnimator mSizeAnimator;
    private int mWidth;
    private int mHeight;
    private float mScale;

    private float mRawX;
    private float mRawY;
    ViewGroup.MarginLayoutParams layoutParams;

    public CameraFocusView(Context context) {
        this(context, null);
    }

    public CameraFocusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }


    public CameraFocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DLCameraFocus);
        mWidth = typedArray.getDimensionPixelSize(R.styleable.DLCameraFocus_focus_width, DensityUtils.dp2px(context, 70));
        mHeight = typedArray.getDimensionPixelSize(R.styleable.DLCameraFocus_focus_height, DensityUtils.dp2px(context, 70));
        mScale = typedArray.getFloat(R.styleable.DLCameraFocus_focus_scale, 0.666f);
        typedArray.recycle();
        setScaleType(ScaleType.CENTER_INSIDE);

    }


    public void showCameraFocus(float x, float y) {
        removeCallbacks(mCameraFocusDismiss);
        postDelayed(mCameraFocusDismiss, 1000);
        if (mSizeAnimator == null) {
            mSizeAnimator = ValueAnimator.ofFloat(1, mScale).setDuration(300);
            mSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    showCameraFocusLayout((float) animation.getAnimatedValue());
                }
            });
        } else if (mSizeAnimator.isRunning()) {
            mSizeAnimator.end();
        }
        mRawX = x;
        mRawY = y;
        mSizeAnimator.start();
    }

    private void showCameraFocusLayout(float scale) {
        int w = (int) (mWidth * scale);
        int h = (int) (mHeight * scale);
        int left = (int) (mRawX - w / 2);
        int top = (int) (mRawY - h / 2);

        if (layoutParams == null) {
            layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        }
        layoutParams.width = w;
        layoutParams.height = h;
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        setLayoutParams(layoutParams);
    }

    private final Runnable mCameraFocusDismiss = new Runnable() {
        @Override
        public void run() {
            layoutParams.width = 0;
            layoutParams.height = 0;
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = 0;
            setLayoutParams(layoutParams);
        }
    };
}
