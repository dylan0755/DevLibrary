package com.dylan.mylibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.dylan.library.adapter.CommonBaseAdapter;
import com.dylan.library.bean.DateInfo;
import com.dylan.library.utils.DateUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.widget.shape.GradientDrawableBuilder;
import com.dylan.mylibrary.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HeaderDateAdapter extends CommonBaseAdapter<DateInfo, HeaderDateAdapter.ViewHolder> {
    private GradientDrawable todayDrawable, selectDayDrawable;
    private int normalTextColor = Color.parseColor("#333333");
    private List<String> staticForDayList;
    private int selectPos = -1;
    private boolean canSelectEarlier=true;
    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void setCanSelectEarlier(boolean canSelectEarlier) {
        this.canSelectEarlier = canSelectEarlier;
    }

    public void setStaticForDayList(List<String> staticForDayList) {
        this.staticForDayList = staticForDayList;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_header_date;
    }

    @Override
    protected void attachContext(Context context) {
        todayDrawable = new GradientDrawableBuilder(context).setSolidColor(Color.BLACK).setAlpha(0.3f).setCornerRadius(15).build();
        selectDayDrawable = new GradientDrawableBuilder(context).setSolidColor(Color.parseColor("#171A27")).setCornerRadius(15).build();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, DateInfo dateInfo, int position) {
        holder.tvHasSchedules.setVisibility(View.INVISIBLE);
        if (dateInfo.timestamp == 0) return;
        holder.tvDateItem.setText(String.valueOf(dateInfo.getDay()));
        holder.tvDateItem.setTextColor(normalTextColor);
        if (selectPos == position) {
            holder.tvDateItem.setBackground(selectDayDrawable);
            holder.tvDateItem.setTextColor(Color.WHITE);
        } else if (DateUtils.isToday(dateInfo.timestamp)) {
            holder.tvDateItem.setBackground(todayDrawable);
        } else {
            holder.tvDateItem.setBackground(null);
        }

        if (EmptyUtils.isNotEmpty(staticForDayList)){
            String yyyMMdd=outputFormat.format(new Date(dateInfo.timestamp));
            boolean hasEvent=staticForDayList.contains(yyyMMdd);
            holder.tvHasSchedules.setVisibility(hasEvent?View.VISIBLE:View.INVISIBLE);
        }


        holder.tvDateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canSelectEarlier){
                    Date today = new Date();
                    today.setHours(0);
                    today.setMinutes(0);
                    today.setSeconds(0);
                    Date selectDate=new Date(dateInfo.timestamp);
                    if (selectDate.before(today)) {
                        Log.e("onClick: ", selectDate + " is before " + today);
                        return;
                    }
                }

                if (selectPos!=position){
                    selectPos=position;
                    if (onItemSelectLister !=null) onItemSelectLister.onSelect(dateInfo);
                    notifyDataSetChanged();
                }
            }
        });
    }

    class ViewHolder extends CommonBaseAdapter.ViewHolder {
        TextView tvDateItem,tvHasSchedules;

        public ViewHolder(View convertView) {
            super(convertView);
            tvDateItem = convertView.findViewById(R.id.tvDateItem);
            tvHasSchedules=convertView.findViewById(R.id.tvHasSchedules);
        }
    }

    public int getSelectPos(){
        return selectPos;
    }

    public void setSelect(int pos){
        selectPos=pos;
        if (selectPos<0)return;
        notifyDataSetChanged();
    }


    private OnItemSelectLister onItemSelectLister;
    public interface OnItemSelectLister {
         void onSelect(DateInfo dateInfo);
    }

    public void setOnItemSelectLister(OnItemSelectLister onItemSelectLister){
        this.onItemSelectLister = onItemSelectLister;
    }
}
