package com.dylan.mylibrary.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import com.dylan.library.utils.helper.PhotoSelector;
import com.dylan.mylibrary.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Dylan on 2017/7/28.
 */

public class LocalPhotoSelectActivity extends AppCompatActivity {
    private ImageView ivIdCardFront;
    private ImageView ivIdCardBack;
    private PhotoSelector photoSelector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopicker);
        ivIdCardFront = findViewById(R.id.ivIdCardFront);
        ivIdCardBack = findViewById(R.id.ivIdCardBack);
        photoSelector = new PhotoSelector(new PhotoSelector.PhotoPickerCallBack() {

            @Override
            public Context getActivityContext() {
                return LocalPhotoSelectActivity.this;
            }

            @Override
            public void onPickerResult(File pickFile, String tag) {
                try {
                    FileInputStream fis = new FileInputStream(pickFile);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);
                    if ("idCardFront".equals(tag)) {
                        ivIdCardFront.setImageBitmap(bitmap);
                    } else if ("idCardBack".equals(tag))
                        ivIdCardBack.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });

        ivIdCardFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoSelector.showDialog("idCardFront");
            }
        });

        ivIdCardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoSelector.showDialog(true,360,240,"idCardBack");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoSelector.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        photoSelector.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
