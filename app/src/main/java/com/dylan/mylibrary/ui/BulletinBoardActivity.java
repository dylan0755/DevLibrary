package com.dylan.mylibrary.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.ToastUtils;
import com.dylan.library.widget.DanMuView;
import com.dylan.mylibrary.R;
import com.dylan.library.widget.BulletinBoard;
import com.dylan.mylibrary.widget.HotSearchDanMu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan on 2017/5/3.
 */

public class BulletinBoardActivity extends Activity {
    private BulletinBoard bulletinBoard;
    private LinearLayout llDanMuGroup;
    private List<DanMuView> danMuViewList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin);
        initBulletinBoard();
        initDanMuGroup();
    }

    private void initBulletinBoard() {
        String[] messageArray1=getResources().getStringArray(R.array.messageArray1);
        String[] messageArray2=getResources().getStringArray(R.array.messageArray2);
        final List<String> messageList=new ArrayList<>();
        List<String> list= Arrays.asList(messageArray1);
        for (String string:list){
            messageList.add(string);
        }
        final List<String> messageList2= new ArrayList<>();
        List<String> list2= Arrays.asList(messageArray2);
        for (String string:list2){
            messageList2.add(string);
        }


        bulletinBoard = (BulletinBoard) findViewById(R.id.bulletinboard);
        try {
            bulletinBoard.appendMessages(messageList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bulletinBoard.setMoveCallBack(new BulletinBoard.MoveCallBack() {
            @Override
            public void onNest(int position, String message) {

            }

            @Override
            public void onFinish() {
                bulletinBoard.setVisibility(View.GONE);
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    bulletinBoard.setVisibility(View.VISIBLE);
                    bulletinBoard.appendMessages(messageList2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },16000);
    }


    private void initDanMuGroup(){
        llDanMuGroup=findViewById(R.id.llDanMuGroup);
        final List<String> danmuMsgs1 = new ArrayList<>();
        final List<String> danmuMsgs2 = new ArrayList<>();
        final List<String> danmuMsgs3 = new ArrayList<>();

        for (int i = 0; i <10 ; i++) {
            danmuMsgs1.add("1-"+(i+1));
            danmuMsgs2.add("2-"+(i+1));
            danmuMsgs3.add("3-"+(i+1));
        }


        if (EmptyUtils.isEmpty(danMuViewList)) {
            int[] delay = new int[]{1000, 3000, 5000};
            for (int i = 0; i < 3; i++) {
                final DanMuView danMuView = new HotSearchDanMu(this);
                danMuView.setDanMuViewList(danMuViewList);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
                layoutParams.topMargin = DensityUtils.dp2px(this, 15);
                danMuView.setLayoutParams(layoutParams);
                llDanMuGroup.addView(danMuView);
                danMuViewList.add(danMuView);
                final int index = i;
                danMuView.setOnDanMuClickListener(new DanMuView.OnDanMuClickListener<String>() {
                    @Override
                    public void onClick(String str) {
                        ToastUtils.show(str);
                        danMuView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                  for (DanMuView danMuV:danMuViewList){
                                      danMuV.resumeThread();
                                  }
                            }
                        },1000);
                    }
                });


                danMuView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (index == 0) {
                                danMuView.appendMessages(danmuMsgs1);
                            } else if (index == 1) {
                                danMuView.appendMessages(danmuMsgs2);
                            } else {
                                danMuView.appendMessages(danmuMsgs3);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, delay[i]);
            }
        } else {
            //添加新的数据
            try {
                danMuViewList.get(0).appendMessages(danmuMsgs1);
                danMuViewList.get(1).appendMessages(danmuMsgs2);
                danMuViewList.get(2).appendMessages(danmuMsgs3);
                ToastUtils.show("已加入弹幕队列");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
