package com.dylan.mylibrary.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dylan.mylibrary.R;
/**
 * Author: Dylan
 * Date: 2019/8/4
 * Desc:
 */
public class ModifyFontActivity extends AppCompatActivity {
    private Typeface mTypeface;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(LayoutInflater.from(this), new LayoutInflater.Factory2()
        {
            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                return  replaceFont(null,name, context, attrs);
            }
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                return  replaceFont(parent,name, context, attrs);
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyfont);
    }


    private View replaceFont(View Parent,String name, Context context, AttributeSet attrs) {
        if (mTypeface==null)Typeface.createFromAsset(getAssets(),"fonts/PingFangSC-Semibold.ttf");
        Log.e("replaceFont: ","name "+name );
        if ("TextView".equals(name)) {
            TextView textView = new TextView(context, attrs);
            textView.setTextColor(Color.parseColor("#4B4B52"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
            textView.setTypeface(mTypeface);
            return textView;
        }else if ("android.support.v7.widget.AppCompatTextView".equals(name)){
         // ......
        }
        return null;
    }
}
