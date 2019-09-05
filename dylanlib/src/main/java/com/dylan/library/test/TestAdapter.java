package com.dylan.library.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dylan.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2017/1/1.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    private List<String> dataList;
    private Context mContext;
    public TestAdapter(){
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
        View convertView= LayoutInflater.from(mContext).inflate(R.layout.dl_rvitem_test,parent,false);
        ViewHolder holder=new ViewHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         holder.tvItem.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvItem;
        public ViewHolder(View itemView) {
            super(itemView);
            tvItem= (TextView) itemView.findViewById(R.id.tv_listItem);

        }
    }


    public List<String> getDataList(){
        List<String> list=new ArrayList<>();
        for (int i='A';i<'Z';i++){
            list.add(""+(char)i);
        }

        return list;
    }
}
