package com.dylan.library.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Dylan on 2016/12/20.
 */

public class EditNumberHelper {
    private EditText mEditText;
    private int space=1;
    private int minNum=1;
    private int maxNum=0;

    public EditNumberHelper(int minNum,int maxNum,int space){
        this.space=space;
        this.minNum=minNum;
        this.maxNum=maxNum;
    }


    public EditNumberHelper(int minNum,int space){
        this.space=space;
        this.minNum=minNum;
    }


    public void restore(){
        String numStr=Integer.toString(minNum);
        mEditText.setText(numStr);
        mEditText.setSelection(numStr.length());
    }




    public void bindEditText(EditText editText){
        mEditText=editText;
        mEditText.addTextChangedListener(new TextObsevser());
        restore();
    }


    public int increase(){
        if (mEditText==null)return 0;
        String text=mEditText.getText().toString();
        if ("".equals(text)){
            text=Integer.toString(minNum);
            mEditText.setText(text);
            mEditText.setSelection(text.length());
            return minNum;
        }

        int num=Integer.parseInt(text);
        num=num+space;
        if (num>maxNum&&maxNum!=0)num=maxNum;
        String numstr=Integer.toString(num);
        mEditText.setText(numstr);
        mEditText.setSelection(numstr.length());
        return num;
    }


    public int reduce(){
        if (mEditText==null)return 0;
        String text=mEditText.getText().toString();
        if ("".equals(text)){
            text=Integer.toString(minNum);
            mEditText.setText(text);
            mEditText.setSelection(text.length());
            return minNum;
        }
        if (Integer.toString(minNum).equals(mEditText.getText().toString())) return minNum;
        int num=Integer.parseInt(text);
        num=num-space;
        String numstr=Integer.toString(num);
        mEditText.setText(numstr);
        mEditText.setSelection(numstr.length());
        return num;
    }


    class TextObsevser implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
                        if (s.toString().length()!=0){
                            int num=Integer.parseInt(s.toString());
                            if (maxNum!=0&&num>maxNum){
                                num=maxNum;
                                String numStr=Integer.toString(num);
                                mEditText.setText(numStr);
                                mEditText.setSelection(numStr.length());
                            }
                           if (mListener!=null)mListener.onTextChanged(num);
                        }else{
                            if (mListener!=null)mListener.onTextChanged(minNum);
                        }

        }
    }

    private TextChangedListener mListener;
    public interface TextChangedListener{
        void onTextChanged(int numStr);
    }

    public void setTextChangedListener(TextChangedListener listener){
        mListener=listener;
    }
}
