package com.dylan.mylibrary.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dylan.mylibrary.R;
import com.dylan.library.screen.ScaleUtils;

/**
 * Created by Dylan on 2018/3/9.
 */

public class CustomToastActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customtoast);
    }


    public void showToast(View v){
        Toast toast=new Toast(this);
        View toastView= LayoutInflater.from(this).inflate(R.layout.toast_appoint_success,null);
        toastView.setBackgroundResource(R.drawable.shape_appointsuccess_toast);

        TextView textView= (TextView) toastView.findViewById(R.id.tv_toast);
        textView.setText("预约成功,我们会在直播开始前短信通知您");
        if (toastView.getBackground()!=null){
            toastView.getBackground().setAlpha(205);
        }
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        ScaleUtils scaleUtils=new ScaleUtils(this);
        toast.setGravity(Gravity.BOTTOM,0,scaleUtils.toScaleSize(500));
        toast.show();
    }


}
