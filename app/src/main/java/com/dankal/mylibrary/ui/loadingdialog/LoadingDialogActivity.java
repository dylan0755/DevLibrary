package com.dankal.mylibrary.ui.loadingdialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.dankal.mylibrary.R;
import com.dylan.library.widget.DialogUtils;

/**
 * Created by Dylan on 2017/1/15.
 */

public class LoadingDialogActivity extends Activity{
    Button btn_loading;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingdialog);
        btn_loading= (Button) findViewById(R.id.btn_loading);

        btn_loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog=DialogUtils.createLoadingDialog(v.getContext(),"加载中...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtils.closeDialog(dialog);
                    }
                },5000);
            }
        });


    }
}
