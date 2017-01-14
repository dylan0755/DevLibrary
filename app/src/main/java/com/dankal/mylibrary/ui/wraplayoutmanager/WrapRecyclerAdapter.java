package com.dankal.mylibrary.ui.wraplayoutmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.ui.testdata.GridItemData;
import com.dylan.library.adapter.BaseRecyclerAdapter;

/**
 * Created by Dylan on 2017/1/15.
 */

public class WrapRecyclerAdapter extends BaseRecyclerAdapter<GridItemData,WrapRecyclerAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateItemHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.rvitem_homeclassify,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, GridItemData gridItemData, int position) {
          holder.itemName.setText(gridItemData.getName());
    }

    class ViewHolder extends BaseRecyclerAdapter.ViewHolder{
        ImageView imgv;
        TextView itemName;
        public ViewHolder(View itemView) {
            super(itemView);
            imgv= (ImageView) itemView.findViewById(R.id.iv_classify_item);
            itemName= (TextView) itemView.findViewById(R.id.tv_classify_item);
        }

  }
}
