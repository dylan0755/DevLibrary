package com.dylan.library.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

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


    public static void hideCursor(EditText editText) {
        if (editText!=null)editText.setCursorVisible(false);
    }

    public static void showCursor(EditText editText){
        if (editText!=null)editText.setCursorVisible(true);
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



    public static void addDoneAction(EditText editText, final OnDoneActionCallBack callBack){
        editText.setImeActionLabel("完成", EditorInfo.IME_ACTION_DONE);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        addOnEditorActionListener(editText,callBack);
    }


    public static void addSearchAction(EditText editText, final OnDoneActionCallBack callBack){
        editText.setImeActionLabel("搜索",EditorInfo.IME_ACTION_SEARCH);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        addOnEditorActionListener(editText,callBack);
    }


    public static void addSendAction(EditText editText, final OnDoneActionCallBack callBack){
        editText.setImeActionLabel("发送",EditorInfo.IME_ACTION_SEND);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        addOnEditorActionListener(editText,callBack);
    }


    private static void addOnEditorActionListener(EditText editText, final OnDoneActionCallBack callBack){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        ||actionId==EditorInfo.IME_ACTION_SEND
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    SoftKeyboardUtils.hideSoftInput(v);
                    if (callBack!=null)callBack.onKeyEvent(v,actionId,event);
                }
                return true;
            }
        });
    }


    public interface OnDoneActionCallBack {
        void onKeyEvent(TextView v, int actionId, KeyEvent event);
    }



}
