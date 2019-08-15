package com.dylan.mylibrary.ui.screenshoot;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.utils.ToastUtils;
import com.dylan.mylibrary.R;
import com.dylan.library.io.FileUtils;
import com.dylan.library.screen.ScreenShoot;
import com.dylan.library.test.TestAdapter;

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
        ScreenShoot.captureDecorView(this, picPath, new BitmapHelper.OutPutListenener() {
            @Override
            public void onSuccess() {
                ToastUtils.show("保存成功！  "+picPath);
            }

            @Override
            public void onFailure() {

            }
        });

    }
}
