package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.screen.NavBarUtils;


/**
 * Created by Dylan on 2017/11/20.
 */

public class LoadingDialog extends Dialog {
    private TextView tipTextView;
    private View backgroudView;
    private ProgressBar loadingBar;
    private boolean hideNavWhileShowing;//弹出对话框是否隐藏虚拟按键 达到全屏效果


    private LoadingDialog(@NonNull Context context) {
        super(context, R.style.LoadingDialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.dl_dialog_loading);
        loadingBar=findViewById(R.id.loadingBar);
        tipTextView=findViewById(R.id.tipTextView);
        backgroudView=findViewById(R.id.rootView);
    }


    @Override
    public void show() {
        if (hideNavWhileShowing){
            NavBarUtils.setNotFocusableFlag(getWindow());
        }
        super.show();
        if (hideNavWhileShowing){
            NavBarUtils.hideNavBar(getWindow());
            NavBarUtils.clearNotFocusableFlag(getWindow());
        }
    }


    public void hideNavBar(){
        hideNavWhileShowing=true;
    }







    public void setLoadingTipText(String tipText) {
        tipTextView.setText(tipText);
        if (tipText!=null&&!tipText.isEmpty()){
            tipTextView.setVisibility(View.VISIBLE);
        }else{
            tipTextView.setVisibility(View.GONE);
        }

    }

    public View getBackgroundView(){
        return backgroudView;
    }

    public TextView getLoadingTextView(){
        return tipTextView;
    }

    public ProgressBar getLoadingBar(){
        return loadingBar;
    }


    public static class Builder {
         private Context context;
         private boolean hideNavWhileShowing=false;
         private String loadingTip;

         public Builder(Context context){
              this.context=context;
         }


        public Builder hideNavBar(boolean bl){
            hideNavWhileShowing=bl;
            return this;
        }

        public Builder setLoadingTipText(String tipText) {
            loadingTip=tipText;
            return this;
        }


        public LoadingDialog build(){
            LoadingDialog loadingDialog=new LoadingDialog(context);
            loadingDialog.setLoadingTipText(loadingTip);
            if (hideNavWhileShowing)loadingDialog.hideNavBar();
            return loadingDialog;
        }
    }




}
