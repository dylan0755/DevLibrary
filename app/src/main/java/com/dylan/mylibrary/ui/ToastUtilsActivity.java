package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.utils.ToastUtils;
import com.dylan.mylibrary.R;
import com.hjq.toast.CustomToast;
import com.hjq.toast.style.ToastAliPayStyle;
import com.hjq.toast.style.ToastBlackStyle;
import com.hjq.toast.style.ToastQQStyle;
import com.hjq.toast.style.ToastWhiteStyle;

/**
 * Author: Dylan
 * Date: 2021/03/10
 * Desc:
 */
public class ToastUtilsActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);
        findViewById(R.id.btnShort).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ToastUtils.show("短Toast");
            }
        });
        findViewById(R.id.btnShortCenter).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ToastUtils.showCenterShort("centerToast");
            }
        });

        findViewById(R.id.btnLongToast).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ToastUtils.showLong("longToast");
            }
        });
    }

    public void show0(View view){
        Toast.makeText(this, "系统吐司", Toast.LENGTH_SHORT).show();
    }
    public void show1(View v) {
        for (int i = 0; i < 3; i++) {
            ToastUtils.show("我是第" + (i + 1) + "个吐司");
        }
    }

    public void show2(View v) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                ToastUtils.show("我是子线程中弹出的吐司");
            }
        }).start();
    }

    public void show3(View v) {
        ToastUtils.initStyle(new ToastWhiteStyle(getApplication()));
        ToastUtils.show("动态切换白色吐司样式成功");
    }

    public void show4(View v) {
        ToastUtils.initStyle(new ToastBlackStyle(getApplication()));
        ToastUtils.show("动态切换黑色吐司样式成功");
    }

    public void show5(View v) {
        ToastUtils.initStyle(new ToastQQStyle(getApplication()));
        ToastUtils.show("QQ那种还不简单，分分钟的事");
    }

    public void show6(View v) {
        ToastUtils.initStyle(new ToastAliPayStyle(getApplication()));
        ToastUtils.show("支付宝那种还不简单，分分钟的事");
    }

    public void show7(View v) {
        // HJQToastUtils.setView(View.inflate(getApplication(), R.layout.toast_custom_view, null));
        ToastUtils.setView(R.layout.toast_custom_view);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.show("我是自定义Toast");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        // 请注意这段代码强烈建议不要放到实际开发中，因为用户屏蔽通知栏和开启应用状态下的概率极低，可以忽略不计

        // 如果通知栏的权限被手动关闭了
        if (!CustomToast.class.equals(ToastUtils.getToast().getClass()) &&
                !NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            // 因为吐司只有初始化的时候才会判断通知权限有没有开启，根据这个通知开关来显示原生的吐司还是兼容的吐司
            ToastUtils.setToast(new CustomToast(getApplication()));
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show("检查到你手动关闭了通知权限，正在重新初始化 Toast");
                }
            }, 1000);
        }
    }

}