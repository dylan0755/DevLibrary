package com.dankal.mylibrary.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dankal.mylibrary.R;
import com.dylan.library.widget.RedPointTextView;

/**
 * Created by Dylan on 2018/4/4.
 */

public class RedPointTextViewActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redpointtextview);
        RedPointTextView textView1= (RedPointTextView) findViewById(R.id.redText1);
        RedPointTextView textView2= (RedPointTextView) findViewById(R.id.redText2);
        textView1.setText("天安门广场上鸣礼炮21响。");
        textView1.setPointVisible();
        textView2.setText("国家主席习近平3日下午在人民大会堂北大厅举行仪式，欢迎津巴布韦总统埃默森·姆南加古瓦对我国进行国事访问。");
        textView2.setPointVisible();

    }


}
