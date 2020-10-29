package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.dylan.library.R;


/**
 * Author: Dylan
 * Date: 2020/4/20
 * Desc:
 */
public abstract class CustomNotFloatingDialog extends Dialog {
    protected abstract int getLayoutId();
    private View contentView;
    private boolean isCancelOutSide=true;

    public CustomNotFloatingDialog(Context context) {
        super(context, R.style.DLCustomNotFloatingDialog);

        final FrameLayout.LayoutParams wrapperLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);


        FrameLayout  wrapLayout = new FrameLayout(context);
        wrapLayout.setLayoutParams(wrapperLp);

        //添加maskView
        FrameLayout maskView = new FrameLayout(context);
        maskView.setLayoutParams(wrapperLp);
        wrapLayout.addView(maskView, 0);
        maskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCancelOutSide){
                    dismiss();
                }
            }
        });


        //添加contentView
        contentView = LayoutInflater.from(context).inflate(getLayoutId(), new FrameLayout(context));
        FrameLayout.LayoutParams contentLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        contentLp.gravity = Gravity.CENTER;
        contentView.setLayoutParams(contentLp);

        contentView.setClickable(true);
        wrapLayout.addView(contentView);


        setContentView(wrapLayout, wrapperLp);

    }


    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        isCancelOutSide=cancel;
    }

    @Override
    public void show() {
        super.show();
    }


    public void setGravity(int gravity) {
        FrameLayout.LayoutParams lp00 = (FrameLayout.LayoutParams) contentView.getLayoutParams();
        lp00.gravity = gravity;
        contentView.postInvalidate();
    }



}
