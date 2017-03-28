package com.dylan.library.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Dylan on 2017/3/10.
 */

public class EditTextUtils {

    public static void showSoftInputAuto(final EditText editText){
        if (editText==null)return;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                String textLength=editText.getText().toString();
                editText.setSelection(textLength.length());
                InputMethodManager inputManager =
                        (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        },100);
    }

}
