package com.dylan.mylibrary.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dylan.library.widget.photoview.PhotoView;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2019/8/3
 * Desc:
 */
public class PhotoViewActivity extends AppCompatActivity {
    PhotoView photoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);
        photoView=findViewById(R.id.photoView);

        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.ic_for_photoview);
        photoView.setImageBitmap(bitmap);
    }
}
