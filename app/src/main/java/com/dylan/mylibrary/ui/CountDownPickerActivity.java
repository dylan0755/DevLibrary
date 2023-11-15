package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dylan.library.widget.wheel.NumericWheelAdapter;
import com.dylan.library.widget.wheel.WheelView;
import com.dylan.mylibrary.R;

public class CountDownPickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_pick);

        int radius=6;
        WheelView wvHour = this.findViewById(R.id.hour);
        wvHour.setDividerColor(Color.parseColor("#F3F3F3"));
        wvHour.setDividerRoundRadius(radius);
        wvHour.setLineSpacingMultiplier(2f);
        wvHour.setVisibleItems(5);
        wvHour.setCyclic(true);
        wvHour.setCurrentItem(1);
        wvHour.setDividerType(WheelView.DividerType.ROUND_RECT);
        wvHour.setAdapter(new NumericWheelAdapter(0, 23));


        WheelView wvMinute=this.findViewById(R.id.minute);
        wvMinute.setDividerColor(Color.parseColor("#F3F3F3"));
        wvMinute.setDividerRoundRadius(radius);
        wvMinute.setLineSpacingMultiplier(2f);
        wvMinute.setVisibleItems(5);
        wvMinute.setCyclic(true);
        wvMinute.setCurrentItem(0);
        wvMinute.setDividerType(WheelView.DividerType.ROUND_RECT);
        wvMinute.setAdapter(new NumericWheelAdapter(0, 59));


        WheelView wvSecond =this.findViewById(R.id.second);
        wvSecond.setDividerColor(Color.parseColor("#F3F3F3"));
        wvSecond.setDividerRoundRadius(radius);
        wvSecond.setLineSpacingMultiplier(2f);
        wvSecond.setVisibleItems(5);
        wvSecond.setCyclic(true);
        wvSecond.setCurrentItem(0);
        wvSecond.setDividerType(WheelView.DividerType.ROUND_RECT);
        wvSecond.setAdapter(new NumericWheelAdapter(0, 59));


        findViewById(R.id.tvComfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour= (int) wvHour.getAdapter().getItem(wvHour.getCurrentItem());
                int minute= (int) wvMinute.getAdapter().getItem(wvMinute.getCurrentItem());
                int second= (int) wvSecond.getAdapter().getItem(wvSecond.getCurrentItem());
                Log.e("onClick: ",""+hour+"时 "+minute+"分 "+second+"秒" );
            }
        });
    }
}
