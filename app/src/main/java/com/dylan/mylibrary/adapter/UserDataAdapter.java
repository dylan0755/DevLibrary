package com.dylan.mylibrary.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.bean.UserBean;
import com.dylan.library.adapter.CheckBoxListAdapter;
import com.dylan.library.adapter.CommonBaseAdapter;

/**
 * Created by Dylan on 2018/2/6.
 */

public class UserDataAdapter extends CheckBoxListAdapter<UserBean,UserDataAdapter.ViewHolder> {




    @Override
    public int getLayoutId() {
        return R.layout.lvitem_user;
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


    class ViewHolder extends CommonBaseAdapter.ViewHolder{
        private TextView tvUserName;
        private CheckBox mCheckBox;
        public ViewHolder(View convertView) {
            super(convertView);
            tvUserName= (TextView) convertView.findViewById(R.id.tv_username);
            mCheckBox= (CheckBox) convertView.findViewById(R.id.checkbox);
        }
    }
}
