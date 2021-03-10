package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.screen.NavBarUtils;
import com.dylan.library.utils.SoftKeyboardUtils;

/**
 * Author: Dylan
 * Date: 2020/4/20
 * Desc:
 */
public abstract class CustomFloatingDialog extends Dialog {
    protected abstract int getLayoutId();
    private boolean hideNavWhileShowing;//弹出对话框是否隐藏虚拟按键 达到全屏效果

    public CustomFloatingDialog(Context context) {
        super(context, R.style.DLCustomFloatingDialog);
        setContentView(getLayoutId());
    }


    @Override
    public void show() {
        if (hideNavWhileShowing){
            NavBarUtils.setNotFocusableFlag(getWindow());
        }
        super.show();
        if (hideNavWhileShowing){
            NavBarUtils.hideNavBar(getWindow());
            NavBarUtils.clearNotFocusableFlag(getWindow());
        }
    }


    protected void hideNavBar(){
        hideNavWhileShowing=true;
    }


    @Override
    public void dismiss() {
        View view = getCurrentFocus();
        if(view instanceof TextView){
            SoftKeyboardUtils.hideSoftInput(view);
        }
        super.dismiss();
    }
}
