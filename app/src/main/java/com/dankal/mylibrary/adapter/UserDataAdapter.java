package com.dankal.mylibrary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.bean.UserBean;
import com.dylan.library.adapter.CheckBoxListAdapter;
import com.dylan.library.adapter.CommonAbsListView;

/**
 * Created by Dylan on 2018/2/6.
 */

public class UserDataAdapter extends CheckBoxListAdapter<UserBean,UserDataAdapter.ViewHolder> {


    @Override
    public ViewHolder oncreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.lvitem_user,parent,false));
    }

    @Override
    public void onBindItem(ViewHolder holder, UserBean userBean,final int position) {
       holder.tvUserName.setText(userBean.getName());
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    toCheck(position);
                }else{
                    toUnCheck(position);
                }
            }
        });
    }

    @Override
    public void onCheckState(ViewHolder holder, UserBean userBean,  int position) {
        holder.mCheckBox.setChecked(true);
    }

    @Override
    public void onUnCheckState(ViewHolder holder, UserBean userBean, int position) {
        holder.mCheckBox.setChecked(false);
    }


    class ViewHolder extends CommonAbsListView.ViewHolder{
        private TextView tvUserName;
        private CheckBox mCheckBox;
        public ViewHolder(View convertView) {
            super(convertView);
            tvUserName= (TextView) convertView.findViewById(R.id.tv_username);
            mCheckBox= (CheckBox) convertView.findViewById(R.id.checkbox);
        }
    }
}
