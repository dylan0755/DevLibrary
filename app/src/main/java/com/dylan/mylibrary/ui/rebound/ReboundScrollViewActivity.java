package com.dylan.mylibrary.ui.rebound;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;

/**
 * Author: Dylan
 * Date: 2020/2/18
 * Desc:
 */
public class ReboundScrollViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebound_scrollview);
        findViewById(R.id.viewLast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toaster.show("被点击了");
            }
        });
    }
}
