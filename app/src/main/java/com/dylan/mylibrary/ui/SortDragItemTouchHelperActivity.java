package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.dialog.NoPeopleImgCarouselDialog;

/**
 * Author: Dylan
 * Date: 2022/04/20
 * Desc:
 */
public class SortDragItemTouchHelperActivity extends AppCompatActivity {
    NoPeopleImgCarouselDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sortdrag_item_touch);
    }



    public void show(View view){
        dialog=new NoPeopleImgCarouselDialog(this);
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (dialog!=null){
            dialog.onActivityResult(requestCode,resultCode,data);
        }
    }
}
