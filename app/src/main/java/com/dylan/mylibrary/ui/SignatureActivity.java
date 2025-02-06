package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.view.View;

import com.dylan.common.BaseActivity;
import com.dylan.library.widget.SignatureView;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2025/2/6
 * Desc:
 */
public class SignatureActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_signature;
    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        SignatureView signView=findViewById(R.id.signView);
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signView.clear();
            }
        });

        findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // signView.save();
            }
        });

    }
}
