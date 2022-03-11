package com.dylan.library.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dylan.library.exception.ELog;

import java.lang.reflect.Method;

/**
 * Created by Dylan on 2017/3/10.
 */

public class EditTextUtils {


    public static void insert(EditText editText,CharSequence text){
        int start=editText.getSelectionStart();
        Editable edit = editText.getEditableText();//获取EditText的文字
        if (start < 0 || start >= edit.length() ){
            edit.append(text);
        }else{
            edit.insert(start,text);//光标所在位置插入文字
        }
    }




    public static void disableImeShowWhileClick(EditText editText){
        //禁止点击EditText自动弹出软键盘,由后面代码控制弹出
        try {
            Class<EditText> cls = EditText.class;
            Method setSoftInputShownOnFocus;
            setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setSoftInputShownOnFocus.setAccessible(true);
            setSoftInputShownOnFocus.invoke(editText, false);
        } catch (Exception e) {
            ELog.e(e);
        }
    }


    public static double parseDoubleValue(TextView textView) {
        double value = 0;
        try {
            value = Double.parseDouble(textView.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static double parseDoubleValue(Editable editable) {
        double value = 0;
        try {
            value = Double.parseDouble(editable.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    public static int parseIntValue(TextView textView) {
        int value = 0;
        try {
            value = Integer.parseInt(textView.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static int parseIntValue(Editable editable) {
        int value = 0;
        try {
            value = Integer.parseInt(editable.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    //保留小数点
    public static void keepDecimal(final EditText editText, final int decimalCount) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > decimalCount) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + decimalCount + 1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null == s) return;

                if (".".equals(s.toString())) {
                    editText.setText("0.");
                    editText.setSelection(2);
                    return;
                }

                //长度大于1且以0开头，就将第一位0去掉
                if (s.toString().length() > 1 && s.toString().startsWith("0") && !s.toString().startsWith("0.")) {
                    s.replace(0, 1, "");
                    return;
                }
            }
        });
    }


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
        if (editText != null) editText.setCursorVisible(false);
    }

    public static void showCursor(EditText editText) {
        if (editText != null) editText.setCursorVisible(true);
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


    public static void addDoneAction(EditText editText, final OnDoneActionCallBack callBack) {
        editText.setImeActionLabel("完成", EditorInfo.IME_ACTION_DONE);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);//优化,横屏全屏输入法不占满屏
        addOnEditorActionListener(editText, callBack);
    }


    public static void addSearchAction(EditText editText, final OnDoneActionCallBack callBack) {
        editText.setImeActionLabel("搜索", EditorInfo.IME_ACTION_SEARCH);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI);//优化,横屏全屏输入法不占满屏
        addOnEditorActionListener(editText, callBack);
    }


    public static void addSendAction(EditText editText, final OnDoneActionCallBack callBack) {
        editText.setImeActionLabel("发送", EditorInfo.IME_ACTION_SEND);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND | EditorInfo.IME_FLAG_NO_EXTRACT_UI);//优化,横屏全屏输入法不占满屏
        addOnEditorActionListener(editText, callBack);
    }


    private static void addOnEditorActionListener(EditText editText, final OnDoneActionCallBack callBack) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    SoftKeyboardUtils.hideSoftInput(v);
                    if (callBack != null) callBack.onKeyEvent(v, actionId, event);
                }
                return true;
            }
        });
    }


    public interface OnDoneActionCallBack {
        void onKeyEvent(TextView v, int actionId, KeyEvent event);
    }


}
