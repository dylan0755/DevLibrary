package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dylan.library.utils.Logger;
import com.dylan.library.widget.VerticalSeekBar;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2021/1/26
 * Desc:
 */
public class VerticalSeekBarActivity extends AppCompatActivity {
    private VerticalSeekBar mVerticalSeekBar;
    private TextView tvProgressText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verticalseekbar);
        tvProgressText=findViewById(R.id.tvProgressText);
        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.seekBar);
        mVerticalSeekBar.setMax(100);
        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvProgressText.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
