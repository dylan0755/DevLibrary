package com.dylan.mylibrary.adapter;

import android.view.View;
import android.widget.ImageView;

import com.dylan.library.adapter.CommonBaseAdapter;
import com.dylan.mylibrary.R;


/**
 * Author: Dylan
 * Date: 2019/7/27
 * Desc:
 */

public class PictureDataGridAdapter extends CommonBaseAdapter<String,PictureDataGridAdapter.ViewHolder> {


    @Override
    public int getLayoutId() {
        return R.layout.griditem_picture_preview_child;
    }

    @Override
    public void onBinderItem(ViewHolder holder, String s, int position) {

    }

    class ViewHolder extends CommonBaseAdapter.ViewHolder{
        ImageView ivPic;
        public ViewHolder(View convertView) {
            super(convertView);
            ivPic=convertView.findViewById(R.id.ivPic);
        }
    }
}
