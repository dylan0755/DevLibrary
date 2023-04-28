package com.dylan.mylibrary.ui.popwindow;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.dylan.library.widget.DLPopWindow;
import com.dylan.mylibrary.R;

/**
 * Author: Administrator
 * Date: 2020/8/7
 * Desc:
 */
public class PopWindowDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popwindow_demo);
        final Button button=findViewById(R.id.btnPop);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DLPopWindow popWindow=new DLPopWindow(PopWindowDemoActivity.this) {
                    @Override
                    public void initView() {
                      //    findViewById()
                    }

                    @Override
                    public int getLayoutId() {
                        return R.layout.pop_window_demo;
                    }
                };
                popWindow.showAsDropDown(button);

            }
        });
    }
}
