package com.dylan.library.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dylan.library.R;


/**
 * Created by Dylan on 2017/11/20.
 */

public class LoadingDialog extends Dialog {
    private TextView tipTextView;


    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.progress_loading);
        initContentView(context);

    }




    private void initContentView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.dialog_loading, null, false);
        View backgroundView=contentView.findViewById(R.id.dialog_loading_view);
        if (backgroundView!=null){
            Drawable drawable=backgroundView.getBackground();
            if (drawable!=null)drawable.setAlpha(200);
        }
        tipTextView = (TextView) contentView.findViewById(R.id.tipTextView);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(contentView, new LinearLayout.LayoutParams(-1, -1));
        setDialogWindowParam(this, Gravity.CENTER, com.dylan.library.R.style.anim_popWindowStyle);

    }

    public void setMessage(String tipText) {
        tipTextView.setText(tipText);
    }


    public static void setDialogWindowParam(Dialog dialog, int gravity, int windowAnimStyleId) {
        if (dialog == null) return;
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(gravity);
        window.setAttributes(lp);
        window.setWindowAnimations(windowAnimStyleId);
    }


}
