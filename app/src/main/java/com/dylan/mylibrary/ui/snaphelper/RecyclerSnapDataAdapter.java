package com.dylan.mylibrary.ui.snaphelper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.dylan.library.adapter.BaseRecyclerAdapter;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.PictureDataGridAdapter;

import java.util.ArrayList;

/**
 * Author: Dylan
 * Date: 2019/7/27
 * Desc:
 */

public class RecyclerSnapDataAdapter extends BaseRecyclerAdapter<String,RecyclerSnapDataAdapter.ViewHolder>{


    @Override
    public int getLayoutId() {
        return R.layout.rvitem_picture_preview_parent;
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        Log.e("RecyclerSnapDataAdapter", "onViewRecycled: " );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, String s, int position) {
        holder.tvPositon.setText(String.valueOf(position+1));
        Log.e("onBindViewHolder: ","position "+position );
    }




    class ViewHolder extends BaseRecyclerAdapter.ViewHolder{
         TextView tvPositon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPositon=itemView.findViewById(R.id.tvPositon);
        }
    }

    private OnChildItemClickListener onChildItemClickListener;
    public interface OnChildItemClickListener{
        void click(AdapterView<?> parent,ArrayList<String> picUrlList,int position);
    }

    public void setOnChildItemClickListener(OnChildItemClickListener listener){
        onChildItemClickListener=listener;
    }
}
