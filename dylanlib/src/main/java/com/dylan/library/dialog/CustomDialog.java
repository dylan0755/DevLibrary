package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import com.dylan.library.R;
import com.dylan.library.screen.NavBarUtils;

/**
 * Author: Dylan
 * Date: 2020/4/20
 * Desc:
 */
public abstract class CustomDialog extends Dialog {
    protected abstract int getLayoutId();
    private boolean hideNavWhileShowing;//弹出对话框是否隐藏虚拟按键 达到全屏效果

    public CustomDialog(Context context) {
        super(context, R.style.DLCustomDialog);
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
}
