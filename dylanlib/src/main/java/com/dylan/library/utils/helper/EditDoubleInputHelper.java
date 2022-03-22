package com.dylan.library.utils.helper;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.dylan.library.utils.MathUtils;
import com.dylan.library.utils.SoftKeyboardUtils;

/**
 * Author: Dylan
 * Date: 2022/01/04
 * Desc:
 */
public class EditDoubleInputHelper {
    private EditText mEditText;
    private double space;
    private double minNum;
    private double maxNum=100000000;
    private String maxValueTip;
    private EditDoubleInputHelper.ToastUtil mUtil;
    private EditDoubleInputHelper.InputChangedListener mListener;

    public EditDoubleInputHelper(Activity activity, double minNum, double maxNum, double space) {
        this.minNum = minNum;
        if (maxNum!=0){
            this.maxNum = maxNum;
        }

        this.space = space;


        SoftKeyboardUtils.observeSoftKeyboard(activity, new SoftKeyboardUtils.OnSoftKeyboardChangeListener() {
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean visible) {
                if (!visible && EditDoubleInputHelper.this.mEditText != null && EditDoubleInputHelper.this.mEditText.getText().toString().isEmpty()) {
                    EditDoubleInputHelper.this.restore();
                }

            }
        });
    }

    public EditDoubleInputHelper(Activity activity, double minNum, double space) {
        this(activity, minNum, 0, space);
    }

    public EditDoubleInputHelper(Activity activity) {
        this(activity, 1, 0, 1);
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public void restore() {
        String numStr = Double.toString(this.minNum);
        this.mEditText.setText(numStr);
        this.mEditText.setSelection(numStr.length());
    }

    public void bindEditText(EditText editText) {
        this.mEditText = editText;
        if (this.mEditText != null) {
            this.mEditText.addTextChangedListener(new EditDoubleInputHelper.TextObsevser());
            InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(10)};
            //editText.setFilters(filters);
            this.restore();
        }
    }

    public void setMaxValueTip(String tip) {
        this.maxValueTip = tip;
    }

    public void increase() {
        if (this.mEditText != null) {
            String text = this.mEditText.getText().toString();
            if (text.startsWith("."))text="0"+text;
            if ("".equals(text)) {
                text = Double.toString(this.minNum);
                this.mEditText.setText(text);
                this.mEditText.setSelection(text.length());
            } else {
                double num= MathUtils.add(text,""+this.space);
                if (num > this.maxNum && this.maxNum != 0) {
                    num = this.maxNum;
                }

                String numstr = Double.toString(num);
                this.mEditText.setText(numstr);
                this.mEditText.setSelection(numstr.length());
            }
        }
    }

    public void reduce() {
        if (this.mEditText != null) {
            String text = this.mEditText.getText().toString();
            if (text.startsWith("."))text="0"+text;
            if ("".equals(text)) {
                text = Double.toString(this.minNum);
                this.mEditText.setText(text);
                this.mEditText.setSelection(text.length());
            } else if (!Double.toString(this.minNum).equals(this.mEditText.getText().toString())) {
                if (text.startsWith("."))text="0"+text;
                double num = MathUtils.subtract(text,""+this.space);
                String numstr = Double.toString(num);
                this.mEditText.setText(numstr);
                this.mEditText.setSelection(numstr.length());
            }
        }
    }

    public void setInputListener(EditDoubleInputHelper.InputChangedListener listener) {
        this.mListener = listener;
    }

    private class ToastUtil {
        private Toast toast;

        private ToastUtil() {
        }

        public void toToast(Context context, String str) {
            if (this.toast == null) {
                this.toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            } else {
                this.toast.setText(str);
            }

            this.toast.show();
        }
    }

    public interface InputChangedListener {
        void onNumberChanged(double var1);
    }

    class TextObsevser implements TextWatcher {
        TextObsevser() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (s.toString().length()==1&&".".equals(s.toString())){
                mEditText.setText("0.");
                mEditText.setSelection(2);
                return;
            }
            if (s.toString().length() != 0) {
                double num = Double.parseDouble(s.toString());

                if (EditDoubleInputHelper.this.maxNum != 0 && num > EditDoubleInputHelper.this.maxNum) {
                    num = EditDoubleInputHelper.this.maxNum;
                    String numStr = Double.toString(num);
                    EditDoubleInputHelper.this.mEditText.setText(numStr);
                    EditDoubleInputHelper.this.mEditText.setSelection(numStr.length());
                    if (EditDoubleInputHelper.this.maxValueTip != null) {
                        if (EditDoubleInputHelper.this.mUtil == null) {
                            EditDoubleInputHelper.this.mUtil = EditDoubleInputHelper.this.new ToastUtil();
                        }

                        EditDoubleInputHelper.this.mUtil.toToast(EditDoubleInputHelper.this.mEditText.getContext(), EditDoubleInputHelper.this.maxValueTip);
                    }
                }

                if (num!=0&& EditDoubleInputHelper.this.minNum!=0&&num<minNum){
                    num = EditDoubleInputHelper.this.minNum;
                    String numStr = Double.toString(num);
                    EditDoubleInputHelper.this.mEditText.setText(numStr);
                    EditDoubleInputHelper.this.mEditText.setSelection(numStr.length());
                    if (EditDoubleInputHelper.this.maxValueTip != null) {
                        if (EditDoubleInputHelper.this.mUtil == null) {
                            EditDoubleInputHelper.this.mUtil = EditDoubleInputHelper.this.new ToastUtil();
                        }
                        EditDoubleInputHelper.this.mUtil.toToast(EditDoubleInputHelper.this.mEditText.getContext(), EditDoubleInputHelper.this.maxValueTip);
                    }
                }

                if (EditDoubleInputHelper.this.mListener != null) {
                    EditDoubleInputHelper.this.mListener.onNumberChanged(num);
                }
            } else if (EditDoubleInputHelper.this.mListener != null) {
               // EditDoubleHelper.this.mListener.onNumberChanged(EditDoubleHelper.this.minNum);
            }

        }
    }
}
