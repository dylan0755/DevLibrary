package com.dylan.library.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.dylan.library.widget.shape.ShapeRelativeLayout;

/**
 * Author: Dylan
 * Date: 2021/04/02
 * Desc:
 */
public class DragScaleLayout extends ShapeRelativeLayout {
    private DragScaleHelper dispatchHelper;
    private boolean allowDragAndScale;
    private boolean allowDrag;
    private boolean allowScale;


    public DragScaleLayout(Context context) {
        this(context,null);

    }

    public boolean isAllowDragAndScale() {
        return allowDragAndScale;
    }

    public DragScaleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        dispatchHelper=new DragScaleHelper(context,this);

    }


    public void setAllowDragAndScale(boolean allowDragAndScale){
        this.allowDragAndScale = allowDragAndScale;
        dispatchHelper.setAllowDragAndScale(allowDragAndScale);
    }

    public void setAllowDrag(boolean allowDrag) {
        this.allowDrag = allowDrag;
        dispatchHelper.setAllowDrag(allowDrag);
    }

    public void setAllowScale(boolean allowScale) {
        this.allowScale = allowScale;
        dispatchHelper.setAllowScale(allowScale);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mOnDispatchTouchEventListener !=null) mOnDispatchTouchEventListener.dispatchTouchEvent(ev);
        if (allowDragAndScale||allowDrag||allowScale){
            dispatchHelper.dispatchTouchEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    private OnDispatchTouchEventListener mOnDispatchTouchEventListener;
    public interface OnDispatchTouchEventListener {
        void dispatchTouchEvent(MotionEvent ev);
    }


    public void setOnDispatchTouchEventListener(OnDispatchTouchEventListener callBack){
        mOnDispatchTouchEventListener =callBack;
    }






}
