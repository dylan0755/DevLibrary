package com.dylan.library.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.adapter.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2017/1/1.
 */

public class TestAdapter extends BaseRecyclerAdapter<String,TestAdapter.ViewHolder> {
    public TestAdapter(){

    }





    @Override
    public int getLayoutId() {
        return R.layout.dl_rvitem_test;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, String s, int position) {
        holder.tvItem.setText(s);
    }




    class ViewHolder extends BaseRecyclerAdapter.ViewHolder{
        TextView tvItem;
        public ViewHolder(View itemView) {
            super(itemView);
            tvItem=  itemView.findViewById(R.id.tv_listItem);
        }
    }


}
