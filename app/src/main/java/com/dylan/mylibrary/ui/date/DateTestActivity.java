package com.dylan.mylibrary.ui.date;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.dylan.library.utils.DateUtils;
import com.dylan.mylibrary.R;

/**
 * Created by Dylan on 2016/12/16.
 */

public class DateTestActivity extends Activity {
    private TextView textView;
    private TextView tv_datepicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datetest);
        textView= (TextView) findViewById(R.id.tv_date);
        tv_datepicker= (TextView) findViewById(R.id.tv_datepicker);
//        tv_datepicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DateUtils.showDatePickerDialog(DateTestActivity.this, new DateUtils.DatePickerListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth) {
//                        tv_datepicker.setText("选择日期为："+year+"-"+month+"-"+dayofmonth);
//                    }
//                });
//            }
//        });
        getDate();
    }

    private void getDate(){
        StringBuffer buffer=new StringBuffer();
          String currentdate= DateUtils.getYear()+"年"+ DateUtils.getMonth()+
                  "月"+ DateUtils.getDayOfMonth()+"日"+" 星期"+ DateUtils.getDayOfWeek();

          DateUtils.getDateByDaysAfter(10);
         String _10daysafter= DateUtils.getYear()+"年"+ DateUtils.getMonth()+
                "月"+ DateUtils.getDayOfMonth()+"日"+" 星期"+ DateUtils.getDayOfWeek();

        DateUtils.getDateByMonthsAfter(2);

        String _2monthafter= DateUtils.getYear()+"年"+ DateUtils.getMonth()+
                "月"+ DateUtils.getDayOfMonth()+"日"+" 星期"+ DateUtils.getDayOfWeek();
        buffer.append("当前日期："+"\n"+currentdate).append("\n").append("10天后的日期：").append("\n")
                .append(_10daysafter)
                .append("\n")
                .append("2个月后的日期:")
                .append("\n")
                .append(_2monthafter);
        textView.setText(buffer.toString());
    }
}
