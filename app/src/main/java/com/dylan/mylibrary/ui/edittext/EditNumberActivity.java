package com.dylan.mylibrary.ui.edittext;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dylan.mylibrary.R;
import com.dylan.library.utils.helper.EditNumberHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dylan on 2016/12/20.
 */

public class EditNumberActivity extends Activity {

    @BindView(R.id.edt_duration)
    EditText edtDuration;
    @BindView(R.id.tv_pay)
    TextView tv_pay;
    private int priceUnit = 50;


    private EditNumberHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnumber);
        ButterKnife.bind(this);
        mHelper = new EditNumberHelper(this, 1, 999, 1);
        mHelper.bindEditText(edtDuration);
        mHelper.setMaxValueTip("最大不能超过999");
        mHelper.setInputListener(new EditNumberHelper.InputChangedListener() {
            @Override
            public void onNumberChanged(int numStr) {
                totalMoney(numStr);

            }
        });
    }

    @OnClick({R.id.iv_reduce, R.id.iv_add})
    public void onClick(View view) {
        int num = 0;
        switch (view.getId()) {
            case R.id.iv_reduce:
                mHelper.reduce();
                break;
            case R.id.iv_add:
                mHelper.increase();
                break;
        }
    }

    private void totalMoney(int dur) {
        int pay = dur * priceUnit;
        tv_pay.setText(Integer.toString(pay) + "元");
    }

}