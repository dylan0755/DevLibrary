package com.dylan.library.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.exception.ELog;


/**
 * Created by Dylan on 2017/10/20.
 */

public class DLAlertDialog extends DLTransParentDialog implements View.OnClickListener {
    private TextView tvTitle;
    private TextView tvMessage;
    private TextView tvCancel;
    private TextView tvEnsure;
    private CallBack mCallBack;
    protected String defColor ="-16738680";//绿色，AlertDialog默认的颜色
    private int currentCancelTextColor=Integer.parseInt(defColor);
    private int currentEnsureTextColor=Integer.parseInt(defColor);
    public DLAlertDialog(Context context) {
        super(context);
        oncreateView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_alert_base;
    }

    @Override
    public void onCanCelCallBack(DialogInterface dialogInterface) {
        mCallBack.onCancel();
    }

    public void oncreateView() {
        tvTitle = (TextView) findViewById(R.id.id_alert_title);
        tvMessage = (TextView) findViewById(R.id.id_alert_message);
        tvCancel = (TextView) findViewById(R.id.id_alert_cancel);
        tvEnsure = (TextView)findViewById(R.id.id_alert_ensure);
        tvCancel.setOnClickListener(this);
        tvEnsure.setOnClickListener(this);
        tvCancel.setTextColor(Integer.parseInt(defColor));
        tvEnsure.setTextColor(Integer.parseInt(defColor));
        tvCancel.getPaint().setFakeBoldText(true);
        tvEnsure.getPaint().setFakeBoldText(true);
    }

    public void addCallBack(CallBack callBack){
        mCallBack = callBack;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_alert_cancel) {
            if (mCallBack != null) mCallBack.onCancel();
            dismiss();
        } else if (v.getId() == R.id.id_alert_ensure) {
            if (mCallBack != null) mCallBack.onSure();
            dismiss();
        }
    }

    public interface CallBack {
        void onCancel();
        void onSure();
    }


    public void show(String title, String message) {
        tvTitle.setText(title);
        tvMessage.setText(message);
        show();
    }

    public void show(String title, String message,CallBack callBack){
        mCallBack = callBack;
        show(title,message);
    }

    public void setCanCelButtonTextColor(@ColorInt int colorValue){
       try {
           tvCancel.setTextColor(colorValue);
           currentCancelTextColor=colorValue;
       }catch (Exception e){
           ELog.e(e);
       }
    }

    public void setEnsureButtonTextColor(@ColorInt int colorValue){
        try {
            tvEnsure.setTextColor(colorValue);
            currentEnsureTextColor=colorValue;
        }catch (Exception e){
            ELog.e(e);
        }
    }
}
