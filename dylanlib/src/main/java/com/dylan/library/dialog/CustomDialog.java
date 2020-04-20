package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import com.dylan.library.R;

/**
 * Author: Dylan
 * Date: 2020/4/20
 * Desc:
 */
public abstract class CustomDialog extends Dialog {
    protected abstract int getLayoutId();

    public CustomDialog(Context context) {
        super(context, R.style.DLCustomDialog);
        setContentView(getLayoutId());
    }

}
