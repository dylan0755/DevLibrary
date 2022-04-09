package com.dylan.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dylan.library.R;
import com.dylan.library.screen.ScreenUtils;

/**
 * Author: Administrator
 * Date: 2020/9/17
 * Desc:
 */
public class ResizableLinearLayout extends LinearLayout {
    private boolean isShowing;
    private int mHeight;
    private int heightOnKeyBoard;
    public ResizableLinearLayout(Context context) {
        super(context);
    }

    public ResizableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHeight= ScreenUtils.getScreenHeight(context);
    }

    public ResizableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeight= ScreenUtils.getScreenHeight(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.viewMask).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (observerListener!=null)observerListener.onTouchOutSide();
            }
        });
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
         if (heightOnKeyBoard <h){
             heightOnKeyBoard=h;
         }
        if (!isShowing&&h< mHeight-200){
            if (observerListener!=null)observerListener.onShow();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    isShowing=true;
                }
            },300);
        }else{
            if (isShowing){
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (observerListener!=null)observerListener.onHide();
                        isShowing=false;
                    }
                },100);

            }

        }
    }

    public void setShowing(boolean bl){
         isShowing=bl;
    }

    private OnKeyBoardObserverListener observerListener;
    public interface OnKeyBoardObserverListener{
        void onShow();
        void onHide();
        void onTouchOutSide();
    }

    public void setOnKeyBoardObserverListener(OnKeyBoardObserverListener listener){
        observerListener=listener;
    }

}
