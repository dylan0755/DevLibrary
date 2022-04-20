package com.dylan.mylibrary.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dylan.common.ButterKnifeRecyclerViewHolder;
import com.dylan.library.adapter.BaseRecyclerAdapter;
import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.mylibrary.R;

import butterknife.BindView;


/**
 * Author: Dylan
 * Date: 2022/04/14
 * Desc:
 */
public class SelectCarouselPicAdapter extends BaseRecyclerAdapter<String, SelectCarouselPicAdapter.ViewHolder> {



    @Override
    public int getLayoutId() {
        return R.layout.item_selectcarousel_pic;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final String s, final int i) {
        if (EmptyUtils.isEmpty(s)) {
            viewHolder.ivDelete.setVisibility(View.GONE);
            viewHolder.ivPic.setImageResource(R.drawable.icon_add_pic_placeholder);
            viewHolder.ivPic.setOnClickListener(new SingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                     if (mItemClickListener!=null){
                         mItemClickListener.onClick(s,i);
                     }
                }
            });
        } else {
            Glide.with(getContext()).load(s).into(viewHolder.ivPic);
            viewHolder.ivPic.setOnClickListener(null);
            viewHolder.ivDelete.setVisibility(View.VISIBLE);
            viewHolder.ivDelete.setOnClickListener(new SingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    getDataList().remove(i);
                    notifyDataSetChanged();
                }
            });
        }
    }

    class ViewHolder extends ButterKnifeRecyclerViewHolder {
        @BindView(R.id.ivPic)
        ImageView ivPic;
        @BindView(R.id.ivDelete)
        ImageView ivDelete;

        public ViewHolder(View view) {
            super(view);
        }
    }
}
