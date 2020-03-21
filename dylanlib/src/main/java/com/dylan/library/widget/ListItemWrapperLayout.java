package com.dylan.library.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.dylan.library.utils.Logger;
import com.dylan.library.utils.ToastUtils;

/**
 * Created by Dylan on 2017/3/22.
 */

public class ListItemWrapperLayout extends LinearLayout {

    public ListItemWrapperLayout(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
    }

    public ListItemWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    public ListItemWrapperLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }




    public interface onTouchEventCallBack{
        boolean onTouchEvent(MotionEvent event);
    }

    private onTouchEventCallBack mCallBack;
    public void setOnTouchEventCallBack(onTouchEventCallBack callBack){
        mCallBack=callBack;
    }
}
