package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dylan.common.BaseActivity;
import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.widget.irecycler.SmoothScrollLayoutManager;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.FAQAdapter;
import com.dylan.mylibrary.bean.Message;
import com.dylan.mylibrary.widget.ChatItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2023/6/16
 * Desc:
 */
public class ChatLayoutActivity  extends BaseActivity {
    public static final int MSG_UPDATE_CHAT=100;
    public static final int MSG_ADD_CHAT=110;
    public static final int MSG_STREAM_CLOSE=120;
    RecyclerView recyclerView;
    EditText editText;
    private SmoothScrollLayoutManager smoothScrollLayoutManager;
    private FAQAdapter mFaqAdapter;

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull android.os.Message msg) {
            if (msg.what==MSG_UPDATE_CHAT){
                mFaqAdapter.notifyItemChanged(mFaqAdapter.getItemCount()-2);
                smoothScrollLayoutManager.smoothScrollToPosition(recyclerView,mFaqAdapter.getItemCount()-1,1000f);
            }else if (msg.what==MSG_ADD_CHAT){
                Message message= (Message) msg.obj;
                mFaqAdapter.add(message);
                recyclerView.scrollToPosition(mFaqAdapter.getItemCount()-1);
            }
            return false;
        }
    });

    @Override
    public int getLayoutId() {
        return R.layout.activity_chatlayout;
    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        recyclerView=findViewById(R.id.recyclerview);
        smoothScrollLayoutManager =new SmoothScrollLayoutManager(this);
        recyclerView.setLayoutManager(smoothScrollLayoutManager);
        recyclerView.addItemDecoration(new ChatItemDecoration(this, DensityUtils.dp2px(this,8)));
        editText=findViewById(R.id.edtInput);
        findViewById(R.id.btnSend).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (EmptyUtils.isEmpty(editText))return;
                Message  quesMessage=new Message();
                quesMessage.setType(Message.TYPE_ME);
                quesMessage.setContent(editText.getText().toString());
                addChatMessage(quesMessage);
                editText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendQues();
                    }
                },1000);
            }
        });
        smoothScrollLayoutManager =new SmoothScrollLayoutManager(this);
        recyclerView.setLayoutManager(smoothScrollLayoutManager);
        recyclerView.addItemDecoration(new ChatItemDecoration(this, DensityUtils.dp2px(this,8)));
        List<Message> list=new ArrayList<>();
        Message guideMessage=new Message();
        guideMessage.setType(Message.TYPE_OTHER);
        guideMessage.setContent("您好，欢迎使用智能AI助手。请您按照以下提示进行\n语音输入并得到想要的答案： \n\n1. 点击下方语音输入； \n\n2. 说出您想要查询的内容，例如“今天的天气”、“最\n近的新闻”、“帮我生成关于直播的文案”等； \n\n3. 我们会立即为您提供相应的答案，如果您需要进一\n步的帮助，可以再次说出您的问题。 \n\n如需更多帮助，您也可以联系客服");
        list.add(guideMessage);
        mFaqAdapter=new FAQAdapter(list);
        recyclerView.setAdapter(mFaqAdapter);
        recyclerView.setItemAnimator(null);
        recyclerView.setHasFixedSize(true);

    }

    private void addChatMessage(Message chatMessage) {
        android.os.Message message= android.os.Message.obtain();
        message.what=MSG_ADD_CHAT;
        message.obj=chatMessage;
        handler.sendMessage(message);
    }

    private void updateChatMessage(){
        android.os.Message message= android.os.Message.obtain();
        message.what=MSG_UPDATE_CHAT;
        handler.sendMessage(message);
    }

    private void sendQues(){
        Message  quesMessage=new Message();
        quesMessage.setType(Message.TYPE_OTHER);
        addChatMessage(quesMessage);

        new Thread(new Runnable() {
            int count=0;
            @Override
            public void run() {

                while(true){
                    count+=1;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (count<=100){
                        quesMessage.setContent(quesMessage.getContent()+"安");
                    }else{
                        quesMessage.setContent(quesMessage.getContent()+"卓");
                    }

                    updateChatMessage();
                    if (count==200){
                        running=false;
                        break;
                    }
                }
            }
        }).start();
    }

    public boolean running=true;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        running=false;
        handler.removeCallbacksAndMessages(null);
    }
}