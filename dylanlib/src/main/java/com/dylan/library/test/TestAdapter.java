package com.dylan.library.test;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dylan.library.R;
import com.dylan.library.adapter.BaseRecyclerAdapter;

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
    public void onBindViewHolder(ViewHolder holder,final String s, int position) {
        holder.tvItem.setText(s);
        holder.tvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
            }
        });
    }




    class ViewHolder extends BaseRecyclerAdapter.ViewHolder{
        TextView tvItem;
        public ViewHolder(View itemView) {
            super(itemView);
            tvItem=  itemView.findViewById(R.id.tv_listItem);
        }
    }


}
