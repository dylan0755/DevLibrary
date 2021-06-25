package com.dylan.mylibrary.ui;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dylan.library.adapter.CommonBaseAdapter;
import com.dylan.library.callback.SingleClickListener;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.testdata.GridItemData;


/**
 * Created by Dylan on 2017/1/11.
 */

public class GridDemoItemAdapter extends CommonBaseAdapter<GridDemoItem, GridDemoItemAdapter.ViewHolder> {



    @Override
    public int getLayoutId() {
        return R.layout.rvitem_democlassify;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final GridDemoItem gridItemData, int position) {
        holder.itemName.setText(gridItemData.getItemName());
        holder.itemName.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnClick!=null)mOnClick.onItemClick(gridItemData);
            }
        });

    }



    class ViewHolder extends CommonBaseAdapter.ViewHolder{
         Button itemName;
        public ViewHolder(View itemView) {
            super(itemView);
            itemName=  itemView.findViewById(R.id.bt_classify_item);
        }
    }


    public interface OnItemClick{

        void onItemClick(GridDemoItem item);
    }
    private OnItemClick mOnClick;
    public void setOnItemClick(OnItemClick itemClick){
        mOnClick=itemClick;
    }

}
