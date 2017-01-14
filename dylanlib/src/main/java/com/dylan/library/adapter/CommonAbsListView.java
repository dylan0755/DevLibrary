package com.dylan.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author Dylan
 */
public class CommonAbsListView  {

    public static abstract class Adapter<T, VH extends ViewHolder> extends BaseAdapter {
        protected Context context;
        protected LayoutInflater mInflater;
        protected List<T> dataList;

        public Adapter() {

        }

        public void bind(List<T> dataList){
            if (dataList!=null&&dataList.size()>0){
                this.dataList = dataList;
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return (dataList==null||dataList.size()==0)?0:dataList.size();
        }

        @Override
        public T getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (context==null){
                context = parent.getContext();
                mInflater = LayoutInflater.from(context);
            }

            VH holder;
            if (convertView==null){
                holder=oncreateViewHolder(mInflater,parent);
                convertView=holder.getConvertView();
                convertView.setTag(holder);
            }else{
                holder= (VH) convertView.getTag();
            }
            if (getItem(position)!=null) onBinderItem(holder, getItem(position),position);
            return convertView;
        }
        public abstract VH oncreateViewHolder(LayoutInflater inflater, ViewGroup parent);
        public abstract void onBinderItem(VH holder,T t,int position);



    }








    public static class ViewHolder {

        private View convertView;
        public ViewHolder(View convertView){
            this.convertView=convertView;
        }
        public View findViewById(int id){
            View view=convertView.findViewById(id);
            return view;
        }

        public View getConvertView(){
            return convertView;
        }

    }

}
