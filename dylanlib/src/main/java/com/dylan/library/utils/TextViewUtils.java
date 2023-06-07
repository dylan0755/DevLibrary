package com.dylan.library.utils;

import android.graphics.Paint;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by Dylan on 2017/10/20.
 */

public class TextViewUtils {

    public static void setMovementMethod(TextView textView){
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public static void setMovementMethodInScrollView(TextView textView){
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        setSupportMoveInScrollView(textView);
    }

    public static void setSupportMoveInScrollView(final TextView textView) {
        textView.setOnTouchListener(new View.OnTouchListener() {
            private float downY;
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    downY=event.getY();
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float deltaY=event.getY()-downY;
                    if (deltaY>0){//判断是否到顶
                        if (ViewUtils.isOnTop(textView)){
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                        }else{
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }else if (deltaY<0){//判断是否到底
                        if (ViewUtils.isOnBottom(textView)){
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                        }else{
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }else{
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    downY=0;
                }

                return false;
            }
        });
    }


    //中划线
    public static void setStrikeThruText(TextView textView,String text){
        textView.setText(text);
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    //中划线
    public static void setStrikeThruText(TextView textView){
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    //获取ScrollView 中TextView当前显示的首行
    public static int getVisibleFirstLineInScrollView(TextView textView, ScrollView scrollView){
        return textView.getLayout().getLineForVertical(scrollView.getScrollY());
    }

    //获取ScrollView 中TextView当前显示的末行
    public static int getVisibleEndLineInScrollView(TextView textView, ScrollView scrollView){
        return textView.getLayout().getLineForVertical(scrollView.getScrollY() + scrollView.getMeasuredHeight());
    }
}
