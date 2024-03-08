package com.dylan.mylibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.dylan.library.bean.DateInfo;
import com.dylan.library.utils.CalendarUtils;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.HeaderDateAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

public class DatePickerDialog extends Dialog {
    private Calendar mCalendar;
    private boolean canSelectEarlier=true;

    public void setCanSelectEarlier(boolean canSelectEarlier){
        this.canSelectEarlier=canSelectEarlier;
    }

    public DatePickerDialog(@NonNull Context context, Calendar calendar) {
        super(context, R.style.FullScreenDialog);
        this.mCalendar=Calendar.getInstance();
        mCalendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        mCalendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
        mCalendar.set(Calendar.DATE,calendar.get(Calendar.DATE));
        setContentView(R.layout.dialog_date_picker);
        findViewById(R.id.ivLastMonth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canSelectEarlier){
                    if (mCalendar.get(Calendar.MONTH)>Calendar.getInstance().get(Calendar.MONTH)){
                        mCalendar.add(Calendar.MONTH,-1);
                        initDate();
                    }
                }else{
                    mCalendar.add(Calendar.MONTH,-1);
                    initDate();
                }



            }
        });
        findViewById(R.id.ivNextMonth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.add(Calendar.MONTH,1);
                initDate();
            }
        });
    }

    private void initDate() {
        TextView tvYearMonth=findViewById(R.id.tvYearMonth);
        tvYearMonth.setText(mCalendar.get(Calendar.YEAR)+"年"+(mCalendar.get(Calendar.MONTH)+1)+"月" );
        LinkedHashMap<Long, ArrayList<DateInfo>> linkedHashMap= CalendarUtils.getMonthMap(mCalendar,1);
        long key=linkedHashMap.keySet().iterator().next();
        ArrayList<DateInfo> arrayList=linkedHashMap.get(key);
        HeaderDateAdapter dateAdapter=new HeaderDateAdapter();
        dateAdapter.setCanSelectEarlier(canSelectEarlier);
        GridView gridView=findViewById(R.id.gridView);
        gridView.setAdapter(dateAdapter);
        dateAdapter.bind(arrayList);
        dateAdapter.setOnItemSelectLister(new HeaderDateAdapter.OnItemSelectLister() {
            @Override
            public void onSelect(DateInfo dateInfo) {
                dismiss();
                if (onSelectDateListener!=null)onSelectDateListener.onSelect(arrayList,dateInfo,mCalendar);
            }
        });
    }


    public void show(OnSelectDateListener listener) {
        super.show();
        onSelectDateListener=listener;
        initDate();
    }

    private OnSelectDateListener onSelectDateListener;
    public interface OnSelectDateListener{
       void onSelect(ArrayList<DateInfo> list,DateInfo dateInfo,Calendar calendar);
    }
}
