package com.dylan.common;

import android.view.View;

import com.dylan.library.adapter.BaseRecyclerAdapter;

import butterknife.ButterKnife;

/**
 * Author: Dylan
 * Date: 2019/9/30
 * Desc:
 */
public class ButterKnifeRecyclerViewHolder extends BaseRecyclerAdapter.ViewHolder {
    public ButterKnifeRecyclerViewHolder(View view) {
        super(view);
        ButterKnife.bind(this,view);
    }
}
