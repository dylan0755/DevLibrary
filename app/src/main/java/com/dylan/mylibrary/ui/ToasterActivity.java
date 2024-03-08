package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;


/**
 * Author: Dylan
 * Date: 2021/03/10
 * Desc:
 */
public class ToasterActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);
        findViewById(R.id.btnShort).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Toaster.show("短Toast");
            }
        });
        findViewById(R.id.btnShortCenter).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Toaster.showShort("centerToast");
            }
        });

        findViewById(R.id.btnLongToast).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Toaster.showLong("longToast");
            }
        });

        findViewById(R.id.btnSubThread).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.showLong("子线程中弹出");
                    }
                }).start();

            }
        });
    }



}