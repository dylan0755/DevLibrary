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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dylan.library.R;


/**
 * Created by Dylan on 2017/11/20.
 */

public class LoadingDialog extends Dialog {
    private TextView tipTextView;
    private View backgroundView;
    private ProgressBar loadingBar;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.progress_loading);
        initContentView(context);

    }




    private void initContentView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.dialog_loading, null, false);
        backgroundView=contentView.findViewById(R.id.dialog_loading_view);
//        if (backgroundView!=null){
//            Drawable drawable=backgroundView.getBackground();
//            if (drawable!=null)drawable.setAlpha(230);
//        }
        tipTextView = (TextView) contentView.findViewById(R.id.tipTextView);
        loadingBar=contentView.findViewById(R.id.loadingBar);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(contentView, new LinearLayout.LayoutParams(-1, -1));
        setDialogWindowParam(this, Gravity.CENTER, com.dylan.library.R.style.anim_popWindowStyle);

    }

    public void setLoadingTipText(String tipText) {
        tipTextView.setText(tipText);
        if (tipText!=null&&!tipText.isEmpty()){
            tipTextView.setVisibility(View.VISIBLE);
        }else{
            tipTextView.setVisibility(View.GONE);
        }

    }

    public View getBackgroundView(){
        return backgroundView;
    }

    public TextView getLoadingTextView(){
        return tipTextView;
    }

    public ProgressBar getLoadingBar(){
        return loadingBar;
    }

    private static void setDialogWindowParam(Dialog dialog, int gravity, int windowAnimStyleId) {
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
