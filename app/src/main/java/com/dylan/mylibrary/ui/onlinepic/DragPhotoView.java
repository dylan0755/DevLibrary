package com.dylan.mylibrary.ui.onlinepic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.dylan.library.utils.Logger;

/**
 * Author: Dylan
 * Date: 2019/7/25
 * Desc:
 */

public class DragPhotoView extends PhotoView {
    private float downX;
    private float downY;


    public DragPhotoView(Context context) {
        super(context);
    }

    public DragPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void drag(){

    }
}
