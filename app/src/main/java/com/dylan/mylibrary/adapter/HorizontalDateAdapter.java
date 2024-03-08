package com.dylan.mylibrary.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.dylan.library.adapter.BaseRecyclerAdapter;
import com.dylan.library.bean.DateInfo;
import com.dylan.mylibrary.R;

public class HorizontalDateAdapter extends BaseRecyclerAdapter<DateInfo,HorizontalDateAdapter.ViewHolder> {
    private int selectColor= Color.parseColor("#333333");
    private int unselectColor= Color.parseColor("#91333333");


    @Override
    public int getLayoutId() {
        return R.layout.item_horizontal_date;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, DateInfo dateInfo, int position) {
        if (dateInfo.timestamp==0){
            holder.tvDay.setText("");
            holder.tvWeek.setText("");
            holder.tvMonth.setText("");
            return;
        }

        holder.tvDay.setText(dateInfo.getDay());
        holder.tvWeek.setText(dateInfo.getWeekName());
        holder.tvMonth.setText(dateInfo.getMonth()+"月");
        holder.tvDay.setTextColor(dateInfo.isSelected?selectColor:unselectColor);
        holder.tvWeek.setTextColor(dateInfo.isSelected?selectColor:unselectColor);
        holder.tvMonth.setTextColor(dateInfo.isSelected?selectColor:unselectColor);
    }


    class ViewHolder extends BaseRecyclerAdapter.ViewHolder{
        TextView tvDay,tvWeek,tvMonth;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDay=itemView.findViewById(R.id.tvDay);
            tvWeek=itemView.findViewById(R.id.tvWeek);
            tvMonth=itemView.findViewById(R.id.tvMonth);
        }
    }
}
