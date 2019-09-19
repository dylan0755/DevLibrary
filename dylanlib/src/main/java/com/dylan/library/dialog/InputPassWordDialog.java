package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.widget.InputPassWordView;
import com.dylan.library.widget.NumberKeyboardView;

/**
 * Created by john
 * on 2019/9/5
 * desc 密码输入Dialog
 */
public class InputPassWordDialog extends Dialog {


    public InputPassWordDialog(@NonNull Context context) {
        super(context);
    }

    public InputPassWordDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        private Context mContext;

        private setOnPassWordInputListener listener;

        private String str = "";

        private String tipText;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder setOnInputReturnListener(setOnPassWordInputListener listener) {
            this.listener = listener;
            return this;
        }


        public Builder setTip(String tipText) {
            this.tipText = tipText;
            return this;
        }

        public InputPassWordDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final InputPassWordDialog dialog = new InputPassWordDialog(mContext, R.style.DLInputPasswordDialog);
            View layout = inflater.inflate(R.layout.dl_dialog_input_password, null);
            final InputPassWordView inputView = layout.findViewById(R.id.dlInputView);
            TextView tvTip = layout.findViewById(R.id.tvTips);
            if (EmptyUtils.isNotEmpty(tipText)){
                tvTip.setText(tipText);
            }
            NumberKeyboardView keyboardView = layout.findViewById(R.id.dlInputKeyBoard);
            keyboardView.setOnNumberClickListener(new NumberKeyboardView.OnNumberClickListener() {
                @Override
                public void onNumberReturn(String number) {
                    if (str.length() >= 6) {
                        return;
                    }
                    str += number;
                    inputView.setText(str);
                    if (str.length() >= 6) {
                        listener.onInputFinish(str);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 200);
                    }
                }

                @Override
                public void onNumberDelete() {
                    if (str.length() <= 1) {
                        str = "";
                        inputView.clear();
                    } else {
                        str = str.substring(0, str.length() - 1);
                    }
                    inputView.setText(str);
                }
            });
            layout.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setContentView(layout);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
            return dialog;
        }
    }


    /**
     * 输入完回调
     */
    public interface setOnPassWordInputListener {
        void onInputFinish(String str);
    }
}
