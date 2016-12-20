package com.dankal.mylibrary.ui.edittext;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dylan.library.util.EditNumberHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dylan on 2016/12/20.
 */

public class EditNumberActivity extends Activity {

    @Bind(R.id.edt_duration)
    EditText edtDuration;
    @Bind(R.id.tv_pay)
    TextView tv_pay;
    private int priceUnit=50;


    private EditNumberHelper mHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnumber);
        ButterKnife.bind(this);
        mHelper=new EditNumberHelper(1,100,1);
        mHelper.bindEditText(edtDuration);
        mHelper.setTextChangedListener(new EditNumberHelper.TextChangedListener() {
            @Override
            public void onTextChanged(int numStr) {
                totalMoney(numStr);
            }
        });
    }

    @OnClick({R.id.iv_reduce, R.id.iv_add})
    public void onClick(View view) {
        int num=0;
        switch (view.getId()) {
            case R.id.iv_reduce:
                num=mHelper.reduce();
                String reduce=Integer.toString(num);
                edtDuration.setText(reduce);
                totalMoney(num);
                break;
            case R.id.iv_add:
                num=mHelper.increase();
                String reduce2=Integer.toString(num);
                edtDuration.setText(reduce2);
                totalMoney(num);
                break;
        }
    }

    private void totalMoney(int dur){
        int pay=dur*priceUnit;
        tv_pay.setText(Integer.toString(pay)+"元");
    }
}
