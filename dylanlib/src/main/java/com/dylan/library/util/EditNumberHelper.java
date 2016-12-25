package com.dylan.library.util;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Dylan on 2016/12/20.
 */

/**
 * //在实现类的回调方法onNumberChanged中不能EditText不能调用setText()；否则死循环
 */
public class EditNumberHelper {
    private EditText mEditText;
    private int space = 1;
    private int minNum = 1;
    private int maxNum = 100000000;//默认最大数值一个亿
    private String maxValueTip;
    private ToastUtil mUtil;

    public EditNumberHelper(Activity activity, final int minNum, int maxNum, int space) {
        this.space = space;
        if (minNum != 0) this.minNum = minNum;
        if (maxNum != 0) this.maxNum = maxNum;

        SoftKeyboardUtil.observeSoftKeyboard(activity, new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean visible) {
                if (!visible) {
                    if (mEditText != null && mEditText.getText().toString().isEmpty()) {
                        restore();
                    }
                }
            }
        });
    }


    public EditNumberHelper(Activity activity, int minNum, int space) {
        this(activity, minNum, 0, space);

    }


    public EditNumberHelper(Activity activity) {
        this(activity, 1, 0, 1);
    }


    public void restore() {
        String numStr = Integer.toString(minNum);
        mEditText.setText(numStr);
        mEditText.setSelection(numStr.length());
    }


    public void bindEditText(EditText editText) {
        mEditText = editText;
        if (mEditText == null) return;
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mEditText.addTextChangedListener(new TextObsevser());
        InputFilter[] filters = {new InputFilter.LengthFilter(10)};
        editText.setFilters(filters);
        restore();
    }

    //超过最大值得提示文本
    public void setMaxValueTip(String tip) {
        maxValueTip = tip;
    }



    public void increase() {
        if (mEditText == null) return;
        String text = mEditText.getText().toString();
        if ("".equals(text)) {
            text = Integer.toString(minNum);
            mEditText.setText(text);
            mEditText.setSelection(text.length());
            return;
        }

        int num = Integer.parseInt(text);
        num = num + space;
        if (num > maxNum && maxNum != 0) num = maxNum;
        String numstr = Integer.toString(num);
        mEditText.setText(numstr);
        mEditText.setSelection(numstr.length());

    }


    public void reduce() {
        if (mEditText == null) return;
        String text = mEditText.getText().toString();
        if ("".equals(text)) {
            text = Integer.toString(minNum);
            mEditText.setText(text);
            mEditText.setSelection(text.length());
            return;
        }
        if (Integer.toString(minNum).equals(mEditText.getText().toString())) return;
        int num = Integer.parseInt(text);
        num = num - space;
        String numstr = Integer.toString(num);
        mEditText.setText(numstr);
        mEditText.setSelection(numstr.length());

    }


    class TextObsevser implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() != 0) {
                int num = Integer.parseInt(s.toString());
                if (maxNum != 0 && num > maxNum) {
                    num = maxNum;
                    String numStr = Integer.toString(num);
                    mEditText.setText(numStr);
                    mEditText.setSelection(numStr.length());
                    if (maxValueTip != null) {
                        if (mUtil == null) mUtil = new ToastUtil();
                        mUtil.toToast(mEditText.getContext(), maxValueTip);
                    }

                }
                if (mListener != null) mListener.onNumberChanged(num);
            } else {
                if (mListener != null) mListener.onNumberChanged(minNum);
            }


        }
    }

    private InputChangedListener mListener;

    public interface InputChangedListener {
        void onNumberChanged(int numStr);   //在实现类的回调方法onNumberChanged中不能EditText不能调用setText()；否则死循环
    }

    public void setInputListener(InputChangedListener listener) {
        mListener = listener;
    }


    public class ToastUtil {
        private Toast toast;

        public void toToast(Context context, String str) {
            if (toast == null) {
                toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            } else {
                toast.setText(str);
            }
            toast.show();
        }


    }


}
