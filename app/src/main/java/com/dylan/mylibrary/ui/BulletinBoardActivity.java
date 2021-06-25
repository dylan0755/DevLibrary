package com.dylan.mylibrary.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.dylan.mylibrary.R;
import com.dylan.library.widget.BulletinBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan on 2017/5/3.
 */

public class BulletinBoardActivity extends Activity {
    private BulletinBoard bulletinBoard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin);
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


}
