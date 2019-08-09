package com.dylan.library.widget.sliding;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public class SlidingLayout extends FrameLayout {
    private boolean measured;
    private boolean ContentEmpty = true;
    private OffsetChangedListener offsetChangedListener;
    SlidingContentLayout contentLayout;

    public SlidingLayout(Context context) {
        this(context, null);
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() == 0) return;

        if (!measured) {
            post(new Runnable() {
                @Override
                public void run() {
                    addContentViewToScrollLayout();
                }
            });
            measured = true;
        }

    }

    private void addContentViewToScrollLayout() {
        int offsetY = getChildAt(0).getMeasuredHeight();
        int contentHeight = getMeasuredHeight(); //+ offsetY

        contentLayout = (SlidingContentLayout) getChildAt(1);
        if (contentLayout.getParent() != null) {
            ViewGroup vp = (ViewGroup) contentLayout.getParent();
            vp.removeView(contentLayout);
        }
        View offsetYView = new View(getContext());
        offsetYView.setClickable(false);
        LinearLayout.LayoutParams offsetLp = new LinearLayout.LayoutParams(getMeasuredWidth(), offsetY);
        offsetYView.setLayoutParams(offsetLp);


        LinearLayout wrapperLayout = new LinearLayout(getContext());
        wrapperLayout.setOrientation(LinearLayout.VERTICAL);
        wrapperLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, contentHeight));
        wrapperLayout.addView(offsetYView);


        contentLayout.setOffsetY(offsetY);
        contentLayout.setClickable(true);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, contentHeight));
        contentLayout.addOffsetChangedListener(offsetChangedListener);
        wrapperLayout.addView(contentLayout);

        addView(wrapperLayout);
    }


    public void slideDown() {
        contentLayout.slidedown();
    }


    public boolean isOnTop() {
        return contentLayout.isOnTop;
    }



    public void addOnOffsetChangedListener(OffsetChangedListener listener){
        offsetChangedListener=listener;
    }

}


