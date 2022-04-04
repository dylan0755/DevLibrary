package com.dylan.library.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.dylan.library.utils.DensityUtils;

/**
 * Author: Dylan
 * Date: 2021/1/20
 * Desc:
 */
public class CameraGLSurfaceView extends GLSurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mSurfaceHolder;


    public CameraGLSurfaceView(Context context) {
        super(context);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        mSurfaceHolder=holder;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (super.onTouchEvent(event)){
            return true;
        }
        if (event.getPointerCount() == 1 && event.getAction() == MotionEvent.ACTION_DOWN) {
            float rawX = event.getX();
            float rawY = event.getY();
            int focusRectSize = DensityUtils.dp2px(getContext(),70);
            if (mFocusCallBack!=null)mFocusCallBack.onTouchFocus(rawX,rawY,focusRectSize);
            return true;
        }

        return super.onTouchEvent(event);
    }

    private OnTouchFocusCallBack mFocusCallBack;
    public interface OnTouchFocusCallBack{
        void onTouchFocus(float rawX,float rawY,int focusRectSize );
    }

    public void setOnTouchFocusCallBack(OnTouchFocusCallBack callBack){
        mFocusCallBack=callBack;
    }
}
