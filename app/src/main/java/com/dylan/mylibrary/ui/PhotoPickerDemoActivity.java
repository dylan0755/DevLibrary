package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dylan.mylibrary.R;
import com.dylan.photopicker.app.PhotoPickerActivity;

/**
 * Author: Dylan
 * Date: 2020/2/27
 * Desc:
 */
public class PhotoPickerDemoActivity extends AppCompatActivity {
    private TextView tvSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);
        tvSelected= (TextView) findViewById(R.id.tv_selected);
    }

    //本地相册
    public void openPicker(View view) {
        Intent intent = new Intent(view.getContext(), PhotoPickerActivity.class);
        intent.putExtra(com.dylan.photopicker.app.PhotoPickerActivity.EXTRA_MAX_SELECT,9);
         intent.putExtra(PhotoPickerActivity.EXTRA_LIMIT_SELECT_TIP,"最多能选9张图片");
        intent.putExtra(PhotoPickerActivity.EXTRA_CAN_PREVIEW_ON_SINGLE_CHOICE,false);
        startActivityForResult(intent, 1001);
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            if (data==null)return;
            String[] array = data.getStringArrayExtra(com.dylan.photopicker.app.PhotoPickerActivity.IMAGE_SELECT_ARRAY);
            if (array!=null){
                StringBuffer buffer=new StringBuffer();
                for (String str : array) {
                    Log.e("startActivityForResult", "str " + str);
                    buffer.append(str+"\n"+"\n");
                }
                tvSelected.setText(buffer.toString());
            }

        }
    }
}
