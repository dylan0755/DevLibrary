package com.dankal.mylibrary.ui.install;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dylan.library.service.AutoInstallService;
import com.dylan.library.service.ServiceHelper;
import com.dylan.library.utils.AppUtils;
import com.dylan.library.utils.FileUtils;

import java.io.File;

public class AutoInstallActivity extends Activity {
    TextView textView;
    private boolean isActvi;
    private static final String INSTALL_PATH="/storage/emulated/0/backups/apps/UtoVRPlayerDemo_1.0.apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoinstall);
        textView= (TextView) findViewById(R.id.tv_activ_state);


        getAccessibilitySate();
        findViewById(R.id.activeButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!isActvi){
                    AutoInstallService.isOpen=true;//安装完自动打开，默认不打开
                    Intent killIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(killIntent,100);
                }



            }
        });

      findViewById(R.id.installButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!new File(INSTALL_PATH).exists())
                    FileUtils.copyAssets2SDcard(AutoInstallActivity.this,"UtoVRPlayerDemo_1.0.apk",INSTALL_PATH);


                AppUtils.toInstall(AutoInstallActivity.this,INSTALL_PATH);

            }
        });

    }

    private void getAccessibilitySate() {
        if (ServiceHelper.isAccessibilitySettingsOn(this,AutoInstallService.class)){
            isActvi=true;
            textView.setText("已开启");
        }else{
            textView.setText("未开启"+" 打开无障碍");

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==100){
            getAccessibilitySate();
        }
    }
}
