package com.dankal.mylibrary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dylan.library.adapter.CommonAbsListView;



/**
 * Created by Dylan on 2017/1/1.
 */

public class DemoListAdapter extends CommonAbsListView.Adapter<String,DemoListAdapter.ViewHolder> {



    @Override
    public ViewHolder oncreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
       View contentView=mInflater.inflate(R.layout.gvitem_demolist,parent,false);
        ViewHolder holder=new ViewHolder(contentView);
        return holder;
    }

    @Override
    public void onBinderItem(ViewHolder holder, String s, int position) {
         holder.tvItem.setText(s);
    }

    class ViewHolder extends CommonAbsListView.ViewHolder{
        TextView tvItem;
        public ViewHolder(View convertView) {
            super(convertView);
            tvItem= (TextView) convertView.findViewById(R.id.tv_listItem);
        }
    }
}
