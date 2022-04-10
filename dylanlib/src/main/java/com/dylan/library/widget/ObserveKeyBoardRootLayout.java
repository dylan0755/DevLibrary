package com.dylan.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dylan.library.R;
import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.Logger;

/**
 * Author: Administrator
 * Date: 2020/9/17
 * Desc: 不管Activity还是Dialog 使用要配置：getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
 *
 */
public class ObserveKeyBoardRootLayout extends LinearLayout {
    private boolean isShowing;
    private int mHeight;
    private int heightOnKeyBoard;
    public ObserveKeyBoardRootLayout(Context context) {
        super(context);
    }

    public ObserveKeyBoardRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHeight= ScreenUtils.getScreenHeight(context);
    }

    public ObserveKeyBoardRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeight= ScreenUtils.getScreenHeight(context);
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
    }

    public void setOnKeyBoardObserverListener(OnKeyBoardObserverListener listener){
        observerListener=listener;
    }

}
