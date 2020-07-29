package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dylan.library.utils.ToastUtils;
import com.dylan.library.widget.ArcMenu;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2020/7/29
 * Desc:
 */
public class ArcMenuActivity extends AppCompatActivity {
    ArcMenu arcMenu;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcmenu);
        arcMenu=findViewById(R.id.arcMenu);


        arcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                ToastUtils.show(""+pos);
            }
        });
    }
}
