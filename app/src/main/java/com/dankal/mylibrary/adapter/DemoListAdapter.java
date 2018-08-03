package com.dankal.mylibrary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dylan.library.adapter.CommonBaseAdapter;


/**
 * Created by Dylan on 2017/1/1.
 */

public class DemoListAdapter extends CommonBaseAdapter<String,DemoListAdapter.ViewHolder> {




    @Override
    public int getLayoutId() {
        return R.layout.gvitem_demolist;
    }

    @Override
    public void onBinderItem(ViewHolder holder, String s, int position) {
         holder.tvItem.setText(s);
    }

    class ViewHolder extends CommonBaseAdapter.ViewHolder{
        TextView tvItem;
        public ViewHolder(View convertView) {
            super(convertView);
            tvItem= (TextView) convertView.findViewById(R.id.tv_listItem);
        }
    }
}
