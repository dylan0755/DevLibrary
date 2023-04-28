package com.dylan.mylibrary.ui.slidingrefresh;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dylan.library.utils.ToastUtils;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2019/8/8
 * Desc:
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_slidingrefresh, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.tvText.setText("测试测试 "+(i+1));
        viewHolder.tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(viewHolder.tvText.getActivityContext(), "position "+i, Toast.LENGTH_SHORT).show();
                ToastUtils.show("position "+i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 50;
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvText = itemView.findViewById(R.id.tvText);
        }
    }
}
