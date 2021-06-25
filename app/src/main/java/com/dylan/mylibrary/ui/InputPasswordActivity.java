package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dylan.library.dialog.InputPassWordDialog;
import com.dylan.library.utils.ToastUtils;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2019/9/19
 * Desc:
 */
public class InputPasswordActivity extends AppCompatActivity {
    TextView tvInputPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputpassword);
        tvInputPassword = findViewById(R.id.tvInputPassword);

    }


    public void onShow(View view) {

        InputPassWordDialog dialog = new InputPassWordDialog.Builder(this)
                .setTitle("请输入资金密码")
                .setTip("为保证您的账号安全，请输入您的资金密码")
                .setOnInputReturnListener(new InputPassWordDialog.setOnPassWordInputListener() {
                    @Override
                    public void onInputFinish(String str) {
                        ToastUtils.show(str);
                    }
                }).create();
        dialog.show();
    }
}
