package com.dylan.mylibrary.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.dylan.common.BaseActivity;
import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.mylibrary.R;
import com.shouzhong.scanner.ScanBankCardActivity;

public class ScanBankCardDemoActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_scan_bank_card_demo;
    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
       findViewById(R.id.ivCamera).setOnClickListener(new SingleClickListener() {
           @Override
           public void onSingleClick(View v) {
               if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                   ActivityCompat.requestPermissions(ScanBankCardDemoActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                   return;
               }
               startActivityForResult(new Intent(v.getContext(), ScanBankCardActivity.class),100);
           }
       });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (resultCode == RESULT_OK) {
            String bankCardNo = data.getStringExtra("bankCardNo");
            EditText edtCardNo=findViewById(R.id.edtCardNo);
            edtCardNo.setText(bankCardNo);
            if (EmptyUtils.isNotEmpty(bankCardNo)) {
                edtCardNo.setSelection(bankCardNo.length());
            }
        }
    }
}
