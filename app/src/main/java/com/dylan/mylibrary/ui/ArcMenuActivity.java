package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.dylan.library.widget.ArcMenu;
import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;

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
                Toaster.show(""+pos);
            }
        });
    }
}
