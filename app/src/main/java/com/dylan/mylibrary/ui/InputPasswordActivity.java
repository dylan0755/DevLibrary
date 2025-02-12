package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dylan.library.dialog.InputPassWordDialog;
import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;

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
                        Toaster.show(str);
                    }
                }).create();
        dialog.show();
    }
}
