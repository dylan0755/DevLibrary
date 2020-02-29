package com.dylan.mylibrary.ui.marginspan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.dylan.library.utils.SpannableStringUtils;
import com.dylan.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2017/1/1.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private List<String> dataList;
    private Context mContext;
    public DataAdapter(){
        dataList=new ArrayList<String>();
        bind(getDataList());
    }


    public void bind(List<String> list){
        if (list!=null&&list.size()>0){
            dataList=list;
            notifyDataSetChanged();
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext==null)mContext=parent.getContext();
        View convertView= LayoutInflater.from(mContext).inflate(R.layout.rvitem_firstline_margin,parent,false);
        ViewHolder holder=new ViewHolder(convertView);
        return holder;
    }




    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String author="主播：";
        holder.tvAuthor.setText(author);
        final String content=dataList.get(position);
        int leftTagViewWidth= (int)holder.tvAuthor.getPaint().measureText(author);
        SpannableString spannableString=SpannableStringUtils.firstLineMarginLeft(leftTagViewWidth,content);
        holder.tvMessage.setText(spannableString);


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvMessage;
        TextView tvAuthor;
        public ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvAuthor=itemView.findViewById(R.id.tvAuthor);
        }
    }


    public List<String> getDataList(){
        List<String> list=new ArrayList<>();
        for (int i='A';i<'Z';i++){
            //list.add(""+(char)i);
            list.add("关注主播不迷路，主播带你上高速，视频左上角关注点起来");

        }

        return list;
    }
}
