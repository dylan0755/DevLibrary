package com.dylan.library.widget;

import android.content.Context;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Author: Dylan
 * Date: 2021/03/23
 * Desc:
 */
public class LetterNumberEditText extends EditText {

    public LetterNumberEditText(Context context) {
        super(context);
        setDigits();
    }

    public LetterNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDigits();
    }


    private void setDigits(){
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }
}
