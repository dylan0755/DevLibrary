package com.dylan.mylibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.bean.AutoLiveItem;

import java.util.List;

public class AutoLiveItemAdapter extends RecyclerView.Adapter<AutoLiveItemAdapter.ViewHolder> {
    private List<AutoLiveItem> items;
    public AutoLiveItemAdapter(List<AutoLiveItem> items){
        this.items=items;
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_autolive_member,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
