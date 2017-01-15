package com.dylan.library.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dylan.library.R;

/**
 * Created by Dylan on 2017/1/15.
 */

public class DialogUtils  {

    public static Dialog createLoadingDialog(Context context,String tipText){
        if (context==null)return null;
        LayoutInflater inflater=LayoutInflater.from(context);
        View contentView=inflater.inflate(R.layout.dialog_loading,null,false);


        TextView tipTextView= (TextView) contentView.findViewById(R.id.tipTextView);
        tipTextView.setText(tipText);


        Dialog dialog=new Dialog(context,R.style.DLDialogStyle);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(contentView,new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        setDialogWindowParam(dialog, Gravity.CENTER,R.style.anim_popWindowStyle);

        dialog.show();
        return dialog;
    }


    public static void closeDialog(Dialog dialog){
        if (dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }

    }


    public static void setDialogWindowParam(Dialog dialog, int gravity,int windowAnimStyleId){
        if (dialog==null)return;
        Window window=dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(gravity);
        window.setAttributes(lp);
        window.setWindowAnimations(windowAnimStyleId);

    }
}
