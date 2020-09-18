package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.dylan.library.dialog.NoExtractUiEditDialogHelper;
import com.dylan.library.dialog.NoExtractUiEditPopHelper;
import com.dylan.mylibrary.R;

/**
 * Author: Administrator
 * Date: 2020/9/18
 * Desc:
 */
public class NoExtraUiEditTextDemoActivity extends AppCompatActivity {
    EditText edtDialog,edtPop;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noextrauidemo);
        edtDialog=findViewById(R.id.edtDialog);
        edtPop=findViewById(R.id.edtPop);


        NoExtractUiEditDialogHelper.buildEditTextDialog(edtDialog,true);
        NoExtractUiEditPopHelper.buildEditTextDialog(edtPop,true);
    }




}
