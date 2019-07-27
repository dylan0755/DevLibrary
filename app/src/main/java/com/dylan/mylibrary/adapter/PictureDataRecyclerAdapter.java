package com.dylan.mylibrary.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dylan.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/7/27
 * Desc:
 */

public class PictureDataRecyclerAdapter extends RecyclerView.Adapter<PictureDataRecyclerAdapter.ViewHolder>{
    ArrayList<String> picUrls=new ArrayList<>();


    public PictureDataRecyclerAdapter(){
        picUrls.add("http://img4.imgtn.bdimg.com/it/u=2958967064,2714608608&fm=26&gp=0.jpg");
        picUrls.add("http://img4.imgtn.bdimg.com/it/u=2170225557,758070829&fm=26&gp=0.jpg");
        // picUrls.add("http://img5.imgtn.bdimg.com/it/u=3304966988,1696890235&fm=26&gp=0.jpg");
        picUrls.add("http://sjbz.fd.zol-img.com.cn/t_s640x1136c/g2/M00/01/04/ChMlWl01HCGIfugDABVvrwnLHUkAAL-dgA9wLsAFW_H618.jpg");
        picUrls.add("http://img4.imgtn.bdimg.com/it/u=3999606577,2503585579&fm=26&gp=0.jpg");
        picUrls.add("http://img4.imgtn.bdimg.com/it/u=3077036428,433114230&fm=26&gp=0.jpg");
        picUrls.add("http://img3.imgtn.bdimg.com/it/u=332908813,1086226178&fm=26&gp=0.jpg");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.rvitem_picture_preview_parent,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        PictureDataGridAdapter gridAdapter=new PictureDataGridAdapter();
        viewHolder.gridView.setAdapter(gridAdapter);
        gridAdapter.bind(picUrls);
        viewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onChildItemClickListener!=null)onChildItemClickListener.click(parent,picUrls,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
         GridView gridView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gridView=itemView.findViewById(R.id.gridView);
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
