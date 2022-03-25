package com.dylan.mylibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.dylan.mylibrary.R;

import butterknife.ButterKnife;

/**
 * Created by Dylan on 2018/7/21.
 */

public abstract class BaseDialog extends Dialog {

   protected abstract int getLayoutId();

    public BaseDialog(final Context context) {
        this(context,false);
    }

    public BaseDialog(final Context context, boolean isFullScreen) {
        super(context,isFullScreen? R.style.DLDialogFullscreen:R.style.DLCustomFloatingDialog);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
    }










}
