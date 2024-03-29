package com.dylan.mylibrary.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.dylan.library.dialog.PhotoPreviewDialog;
import com.dylan.mylibrary.R;
import com.gyf.barlibrary.ImmersionBar;

/**
 * Author: Dylan
 * Date: 2019/8/3
 * Desc:
 */
public class ScaleUpPhotoViewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);
        ImmersionBar  immersionBar=ImmersionBar.with(this);
        immersionBar.fitsSystemWindows(true);
        immersionBar.statusBarDarkFont(true);
        immersionBar.statusBarColorInt(Color.WHITE);
        immersionBar.init();
        final Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.ic_bitmap_test);


        ImageView ivTop=findViewById(R.id.ivTop);
        ivTop.setImageBitmap(bitmap);
        ivTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPreviewDialog dialog=new PhotoPreviewDialog(v.getContext());
                dialog.show((ImageView) v);
            }
        });

        ImageView ivCenter=findViewById(R.id.ivCenter);
        ivCenter.setImageBitmap(bitmap);
        ivCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPreviewDialog dialog=new PhotoPreviewDialog(v.getContext());
                dialog.setBackgroundColor(Color.BLACK);
                dialog.show((ImageView) v);
            }
        });

        ImageView ivBottom=findViewById(R.id.ivBottom);
        ivBottom.setImageBitmap(bitmap);
        ivBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPreviewDialog dialog=new PhotoPreviewDialog(v.getContext());
                dialog.show((ImageView) v);
            }
        });


    }
}
