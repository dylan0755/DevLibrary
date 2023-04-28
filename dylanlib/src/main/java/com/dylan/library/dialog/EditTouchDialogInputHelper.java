package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.utils.EditTextUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.ToastUtils;
import com.dylan.library.widget.KeyPreImeEditText;
import com.dylan.library.widget.ObserveKeyBoardRootLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Author: Administrator
 * Date: 2020/9/16
 * Desc:
 */
public class EditTouchDialogInputHelper extends Dialog {
    KeyPreImeEditText dialogEditText;
    EditText srcEditText;
    Button btnOk;
    ObserveKeyBoardRootLayout observeKeyBoardRootLayout;
    private InputMethodManager mInputMethodManager;
    private boolean canConfirmWhileEmpty=true;
    private boolean useSrcTextWhenShow=true;
    private String emptyTipWhenConfirm;
    private CharSequence hint;
    private int  mInputType=-1;

    public EditTouchDialogInputHelper(@NonNull Context context) {
        super(context, R.style.DLDialogFullscreen);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            getWindow().setDimAmount(0);
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            getWindow().setGravity(Gravity.BOTTOM);
        }

        setContentView(R.layout.dl_dialog_common_edit_input);
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        observeKeyBoardRootLayout = findViewById(R.id.resizeLayout);
        dialogEditText = findViewById(R.id.edtInput);
        btnOk = findViewById(R.id.btnOk);
        setCanceledOnTouchOutside(false);
        findViewById(R.id.viewMask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        observeKeyBoardRootLayout.setOnKeyBoardObserverListener(new ObserveKeyBoardRootLayout.OnKeyBoardObserverListener() {
            @Override
            public void onShow() {

            }

            @Override
            public void onHide() {
                dismiss();
            }

        });
    }


    public void setCanConfirmWhileEmpty(boolean canConfirmWhileEmpty) {
        this.canConfirmWhileEmpty = canConfirmWhileEmpty;
    }

    public void setEmptyTipWhenConfirm(String emptyTipWhenConfirm) {
        this.emptyTipWhenConfirm = emptyTipWhenConfirm;
    }

    public void setUseSrcTextWhenShow(boolean useSrcTextWhenShow) {
        this.useSrcTextWhenShow = useSrcTextWhenShow;
    }

    public void setHint(CharSequence hint) {
        this.hint = hint;
    }

    public void setInputType(int inputType) {
        this.mInputType = inputType;
    }

    public void show(final EditText srcEditText) {
        EditTextUtils.disableImeShowWhileClick(srcEditText);
        this.srcEditText=srcEditText;
        super.show();
        //设置光标位置
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialogEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        dialogEditText.setMaxLines(Integer.MAX_VALUE);
                        if (EmptyUtils.isNotEmpty(hint))dialogEditText.setHint(hint);
                        if (useSrcTextWhenShow)dialogEditText.setText(srcEditText.getText());
                        if (mInputType!=-1)dialogEditText.setInputType(mInputType);
                        //处理焦点
                        dialogEditText.setFocusable(true);
                        dialogEditText.setFocusableInTouchMode(true);
                        dialogEditText.requestFocus();
                        dialogEditText.setSelection(srcEditText.length());
                        mInputMethodManager.showSoftInput(dialogEditText, 0);
                    }
                });

            }
        }, 100);
        //绑定监听
        addListener(srcEditText, canConfirmWhileEmpty, emptyTipWhenConfirm);


    }


    public EditText getSrcEditText() {
        return srcEditText;
    }
    

    private void addListener(final EditText srcEditText, final boolean canConfirmWhileEmpty, final String emptyTip) {
        dialogEditText.setOnKeyListener(new KeyPreImeEditText.OnKeyListener() {
            @Override
            public boolean onKeyPreIme(int keyCode, KeyEvent event, boolean isBack) {
                dismiss();
                return false;
            }
        });
        //完成按钮
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canConfirmWhileEmpty) {
                    if (EmptyUtils.isNotEmpty(emptyTip)) ToastUtils.show(emptyTip);
                    return;
                }

                if (mCallBack!=null)mCallBack.onCallBack();
                dismiss();
            }
        });



        //实时监听
        dialogEditText.addTextChangedListener(new EditTextUtils.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                srcEditText.setText(s);
                srcEditText.setSelection(dialogEditText.getSelectionStart());
            }
        });


    }


    public KeyPreImeEditText getDialogEditText() {
        return dialogEditText;
    }

    private OnCompleteCallBack mCallBack;
    public interface OnCompleteCallBack{
        void onCallBack();
    }


    public void addCompleteCallBack(OnCompleteCallBack callBack){
        mCallBack=callBack;
    }


    @Override
    public void dismiss() {
        View view = getCurrentFocus();
        if (view instanceof TextView) {
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
        observeKeyBoardRootLayout.setShowing(false);
        super.dismiss();
    }




}
