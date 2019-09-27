package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dylan.library.R;


/**
 * Created by Dylan on 2017/11/20.
 */

public class LoadingDialog extends Dialog {
    private TextView tipTextView;
    private View backgroudView;
    private ProgressBar loadingBar;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.DLCustomDialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.dl_dialog_loading);
        loadingBar=findViewById(R.id.loadingBar);
        tipTextView=findViewById(R.id.tipTextView);
        backgroudView=findViewById(R.id.rootView);
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
        return backgroudView;
    }

    public TextView getLoadingTextView(){
        return tipTextView;
    }

    public ProgressBar getLoadingBar(){
        return loadingBar;
    }
}
