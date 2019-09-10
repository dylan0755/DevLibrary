package com.dylan.library.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dylan on 2017/3/10.
 */

public class EditTextUtils {


    public static void setGravityCenter(EditText editText) {
        if (editText == null) return;
        editText.setGravity(Gravity.CENTER);
        //针对努比亚手机
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
    }

    public static void showSoftInputAuto(final EditText editText) {
        if (editText == null) return;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                String textLength = editText.getText().toString();
                editText.setSelection(textLength.length());
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 100);

    }


    public static abstract class AfterTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public abstract void afterTextChanged(Editable s);
    }


}
