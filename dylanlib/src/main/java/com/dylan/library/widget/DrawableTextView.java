package com.dylan.library.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.utils.CompatUtils;
import com.dylan.library.utils.DensityUtils;


/**
 * Author: Dylan
 * Date: 2020/4/1
 * Desc:
 */
public class DrawableTextView extends TextView {
    public final static int POSITION_LEFT = 0;
    public final static int POSITION_TOP = 1;
    public final static int POSITION_RIGHT = 2;
    public final static int POSITION_BOTTOM = 3;

    protected int leftDrawableWidth = 10;
    protected int leftDrawableHeight = 10;
    protected int topDrawableWidth = 10;
    protected int topDrawableHeight = 10;
    protected int rightDrawableWidth = 10;
    protected int rightDrawableHeight = 10;
    protected int bottomDrawableWidth = 10;
    protected int bottomDrawableHeight = 10;
    Paint mPaint;
    Paint mPaint2;
    Rect mBound;
    protected Drawable leftDrawable;
    protected Drawable topDrawable;
    protected Drawable rightDrawable;
    protected Drawable bottomDrawable;

    public DrawableTextView(Context context) {
        this(context, null, 0);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttributes(context, attrs, defStyleAttr);
    }


    public void getAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DLDrawableTextView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.DLDrawableTextView_drawableLeftWidth) {
                leftDrawableWidth = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.DLDrawableTextView_drawableLeftHeight) {
                leftDrawableHeight = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.DLDrawableTextView_drawableTopWidth) {
                topDrawableWidth = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.DLDrawableTextView_drawableTopHeight) {
                topDrawableHeight = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.DLDrawableTextView_drawableRightWidth) {
                rightDrawableWidth = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.DLDrawableTextView_drawableRightHeight) {
                rightDrawableHeight = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.DLDrawableTextView_drawableBottomWidth) {
                bottomDrawableWidth = a.getDimensionPixelSize(attr, 10);
            } else if (attr == R.styleable.DLDrawableTextView_drawableBottomHeight) {
                bottomDrawableHeight = a.getDimensionPixelSize(attr, 10);
            }
        }
        a.recycle();

        /*
         * setCompoundDrawablesWithIntrinsicBounds方法会首先在父类的构造方法中执行，
         * 彼时执行时drawable的大小还都没有开始获取，都是0,
         * 这里获取完自定义的宽高属性后再次调用这个方法，插入drawable的大小
         * */
        setCompoundDrawablesWithIntrinsicBounds(
                leftDrawable, topDrawable, rightDrawable, bottomDrawable);


    }


    /**
     * Sets the Drawables (if any) to appear to the left of, above, to the
     * right of, and below the text. Use {@code null} if you do not want a
     * Drawable there. The Drawables' bounds will be set to their intrinsic
     * bounds.
     * <p>
     * Calling this method will overwrite any Drawables previously set using
     * {@link #setCompoundDrawablesRelative} or related methods.
     * 这里重写这个方法，来设置上下左右的drawable的大小
     *
     * @attr ref android.R.styleable#TextView_drawableLeft
     * @attr ref android.R.styleable#TextView_drawableTop
     * @attr ref android.R.styleable#TextView_drawableRight
     * @attr ref android.R.styleable#TextView_drawableBottom
     */
    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left,
                                                        Drawable top,
                                                        Drawable right,
                                                        Drawable bottom) {
        this.leftDrawable = left;
        this.topDrawable = top;
        this.rightDrawable = right;
        this.bottomDrawable = bottom;


        if (left != null) {
            left.setBounds(0, 0, leftDrawableWidth, leftDrawableHeight);
        }
        if (right != null) {
            right.setBounds(0, 0, rightDrawableWidth, rightDrawableHeight);
        }
        if (top != null) {
            top.setBounds(0, 0, topDrawableWidth, topDrawableHeight);
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, bottomDrawableWidth, bottomDrawableHeight);
        }

        setCompoundDrawables(left, top, right, bottom);
    }

    /*
     * 代码中动态设置drawable的宽高度
     * */
    public void setDrawableSize(int width, int height, int position) {
        if (position == this.POSITION_LEFT) {
            leftDrawableWidth = width;
            leftDrawableHeight = height;
        }
        if (position == this.POSITION_TOP) {
            topDrawableWidth = width;
            topDrawableHeight = height;
        }
        if (position == this.POSITION_RIGHT) {
            rightDrawableWidth = width;
            rightDrawableHeight = height;
        }
        if (position == this.POSITION_BOTTOM) {
            bottomDrawableWidth = width;
            bottomDrawableHeight = height;
        }

        setCompoundDrawablesWithIntrinsicBounds(
                leftDrawable, topDrawable, rightDrawable, bottomDrawable);
    }


    public void changDrawableLeftIcon(int resId, int drawablePadding, int width, int height) {
        Drawable drawable = CompatUtils.getDrawable(resId);
        if (drawable == null) return;
        drawable.setBounds(0, 0, DensityUtils.dp2px(getContext(), width), DensityUtils.dp2px(getContext(), height));
        setCompoundDrawablePadding(DensityUtils.dp2px(getContext(), drawablePadding));
        setCompoundDrawables(drawable, null, null, null);
    }


    public void changDrawableTopIcon(int resId, int drawablePadding, int width, int height) {
        Drawable drawable = CompatUtils.getDrawable(resId);
        if (drawable == null) return;
        drawable.setBounds(0, 0, DensityUtils.dp2px(getContext(), width), DensityUtils.dp2px(getContext(), height));
        setCompoundDrawablePadding(DensityUtils.dp2px(getContext(), drawablePadding));
        setCompoundDrawables(null, drawable, null, null);
    }

    public void changDrawableRightIcon(int resId, int drawablePadding, int width, int height) {
        Drawable drawable = CompatUtils.getDrawable(resId);
        if (drawable == null) return;
        drawable.setBounds(0, 0, DensityUtils.dp2px(getContext(), width), DensityUtils.dp2px(getContext(), height));
        setCompoundDrawablePadding(DensityUtils.dp2px(getContext(), drawablePadding));
        setCompoundDrawables(null, null, drawable, null);
    }

    public void changDrawableBottomIcon(int resId, int drawablePadding, int width, int height) {
        Drawable drawable = CompatUtils.getDrawable(resId);
        if (drawable == null) return;
        drawable.setBounds(0, 0, DensityUtils.dp2px(getContext(), width), DensityUtils.dp2px(getContext(), height));
        setCompoundDrawablePadding(DensityUtils.dp2px(getContext(), drawablePadding));
        setCompoundDrawables(null, null, null, drawable);
    }


    public void setColorFilter(int color) {
        if (getCompoundDrawables()[0] != null) {
            Drawable left = getCompoundDrawables()[0];
            left = left.mutate();
            if (color == 0) {
                left.clearColorFilter();
            } else {
                left.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            setCompoundDrawables(left, null, null, null);
        } else if (getCompoundDrawables()[1] != null) {
            Drawable top = getCompoundDrawables()[1];
            top = top.mutate();
            if (color == 0) {
                top.clearColorFilter();
            } else {
                top.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            setCompoundDrawables(null, top, null, null);
        } else if (getCompoundDrawables()[2] != null) {
            Drawable right = getCompoundDrawables()[2];
            right = right.mutate();
            if (color == 0) {
                right.clearColorFilter();
            } else {
                right.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            setCompoundDrawables(null, null, right, null);
        } else if (getCompoundDrawables()[3] != null) {
            Drawable bottom = getCompoundDrawables()[3];
            bottom = bottom.mutate();
            if (color == 0) {
                bottom.clearColorFilter();
            } else {
                bottom.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            setCompoundDrawables(null, null, bottom, null);
        }
    }

}
