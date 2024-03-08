package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.dylan.library.dialog.EditTouchDialogInputHelper;
import com.dylan.library.utils.Logger;
import com.dylan.library.widget.ObserveKeyBoardRootLayout;
import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;

/**
 * Author: Administrator
 * Date: 2020/9/18
 * Desc:
 */
public class EditTouchDialogInputDemoActivity extends AppCompatActivity {
    private ObserveKeyBoardRootLayout observeKeyBoardRootLayout;
    EditText edtDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observekeyboard);
        edtDialog=findViewById(R.id.edtDialog);
        observeKeyBoardRootLayout=findViewById(R.id.rootView);
        observeKeyBoardRootLayout.setOnKeyBoardObserverListener(new ObserveKeyBoardRootLayout.OnKeyBoardObserverListener() {
            @Override
            public void onShow() {
                Toaster.show("显示");
                Logger.e("显示");
            }

            @Override
            public void onHide() {
                Logger.e("隐藏");
                Toaster.show("隐藏");
            }


        });

        edtDialog.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()!=MotionEvent.ACTION_UP)return false;
                EditTouchDialogInputHelper inputHelper=new EditTouchDialogInputHelper(v.getContext());
                inputHelper.setHint(edtDialog.getHint());
                inputHelper.show(edtDialog);
                return true;
            }
        });
    }




}
