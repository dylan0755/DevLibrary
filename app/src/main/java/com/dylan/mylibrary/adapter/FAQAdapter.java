package com.dylan.mylibrary.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.dylan.library.adapter.ChatAdapter;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.bean.Message;

import java.util.List;

public class FAQAdapter extends ChatAdapter<Message> {

    public FAQAdapter(List<Message> list) {
        super(list);
    }

    @Override
    public int getItemViewType(int position, Message msg,boolean isFooter) {
        if (!isFooter){
            switch (msg.getType()) {
                case Message.TYPE_ME: {
                    return R.layout.message_item_right;
                }
                default: {
                    return R.layout.message_item_left;
                }
            }
        }else{
            return R.layout.message_item_footer;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(View itemView, int viewType) {
        return new ViewHolder(itemView);
    }


    public class ViewHolder extends ChatAdapter.ViewHolder<Message> {
        private ImageView ivAiRobot;
       public TextView tvMessage;
       private LinearLayout llOperateItem;
       private TextView tvCopyText,tvImport;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAiRobot=itemView.findViewById(R.id.ivAiRobot);
            tvMessage = itemView.findViewById(R.id.txt_content);

        }
        @Override
        protected void onBind(Message msg) {
            String content=msg.getContent();
            tvMessage.setText(content);



            if (ivAiRobot!=null){
                Glide.with(tvMessage.getContext()).load(R.drawable.gpt_robot).into(ivAiRobot);
            }
        }
    }

}
