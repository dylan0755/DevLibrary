package com.dankal.mylibrary.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dankal.mylibrary.R;
import com.dylan.library.widget.DLAlertDialog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Dylan on 2017/7/28.
 */

public class PhotoPickerActivity extends Activity {
    private ImageView ivPhotoShow;
    private PhotoSelector mPicker;
    private DLAlertDialog alertDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopicker);
        alertDialog=new DLAlertDialog(this);
        ivPhotoShow= (ImageView) findViewById(R.id.iv_photoshow);
        mPicker=new PhotoSelector(this);
        mPicker.setPhotoPickerListener(new PhotoSelector.PhotoPickerListener() {
            @Override
            public void onPickerResult(String filepath) {
                Log.e("onPickerResult: ","filepath: "+filepath );
                try {
                    FileInputStream fis=new FileInputStream(filepath);
                    ivPhotoShow.setImageBitmap(BitmapFactory.decodeStream(fis));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public void selectPic(View view){
        mPicker.showDialog(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPicker.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBackPressed() {
        alertDialog.show("提示", "退出", new DLAlertDialog.CallBack() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {
                PhotoPickerActivity.super.onBackPressed();
            }
        });

    }
}
