package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.dylan.library.widget.AutoSplitTextView;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2021/06/10
 * Desc:
 */
public class AutoSplitTextViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autosplittextview);

        TextView textView=findViewById(R.id.tvNormal);
        AutoSplitTextView autoSplitTextView=findViewById(R.id.tvAutoSplit);

        textView.setText("本文地址http://www.cnblogs.com/goagent/p/5159125.html本文地址啊本文。地址。啊http://www.cnblogs.com/goagent/p/5159125.html");

        autoSplitTextView.setText("本文地址http://www.cnblogs.com/goagent/p/5159125.html本文地址啊本文。地址。啊http://www.cnblogs.com/goagent/p/5159125.html");
    }
}
