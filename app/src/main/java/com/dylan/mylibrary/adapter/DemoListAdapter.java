package com.dylan.mylibrary.adapter;

import android.view.View;
import android.widget.TextView;

import com.dylan.mylibrary.R;
import com.dylan.library.adapter.CommonBaseAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dylan on 2017/1/1.
 */

public class DemoListAdapter extends CommonBaseAdapter<String,DemoListAdapter.ViewHolder> {


    public DemoListAdapter(){
    }



    @Override
    public int getLayoutId() {
        return R.layout.gvitem_demolist;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, String s, int position) {
        holder.tvItem.setText(s);
    }



    class ViewHolder extends CommonBaseAdapter.ViewHolder{
        TextView tvItem;
        public ViewHolder(View convertView) {
            super(convertView);
            tvItem=  convertView.findViewById(R.id.tv_listItem);

        }
    }
}
