package com.dylan.mylibrary.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.dylan.mylibrary.R;
import com.dylan.library.widget.RedPointTextView;

/**
 * Created by Dylan on 2018/4/4.
 */

public class RedPointTextViewActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redpointtextview);
        RedPointTextView textView1=findViewById(R.id.redText1);
        RedPointTextView textView2=  findViewById(R.id.redText2);
        RedPointTextView textView3=findViewById(R.id.redText3);
        textView1.setText("天安门广场上鸣礼炮21响。");
        textView1.showPoint();
        textView2.setText("国家主席习近平3日下午在人民大会堂北大厅举行仪式，欢迎津巴布韦总统埃默森·姆南加古瓦对我国进行国事访问。");
        textView2.showPoint();


        textView3.setText("国家主席习近平3日下午在人民大会堂北大厅举行仪式，欢迎津巴布韦总统埃默森·姆南加古瓦对我国进行国事访问。");
        textView3.showPoint(Color.DKGRAY);
    }


}
