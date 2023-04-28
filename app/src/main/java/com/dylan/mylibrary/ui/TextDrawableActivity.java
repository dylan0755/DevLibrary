package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dylan.library.utils.DensityUtils;
import com.dylan.library.widget.CircleImageView;
import com.dylan.library.widget.shape.TextDrawable;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2022/03/04
 * Desc:
 */
public class TextDrawableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textdrawable);

        String firstChar="六";
        TextDrawable textDrawable=TextDrawable.builder()
                .beginConfig().textColor(Color.parseColor("#FF2D2D2D"))
                .fontSize(DensityUtils.dp2px(this,16))
                .width(DensityUtils.dp2px(this,50))
                .height(DensityUtils.dp2px(this,50))
                .endConfig()
                .buildRect(firstChar, Color.parseColor("#FFFADB22"));


        CircleImageView circleImageView= findViewById(R.id.ivAvatar);
        circleImageView.setImageDrawable(textDrawable);

    }
}
