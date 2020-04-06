package com.dylan.mylibrary.ui.lazyload;

import android.view.View;
import android.widget.TextView;

import com.dylan.mylibrary.R;
import com.dylan.library.adapter.BaseRecyclerAdapter;

/**
 * Created by Dylan on 2018/2/8.
 */

public class NewDataAdapter extends BaseRecyclerAdapter<NewItem,NewDataAdapter.ViewHolder> {



    @Override
    public int getLayoutId() {
        return R.layout.rvitem_newdata;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, NewItem newItem, int position) {
        holder.tvTitle.setText("0000");
    }

    class ViewHolder extends BaseRecyclerAdapter.ViewHolder{
        TextView tvTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle= (TextView) itemView.findViewById(R.id.tv_newTitle);
        }
    }
}
