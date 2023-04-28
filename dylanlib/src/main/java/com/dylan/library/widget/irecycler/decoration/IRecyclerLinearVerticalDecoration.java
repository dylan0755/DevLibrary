package com.dylan.library.widget.irecycler.decoration;//


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.State;
import android.util.Log;
import android.view.View;


/**
 * Author: Dylan
 * Date: 2019/2/27
 * Desc: 该类只适合 IRecyclerView,  绑定适配器需要调用 setIAdapter，而不是setAdapter
 * 如果是RecyclerView 则会导致当前屏幕的第一，第二和最后倒数第一，倒数第二都会没有分割线
 */


public class IRecyclerLinearVerticalDecoration extends ItemDecoration {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private static final String TAG = "DividerItem";
    private static final int[] ATTRS = new int[]{16843284};
    private Drawable mDivider;
    private int mOrientation;
    private final Rect mBounds = new Rect();
    private int mPaddingLeftPixel;
    private int mPaddingRightPixel;
    private boolean showBottomLine;

    public IRecyclerLinearVerticalDecoration(Context context, int orientation) {
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        this.mDivider = a.getDrawable(0);
        if (this.mDivider == null) {
            Log.w("DividerItem", "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration. Please set that attribute all call setDrawable()");
        }

        a.recycle();
        this.setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != 0 && orientation != 1) {
            throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        } else {
            this.mOrientation = orientation;
        }
    }

    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        } else {
            this.mDivider = drawable;
        }
    }


    public void setDrawable(@NonNull Drawable drawable, int paddingLeftPixel, int paddingRightPixel,
                            boolean showBottomLine) {
        this.showBottomLine = showBottomLine;
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        } else {
            this.mDivider = drawable;
            this.mPaddingLeftPixel = paddingLeftPixel;
            this.mPaddingRightPixel = paddingRightPixel;
        }
    }

    public void onDraw(Canvas c, RecyclerView parent, State state) {
        if (parent.getLayoutManager() != null && this.mDivider != null) {
            if (this.mOrientation == 1) {
                this.drawVertical(c, parent, state);
            } else {
                this.drawHorizontal(c, parent);
            }

        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent, State state) {
        canvas.save();
        int left;
        int right;
        if (parent.getClipToPadding()) {
//            left = parent.getPaddingLeft();
//            right = parent.getWidth() - parent.getPaddingRight();
            left = parent.getPaddingLeft() + mPaddingLeftPixel;
            right = parent.getWidth() - parent.getPaddingRight() - mPaddingRightPixel;
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
//            left = 0;
//            right = parent.getWidth();
            left = mPaddingLeftPixel;
            right = parent.getWidth() - mPaddingRightPixel;
        }

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; ++i) {
            View child = parent.getChildAt(i);
            int pos = parent.getChildLayoutPosition(child);
            //去掉头部Header，IRecyclerView header 包含两个布局
            if ((i == 0 && pos == 0) || (i == 1 && pos == 1)) {
                continue;
            }
            parent.getDecoratedBoundsWithMargins(child, this.mBounds);
            int bottom = this.mBounds.bottom + Math.round(child.getTranslationY());
            int top = bottom - this.mDivider.getIntrinsicHeight();
            this.mDivider.setBounds(left, top, right, bottom);
            this.mDivider.draw(canvas);
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, this.mBounds);
            int right = this.mBounds.right + Math.round(child.getTranslationX());
            int left = right - this.mDivider.getIntrinsicWidth();
            this.mDivider.setBounds(left, top, right, bottom);
            this.mDivider.draw(canvas);
        }
        canvas.restore();
    }

    /**
     * 该方法设置item之间的间距，如果是最后一行或者footer的话就设置间距为0，画了分割线也看不到
     */
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        if (this.mDivider == null) {
            outRect.set(0, 0, 0, 0);
        } else {
            if (this.mOrientation == 1) {
                if (isHeader(view, parent)||isLastRowOrFooter(view, parent)) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
                }
            } else {
                outRect.set(0, 0, this.mDivider.getIntrinsicWidth(), 0);
            }

        }
    }

    //头部Header，IRecyclerView header 包含两个布局
    private boolean isHeader(View view, RecyclerView parent){
        int position = parent.getChildAdapterPosition(view);
        if (position == 0||position == 1) return true;
        return false;
    }

    // 如果是最后一行或者有footerView
    private boolean isLastRowOrFooter(View view, RecyclerView parent) {
        int position = parent.getChildAdapterPosition(view);
        int count = parent.getAdapter().getItemCount();
        if (position == count - 1) return true;
        if (position == count - 2) return true;
        if (!showBottomLine) {//footerView 上面是否显示分割线
            if (position == count - 3) return true;
        }
        return false;
    }


}
