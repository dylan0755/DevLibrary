package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.dylan.library.bean.DateInfo;
import com.dylan.library.utils.CalendarUtils;
import com.dylan.library.utils.DateUtils;
import com.dylan.library.utils.DensityFontUtils;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.widget.wheel.NumericWheelAdapter;
import com.dylan.library.widget.wheel.OnItemSelectedListener;
import com.dylan.library.widget.wheel.WheelView;
import com.dylan.mylibrary.MyApplication;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.HorizontalDateAdapter;
import com.dylan.mylibrary.dialog.DatePickerDialog;
import com.hjq.toast.Toaster;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

public class SelectDateTimeActivity extends AppCompatActivity implements View.OnClickListener {
    private HorizontalDateAdapter mAdapter;
    private String selectTime;
    private NumericWheelAdapter hourAdapter, minuteAdapter;
    private LinearSnapHelper linearSnapHelper;
    private Calendar mCalendar;
    private String preSelectDate;
    RecyclerView rvDate;
    TextView tvSelectedDate,tvSelectTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DensityFontUtils.setCustomActivityDensityInWidth(390,this, MyApplication.getApplication());
        setContentView(R.layout.activity_select_datetime);
        rvDate=findViewById(R.id.rvDate);
        tvSelectedDate=findViewById(R.id.tvSelectedDate);
        tvSelectTime=findViewById(R.id.tvSelectTime);
        TextView tvTitle=findViewById(R.id.tvTitle);
        tvTitle.setText("选择日期");
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initDate();
        initTime();
        findViewById(R.id.llMorning).setOnClickListener(this);
        findViewById(R.id.llAfternoon).setOnClickListener(this);
        findViewById(R.id.llDusk).setOnClickListener(this);
        findViewById(R.id.btnComplete).setOnClickListener(this);
        ImageView iconRight=findViewById(R.id.iconRight);
        iconRight.setOnClickListener(this);
        iconRight.setImageResource(R.mipmap.icon_calandar);
        iconRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog=new DatePickerDialog(v.getContext(),mCalendar);
                dialog.setCanSelectEarlier(false);
                dialog.show(new DatePickerDialog.OnSelectDateListener() {
                    @Override
                    public void onSelect(ArrayList<DateInfo> list, DateInfo dateInfo, Calendar calendar) {
                        mCalendar=calendar;
                        pickDate(list,-1);
                        rvDate.scrollToPosition(0);
                        int selectPos=mAdapter.getDataList().indexOf(dateInfo);
                        int offsetX=(selectPos-2)* DensityUtils.dp2px(SelectDateTimeActivity.this,68);
                        rvDate.scrollBy(offsetX,0);
                        fillSelectDateInfo(dateInfo);
                    }
                });
            }
        });
    }

    private void initDate() {
        preSelectDate=getIntent().getStringExtra("preSelectDate");
        mCalendar=Calendar.getInstance();
        int  preSelectDay=-1;
        if (EmptyUtils.isNotEmpty(preSelectDate)){
            Log.e("initDate: ", "preSelectDate="+preSelectDate);
            //预选日期
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            try {
                mCalendar.setTime(simpleDateFormat.parse(preSelectDate));
                preSelectDay=mCalendar.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mAdapter = new HorizontalDateAdapter();
        rvDate.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvDate.setAdapter(mAdapter);
        linearSnapHelper= new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(rvDate);
        LinkedHashMap<Long, ArrayList<DateInfo>> linkedHashMap = CalendarUtils.getMonthMap(mCalendar, 1);
        long key = linkedHashMap.keySet().iterator().next();
        ArrayList<DateInfo> arrayList = linkedHashMap.get(key);
        pickDate(arrayList,preSelectDay);
    }


    private void pickDate(ArrayList<DateInfo> arrayList,int  preSelectDay) {
        ArrayList<DateInfo> dateList = new ArrayList<>();
        DateInfo todayDateInfo = null;
        DateInfo preSelectDateInfo=null;
        for (DateInfo dateInfo : arrayList) {
            if (dateInfo.day > 0) {
                dateList.add(dateInfo);
                if (DateUtils.isToday(dateInfo.timestamp)) {
                    todayDateInfo = dateInfo;
                }
                if (preSelectDay>0&&dateInfo.day==preSelectDay){
                    preSelectDateInfo=dateInfo;
                }
            }
        }
        dateList.add(0, new DateInfo());
        dateList.add(0, new DateInfo());
        dateList.add( new DateInfo());
        dateList.add( new DateInfo());
        mAdapter.bind(dateList);
        if (EmptyUtils.isNotEmpty(preSelectDateInfo)){
            int index = dateList.indexOf(preSelectDateInfo) - 2;
            rvDate.scrollToPosition(index);
            fillSelectDateInfo(preSelectDateInfo);
        }else if (EmptyUtils.isNotEmpty(todayDateInfo)) {
            int index = dateList.indexOf(todayDateInfo) - 2;
            rvDate.scrollToPosition(index);
            fillSelectDateInfo(todayDateInfo);
        }


        rvDate.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (recyclerView.getChildCount() > 0) {
                        try {
                            // 获取具体位置 划重点
                            int currentPosition = ((RecyclerView.LayoutParams) linearSnapHelper.findSnapView(recyclerView.getLayoutManager()).getLayoutParams()).getViewAdapterPosition();
                            DateInfo selectDateInfo = dateList.get(currentPosition);
                            fillSelectDateInfo(selectDateInfo);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
    }

    DateInfo mSelectDateInfo;
    private void fillSelectDateInfo(DateInfo selectDateInfo) {
        if (mSelectDateInfo != null) {
            mSelectDateInfo.isSelected = false;
        }
        selectDateInfo.isSelected = true;
        mSelectDateInfo = selectDateInfo;
        String day = selectDateInfo.getDay();
        String week = selectDateInfo.getWeekName();
        tvSelectedDate.setText(week + " " + selectDateInfo.getMonth() + "月" + day);
        mAdapter.notifyDataSetChanged();
    }

    private void initTime() {
        Date date = new Date();
        int hour = date.getHours();
        int minute = date.getMinutes();
        int radius = 6;
        WheelView wvHour = this.findViewById(R.id.wvhour);
        wvHour.setDividerColor(Color.parseColor("#F3F3F3"));
        wvHour.setDividerRoundRadius(radius);
        wvHour.setLineSpacingMultiplier(2f);
        wvHour.setVisibleItems(5);
        wvHour.setCyclic(true);
        wvHour.setDividerType(WheelView.DividerType.FILL);
        hourAdapter = new NumericWheelAdapter(0, 23);
        //定位当前
        int currentHourIndex = hourAdapter.indexOf(hour);
        wvHour.setCurrentItem(currentHourIndex);
        wvHour.setAdapter(hourAdapter);


        WheelView wvMinute = this.findViewById(R.id.wvminute);
        wvMinute.setDividerColor(Color.parseColor("#F3F3F3"));
        wvMinute.setDividerRoundRadius(radius);
        wvMinute.setLineSpacingMultiplier(2f);
        wvMinute.setVisibleItems(5);
        wvMinute.setCyclic(true);
        wvMinute.setCurrentItem(0);
        wvMinute.setDividerType(WheelView.DividerType.FILL);
        minuteAdapter = new NumericWheelAdapter(0, 59);
        //定位当前
        int currentMinuteIndex = minuteAdapter.indexOf(minute);
        wvMinute.setCurrentItem(currentMinuteIndex);
        wvMinute.setAdapter(minuteAdapter);
        selectTime = hour + (minute<10?(":0" + minute):":"+minute);
        tvSelectTime.setText(selectTime);

        wvHour.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int var1) {
                int minute=wvMinute.getCurrentItem();
                selectTime = var1 +(minute<10?(":0" + minute):":"+minute);
                tvSelectTime.setText(selectTime);
            }
        });
        wvMinute.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int var1) {
                selectTime = wvHour.getCurrentItem() + (var1<10?(":0" + var1):":"+var1);
                tvSelectTime.setText(selectTime);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llMorning:
                seekTo(9,0);
                break;
            case R.id.llAfternoon:
                seekTo(13,0);
                break;
            case R.id.llDusk:
                seekTo(19,0);
                break;
            case R.id.btnComplete:
                complete();
                break;
        }
    }

    public void seekTo(int hour,int minute){
        WheelView wvhour=findViewById(R.id.wvhour);
        WheelView wvminute=findViewById(R.id.wvminute);
        int currentHourIndex = hourAdapter.indexOf(hour);
        wvhour.setCurrentItem(currentHourIndex);
        int currentMinuteIndex = minuteAdapter.indexOf(minute);
        wvminute.setCurrentItem(currentMinuteIndex);
        selectTime = hour+ (minute<10?(":0" + minute):":"+minute);
        tvSelectTime.setText(selectTime);
    }

    public void complete(){
        String selectDate=mSelectDateInfo.getDate()+" "+selectTime;
        long selectTimeStamp=DateUtils.parseYMDHMS(selectDate+":00");
        Log.e("complete: ","selectDate="+selectDate+" currentTimeStamp="+selectTimeStamp );
        if (selectTimeStamp-System.currentTimeMillis()<1000*60*60*3){
            Toaster.show("只能选择当前3小时之后的时间");
            return;
        }
        Intent intent=new Intent();
        intent.putExtra("date",selectDate);
        intent.putExtra("dateTs",selectTimeStamp);
        setResult(RESULT_OK,intent);
        finish();


    }
}
