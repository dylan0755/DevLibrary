package com.dylan.library.widget;

import android.annotation.TargetApi;
import android.content.Context;

import androidx.core.view.MarginLayoutParamsCompat;
import androidx.core.view.ViewCompat;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dylan.library.adapter.LayoutInflaterAdapter;

/**
 * Author: Dylan
 * Date: 2021/11/29
 * Desc:
 */
public class FlowLayout extends ViewGroup {
    private int lineSpacing;
    private int itemSpacing;
    private boolean singleLine;


    public FlowLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.singleLine = false;
    }

    @TargetApi(21)
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.singleLine = false;
    }


    public int getLineSpacing() {
        return this.lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public int getItemSpacing() {
        return this.itemSpacing;
    }

    public void setItemSpacing(int itemSpacing) {
        this.itemSpacing = itemSpacing;
    }

    public boolean isSingleLine() {
        return this.singleLine;
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int maxWidth = widthMode != MeasureSpec.AT_MOST && widthMode != MeasureSpec.EXACTLY ? Integer.MAX_VALUE : width;
        int childLeft = this.getPaddingLeft();
        int childTop = this.getPaddingTop();
        int childBottom = childTop;
        int maxChildRight = 0;
        int maxRight = maxWidth - this.getPaddingRight();

        int finalWidth;
        for (finalWidth = 0; finalWidth < this.getChildCount(); ++finalWidth) {
            View child = this.getChildAt(finalWidth);
            if (child.getVisibility() != GONE) {
                this.measureChild(child, widthMeasureSpec, heightMeasureSpec);
                LayoutParams lp = child.getLayoutParams();
                int leftMargin = 0;
                int rightMargin = 0;
                if (lp instanceof MarginLayoutParams) {
                    MarginLayoutParams marginLp = (MarginLayoutParams) lp;
                    leftMargin += marginLp.leftMargin;
                    rightMargin += marginLp.rightMargin;
                }

                int childRight = childLeft + leftMargin + child.getMeasuredWidth();
                if (childRight > maxRight && !this.isSingleLine()) {
                    childLeft = this.getPaddingLeft();
                    childTop = childBottom + this.lineSpacing;
                }

                childRight = childLeft + leftMargin + child.getMeasuredWidth();
                childBottom = childTop + child.getMeasuredHeight();
                if (childRight > maxChildRight) {
                    maxChildRight = childRight;
                }

                childLeft += leftMargin + rightMargin + child.getMeasuredWidth() + this.itemSpacing;
            }
        }

        finalWidth = getMeasuredDimension(width, widthMode, maxChildRight);
        int finalHeight = getMeasuredDimension(height, heightMode, childBottom);
        this.setMeasuredDimension(finalWidth, finalHeight);
    }

    private static int getMeasuredDimension(int size, int mode, int childrenEdge) {
        switch (mode) {
            case MeasureSpec.AT_MOST:
                return Math.min(childrenEdge, size);
            case MeasureSpec.EXACTLY:
                return size;
            default:
                return childrenEdge;
        }
    }

    protected void onLayout(boolean sizeChanged, int left, int top, int right, int bottom) {
        if (this.getChildCount() != 0) {
            boolean isRtl = ViewCompat.getLayoutDirection(this) == 1;
            int paddingStart = isRtl ? this.getPaddingRight() : this.getPaddingLeft();
            int paddingEnd = isRtl ? this.getPaddingLeft() : this.getPaddingRight();
            int childStart = paddingStart;
            int childTop = this.getPaddingTop();
            int childBottom = childTop;
            int maxChildEnd = right - left - paddingEnd;

            for (int i = 0; i < this.getChildCount(); ++i) {
                View child = this.getChildAt(i);
                if (child.getVisibility() != GONE) {
                    LayoutParams lp = child.getLayoutParams();
                    int startMargin = 0;
                    int endMargin = 0;
                    if (lp instanceof MarginLayoutParams) {
                        MarginLayoutParams marginLp = (MarginLayoutParams) lp;
                        startMargin = MarginLayoutParamsCompat.getMarginStart(marginLp);
                        endMargin = MarginLayoutParamsCompat.getMarginEnd(marginLp);
                    }

                    int childEnd = childStart + startMargin + child.getMeasuredWidth();
                    if (!this.singleLine && childEnd > maxChildEnd) {
                        childStart = paddingStart;
                        childTop = childBottom + this.lineSpacing;
                    }

                    childEnd = childStart + startMargin + child.getMeasuredWidth();
                    childBottom = childTop + child.getMeasuredHeight();
                    if (isRtl) {
                        child.layout(maxChildEnd - childEnd, childTop, maxChildEnd - childStart - startMargin, childBottom);
                    } else {
                        child.layout(childStart + startMargin, childTop, childEnd, childBottom);
                    }

                    childStart += startMargin + endMargin + child.getMeasuredWidth() + this.itemSpacing;
                }
            }

        }
    }


    public <T> void setAdapter(LayoutInflaterAdapter<T> layoutInflaterAdapter) {
        layoutInflaterAdapter.bindViewGroup(this);
    }


}
