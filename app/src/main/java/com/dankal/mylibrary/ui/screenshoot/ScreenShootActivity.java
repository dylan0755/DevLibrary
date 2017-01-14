package com.dankal.mylibrary.ui.screenshoot;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dankal.mylibrary.R;
import com.dylan.library.file.FileUtils;
import com.dylan.library.screen.ScreenShoot;
import com.dylan.library.test.TestAdapter;
import com.dylan.library.graphics.BitmapUtils;
import com.dylan.library.utils.ToastUtil;

/**
 * Created by Dylan on 2017/1/1.
 */

public class ScreenShootActivity extends Activity {
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshoot);
        mRecyclerView= (RecyclerView) findViewById(R.id.rv_screenshoot);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        TestAdapter madapter=new TestAdapter();
        mRecyclerView.setAdapter(madapter);



    }



    public void shoot(View view){
        Log.e( "shoot: ","响应" );

        final String picPath= FileUtils.getSDCardDir()+"/"+"decordView.jpg";
       // ScreenShoot.captureDecorView(this,picPath);
        ScreenShoot.captureDecorView(this, picPath, new BitmapUtils.OutPutListenener() {
            @Override
            public void onSuccess() {
                ToastUtil.toToast("保存成功！  "+picPath);
            }
        });

    }
}
