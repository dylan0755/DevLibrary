package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;

import com.dylan.library.R;

/**
 * Author: Dylan
 * Date: 2020/4/20
 * Desc:
 */
public class BottomSlideDialog extends Dialog {

    public BottomSlideDialog( Context context) {
        super(context, R.style.DLBottomSheetDialogStyle);
    }



    @Override
    public void show() {
        super.show();
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
    }

}
