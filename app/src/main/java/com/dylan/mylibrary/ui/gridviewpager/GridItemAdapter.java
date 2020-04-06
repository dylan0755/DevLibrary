package com.dylan.mylibrary.ui.gridviewpager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.testdata.GridItemData;
import com.dylan.library.adapter.CommonBaseAdapter;


/**
 * Created by Dylan on 2017/1/11.
 */

public class GridItemAdapter extends CommonBaseAdapter<GridItemData,GridItemAdapter.ViewHolder> {



    @Override
    public int getLayoutId() {
        return R.layout.rvitem_homeclassify;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, GridItemData gridItemData, int position) {
        holder.itemName.setText(gridItemData.getName());
    }



    class ViewHolder extends CommonBaseAdapter.ViewHolder{
         ImageView imgv;
         TextView itemName;
        public ViewHolder(View itemView) {
            super(itemView);
            imgv= (ImageView) itemView.findViewById(R.id.iv_classify_item);
            itemName= (TextView) itemView.findViewById(R.id.tv_classify_item);
        }
    }

}
