package com.dylan.mylibrary.ui.onlinepic;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2019/7/25
 * Desc:
 */

public class SelectorAdapter extends RecyclerView.Adapter<SelectorAdapter.ViewHolder> {
    private int indicatorCount;
    private int currentPosition;

    public void setIndicatorCount(int count,int currentPosition) {
        indicatorCount=count;
        this.currentPosition=currentPosition;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position){
        currentPosition=position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_onlinepic_preview_selector,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (currentPosition==position){
            viewHolder.ivSelector.setSelected(true);
        }else{
            viewHolder.ivSelector.setSelected(false);
        }

    }



    @Override
    public int getItemCount() {
        return indicatorCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSelector;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSelector = itemView.findViewById(R.id.ivSelector);
        }
    }
}
