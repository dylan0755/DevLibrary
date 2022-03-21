package com.dylan.common;

import android.view.View;

import com.dylan.library.adapter.CommonBaseAdapter;

import butterknife.ButterKnife;

/**
 * Author: Dylan
 * Date: 2019/9/30
 * Desc:
 */
public class ButterKnifeBaseAdapterViewHolder extends CommonBaseAdapter.ViewHolder {
    public ButterKnifeBaseAdapterViewHolder(View view) {
        super(view);
        ButterKnife.bind(this,view);
    }
}
