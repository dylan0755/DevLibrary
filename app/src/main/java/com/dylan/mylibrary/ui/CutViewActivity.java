package com.dylan.mylibrary.ui;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dylan.library.widget.CutView;
import com.dylan.mylibrary.R;

public class CutViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutview);
        CutView cutView1=findViewById(R.id.cutView1);
        cutView1.setTip("向下拖动裁剪相机 进行背景填充");
        cutView1.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_clip_drag));

        CutView cutView2=findViewById(R.id.cutView2);
        cutView2.enableLine(true);

    }
}
