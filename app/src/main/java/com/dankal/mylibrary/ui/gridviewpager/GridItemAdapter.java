package com.dankal.mylibrary.ui.gridviewpager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.ui.testdata.GridItemData;
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
    public void onBinderItem(ViewHolder holder, GridItemData homeClassifyBean, int position) {

        holder.itemName.setText(homeClassifyBean.getName());
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
