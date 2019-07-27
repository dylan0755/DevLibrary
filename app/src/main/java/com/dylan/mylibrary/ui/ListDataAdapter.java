package com.dylan.mylibrary.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dylan.mylibrary.R;
import com.dylan.library.widget.ExpandableListItemLayout;

import java.util.List;

/**
 * Created by AD on 2016/3/28.
 */
public class ListDataAdapter extends BaseAdapter{
    private Context mContext;
    private List<String> list;
    private LayoutInflater mInflater;
    public ListDataAdapter( Context Context, List list){
        this.list=list;
        mContext=Context;
        mInflater=LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView==null){
            holder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.list_item_layout,parent,false);
            holder.expandableLayout = (ExpandableListItemLayout) convertView.findViewById(R.id.id_slidinglayout);
            //设置按下去的颜色
            holder.expandableLayout.setPressColor("#edfaff");
            holder.ItemName= (TextView) convertView.findViewById(R.id.id_list_item_text);
            holder.deleteButton= (Button) convertView.findViewById(R.id.id_listitem_delete_button);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
           //删除后出现listview保持着删除状态，所以要缩回去
           holder.expandableLayout.close();

           holder.ItemName.setText(getItem(position).toString());
           holder.deleteButton.setTag(position);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index=(Integer)holder.deleteButton.getTag();
                list.remove(index);
                notifyDataSetChanged();
                holder.expandableLayout.delete();


            }
        });


        holder.expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "" + getItem(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    class ViewHolder{
        ExpandableListItemLayout expandableLayout;
        TextView ItemName;
        Button deleteButton;

    }




}
