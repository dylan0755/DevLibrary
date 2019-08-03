package com.dylan.mylibrary.ui.loadingdialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.dylan.mylibrary.R;
import com.dylan.library.widget.LoadingDialog;


/**
 * Created by Dylan on 2017/1/15.
 */

public class LoadingDialogActivity extends Activity{
    Button btn_loading;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingdialog);
        btn_loading= (Button) findViewById(R.id.btn_loading);

        btn_loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog=new LoadingDialog(LoadingDialogActivity.this);
               //dialog.getBackgroundView().setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_loadingdialog));
               // dialog.setLoadingTipText("25132");
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                },5000);
            }
        });


    }
}
