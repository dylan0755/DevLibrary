package com.dylan.mylibrary.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.utils.ImageViewUtils;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2020/2/23
 * Desc:
 */
public class BitmapHelperActivity extends AppCompatActivity {
    private ImageView ivCenterCropRoundCorner;
    private ImageView ivCenterCropRoundCornerWithBorder;
    private ImageView ivCircle;
    private ImageView ivCircleWithBorder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmaphelpr);
        ivCenterCropRoundCorner = findViewById(R.id.ivCenterCropRound);
        ivCenterCropRoundCornerWithBorder=findViewById(R.id.ivCenterCropRoundCornerWithBorder);
        ivCircle=findViewById(R.id.ivCircle);
        ivCircleWithBorder=findViewById(R.id.ivCircleWithBorder);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_bitmap_test);
        int[] spec = ImageViewUtils.getImageSize(ivCenterCropRoundCorner);
        //圆角
        Bitmap centerCropRoundCorner = BitmapHelper.centerCropRoundCorner(bitmap, 8, spec[0], spec[1]);
        ivCenterCropRoundCorner.setImageBitmap(centerCropRoundCorner);

        //圆角带边框
        Bitmap centerCropRoundCornerBorder=BitmapHelper.centerCropRoundCornerWithBorder(bitmap,8,4,
                Color.YELLOW,spec[0],spec[1]);
        ivCenterCropRoundCornerWithBorder.setImageBitmap(centerCropRoundCornerBorder);


        //圆形
        Bitmap circleBitmap=BitmapHelper.transformCircle(bitmap,spec[0],spec[1]);
        ivCircle.setImageBitmap(circleBitmap);


        //圆形边框
        Bitmap circleBorder=BitmapHelper.transformCircleWithBorder(bitmap,2,Color.YELLOW,spec[0],spec[1]);
        ivCircleWithBorder.setImageBitmap(circleBorder);
    }
}
