package com.dylan.library.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Author: Dylan
 * Date: 2021/1/20
 * Desc:
 */
public class CameraSurfaceView extends GLSurfaceView {



    public CameraSurfaceView(Context context) {
        super(context);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }


}
