package com.dylan.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Author: Administrator
 * Date: 2020/9/17
 * Desc: 当软键盘显示的时候，点击返回按钮 在 Activity 和 Dialog 中是监听不到的，所以要在EditText中监听
 * Ime 为 Input Method Editor的缩写
 */
public class KeyPreImeEditText extends EditText {
    private OnKeyListener keyListener;

    public KeyPreImeEditText(Context context) {
        super(context);
    }

    public KeyPreImeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyPreImeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyListener != null) {
            return keyListener.onKeyPreIme(keyCode, event, keyCode == KeyEvent.KEYCODE_BACK);
        }
        return false;
    }


    public interface OnKeyListener {
        boolean onKeyPreIme(int keyCode, KeyEvent event, boolean isBack);
    }

    public void setOnKeyListener(OnKeyListener listener) {
        keyListener = listener;
    }

}
