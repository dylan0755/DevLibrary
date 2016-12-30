package com.dankal.mylibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dankal.mylibrary.bean.VideoData;
import com.dylan.library.adapter.footer.FooterItemAdapter;
import com.dylan.library.adapter.footer.LoadStateListener;
import com.dylan.library.adapter.footer.RecyclerItemViewHolder;
import com.dylan.library.utils.StringUtils;
import com.dylan.library.widget.RefreshRecyclerView;

import java.util.List;

/**
 * Created by Dylan on 2016/9/9.
 */
public class VideoDataAdapter extends FooterItemAdapter<VideoData,VideoDataAdapter.MyViewHolder> {


    public VideoDataAdapter(Context context, RefreshRecyclerView recyclerView, List<VideoData> list, LoadStateListener listener) {
        super(context, recyclerView, list, listener);
    }



    @Override
    public MyViewHolder onCreateItemViewHolder(LayoutInflater inflater, ViewGroup parent) {
        View convertView = inflater.inflate(R.layout.rvitem_videoitem, parent, false);
        MyViewHolder holder = new MyViewHolder(convertView);
        return holder;
    }


    @Override
    public void onBindItemViewHolder(MyViewHolder holder, final VideoData videoData, int position) {
        if (videoData==null)  return;
        String imgurl=videoData.getImg_key();
        holder.tv_title.setText(videoData.getName());
        holder.tv_description.setText(videoData.getIntroduction());
        if (StringUtils.isValid(imgurl)){
            Glide.with(context).load(MyApplication.setURL(imgurl)).into(holder.thumbView);
        }
    }
    class MyViewHolder extends RecyclerItemViewHolder {
        ImageView thumbView;
        TextView tv_title;
        TextView tv_description;
        View rootView;
        public MyViewHolder(View itemView) {
            super(itemView);
            thumbView = (ImageView) itemView.findViewById(R.id.iv_videoitem_videothumb);
            tv_title = (TextView) itemView.findViewById(R.id.tv_videoitem_title);
            tv_description = (TextView) itemView.findViewById(R.id.tv_videoitem_description);
            rootView = itemView.findViewById(R.id.ll_videoitem_root);
        }
    }


}
