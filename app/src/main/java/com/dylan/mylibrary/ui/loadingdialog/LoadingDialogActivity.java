package com.dylan.mylibrary.ui.loadingdialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.dylan.library.dialog.LoadingDialog;
import com.dylan.library.screen.NavBarUtils;
import com.dylan.mylibrary.R;


/**
 * Created by Dylan on 2017/1/15.
 */

public class LoadingDialogActivity extends Activity{
    Button btn_loading;
    private LoadingDialog dialog1;
    private LoadingDialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingdialog);
        NavBarUtils.hideNavBar(getWindow());
        btn_loading= (Button) findViewById(R.id.btn_loading);

        btn_loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1 =new LoadingDialog.Builder(LoadingDialogActivity.this).build();
               dialog1.getBackgroundView().setBackgroundDrawable(getResources().getDrawable(R.drawable.dl_shape_loadingdialog));
                dialog1.setLoadingTipText("25132");
                dialog1.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                    }
                },5000);
            }
        });

        findViewById(R.id.btn_loading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2=new LoadingDialog.Builder(v.getContext())
                .setLoadingTipText("加载中")
                .hideNavBar(true).build();
                dialog2.show();
            }
        });

    }
}
