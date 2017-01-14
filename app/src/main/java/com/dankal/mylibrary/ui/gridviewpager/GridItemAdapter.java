package com.dankal.mylibrary.ui.gridviewpager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.ui.testdata.GridItemData;
import com.dylan.library.adapter.CommonAbsListView;


/**
 * Created by Dylan on 2017/1/11.
 */

public class GridItemAdapter extends CommonAbsListView.Adapter<GridItemData,GridItemAdapter.ViewHolder> {


    @Override
    public ViewHolder oncreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        ViewHolder holder=new ViewHolder(inflater.inflate(R.layout.rvitem_homeclassify,parent,false));
        return holder;
    }

    @Override
    public void onBinderItem(ViewHolder holder, GridItemData homeClassifyBean, int position) {

        holder.itemName.setText(homeClassifyBean.getName());
    }

    class ViewHolder extends CommonAbsListView.ViewHolder{
         ImageView imgv;
         TextView itemName;
        public ViewHolder(View itemView) {
            super(itemView);
            imgv= (ImageView) itemView.findViewById(R.id.iv_classify_item);
            itemName= (TextView) itemView.findViewById(R.id.tv_classify_item);
        }
    }

}
