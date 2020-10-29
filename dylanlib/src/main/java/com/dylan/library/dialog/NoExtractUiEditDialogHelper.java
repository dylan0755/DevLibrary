package com.dylan.library.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.utils.EditTextUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.SoftKeyboardUtils;
import com.dylan.library.utils.ToastUtils;
import com.dylan.library.widget.KeyPreImeEditText;
import com.dylan.library.widget.ResizableLinearLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Author: Administrator
 * Date: 2020/9/16
 * Desc:
 */
public class NoExtractUiEditDialogHelper extends CustomFloatingDialog {
    KeyPreImeEditText dialogEditText;
    Button btnOk;
    ResizableLinearLayout resizeLayout;
    private InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getWindow() != null)
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    @Override
    protected int getLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.dl_dialog_noextraui;
    }

    public NoExtractUiEditDialogHelper(@NonNull Context context) {
        super(context);
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        resizeLayout = findViewById(R.id.resizeLayout);
        dialogEditText = findViewById(R.id.edtInput);
        btnOk = findViewById(R.id.btnOk);
        setCanceledOnTouchOutside(false);
        if (getWindow() != null) {
            getWindow().setDimAmount(0);
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.BOTTOM);
        }
        resizeLayout.setOnKeyBoardObserverListener(new ResizableLinearLayout.OnKeyBoardObserverListener() {
            @Override
            public void onShow() {

            }

            @Override
            public void onHide() {
                dismiss();
            }

            @Override
            public void onTouchOutSide() {
                dismiss();
            }
        });

    }


    public void show(final EditText srcEditText) {
        show(srcEditText, true, "");
    }


    public void show(final EditText srcEditText, final boolean canConfirmWhileEmpty, final String emptyTip) {
        EditTextUtils.disableImeShowWhileClick(srcEditText);
        //处理焦点
        dialogEditText.requestFocus();
        dialogEditText.setFocusable(true);
        dialogEditText.setFocusableInTouchMode(true);

        //填充文本
        dialogEditText.setText(srcEditText.getText());
        dialogEditText.setInputType(srcEditText.getInputType());
        dialogEditText.setFilters(srcEditText.getFilters());
        srcEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        dialogEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        //设置光标位置
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialogEditText.setSelection(srcEditText.getSelectionEnd());
            }
        }, 100);

        //绑定监听
        addListener(srcEditText, canConfirmWhileEmpty, emptyTip);


        super.show();
        //弹出软键盘
        srcEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputMethodManager.showSoftInput(dialogEditText, 0);
            }
        }, 100);


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
                dismiss();
            }
        });

        //键盘上的完成按钮
        dialogEditText.setImeActionLabel("完成", EditorInfo.IME_ACTION_DONE);
        dialogEditText.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        dialogEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    if (!canConfirmWhileEmpty) {
                        if (EmptyUtils.isNotEmpty(emptyTip)) ToastUtils.show(emptyTip);
                        return true;
                    } else {
                        SoftKeyboardUtils.hideSoftInput(dialogEditText);
                    }
                }
                return true;
            }
        });

        //实时监听
        dialogEditText.addTextChangedListener(new EditTextUtils.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String finalText = s.toString();
                srcEditText.setText(finalText);
                srcEditText.setSelection(dialogEditText.getSelectionStart());
            }
        });


    }





    public EditText getDialogEditText() {
        return dialogEditText;
    }

    public Button getDialogButton() {
        return btnOk;
    }


    @Override
    public void dismiss() {
        View view = getCurrentFocus();
        if (view instanceof TextView) {
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
        resizeLayout.setShowing(false);
        super.dismiss();
    }


    public static NoExtractUiEditDialogHelper buildEditTextDialog(final EditText editText, final boolean useHint) {
        return NoExtractUiEditDialogHelper.buildEditTextDialog(editText,useHint,true,"");
    }

    public static NoExtractUiEditDialogHelper buildEditTextDialog(final EditText editText) {
        return NoExtractUiEditDialogHelper.buildEditTextDialog(editText,false);
    }

    public static NoExtractUiEditDialogHelper buildEditTextDialog(final EditText editText, final boolean useHint, final boolean canConfirmWhileEmpty, final String emptyTip) {
        final NoExtractUiEditDialogHelper dialog = new NoExtractUiEditDialogHelper(editText.getContext());
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    dialog.show(editText, canConfirmWhileEmpty, emptyTip);
                    if (useHint) dialog.getDialogEditText().setHint(editText.getHint());
                }
                return false;
            }
        });

        return dialog;
    }

}
