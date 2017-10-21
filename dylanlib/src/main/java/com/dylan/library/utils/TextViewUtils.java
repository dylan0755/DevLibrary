package com.dylan.library.utils;

import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Dylan on 2017/10/20.
 */

public class TextViewUtils {

    public static void setMovementMethod(TextView textView){
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public static void setSupportMoveInScrollView(TextView textView){
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if(event.getAction()== MotionEvent.ACTION_MOVE){
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
    }
}
