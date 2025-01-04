package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.utils.SoftKeyboardUtils;

/**
 * Author: Dylan
 * Date: 2020/4/20
 * Desc:
 */
public class BottomSlideDialog extends Dialog {

    public BottomSlideDialog( Context context) {
        super(context, R.style.DLBottomSheetDialogStyle);
        //设置以下参数后，子类设置  setStatusBarColor(nav_color); setNavigationBarColor(nav_color);才有效果
        Window window = getWindow();
        if (window != null) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }



    @Override
    public void show() {
        super.show();
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
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
