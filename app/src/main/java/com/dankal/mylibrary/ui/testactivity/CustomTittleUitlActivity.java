package com.dankal.mylibrary.ui.testactivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.zhy.autolayout.AutoLinearLayout;

import butterknife.Bind;


/**
 * Created by Dylan on 2016/12/10.
 */

public class CustomTittleUitlActivity extends BaseActivity {
    @Bind(R.id.cv_avatar)
    ImageView cvAvatar;
    @Bind(R.id.tv_doctorname)
    TextView tvDoctorname;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.edt_phone)
    EditText edtPhone;
    @Bind(R.id.iv_reduce)
    ImageView ivReduce;
    @Bind(R.id.edt_time)
    EditText edtTime;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.ll_date_select)
    AutoLinearLayout llDateSelect;
    @Bind(R.id.tv_coupons)
    TextView tvCoupons;
    @Bind(R.id.ll_coupons)
    AutoLinearLayout llCoupons;
    @Bind(R.id.tv_pay)
    TextView tvPay;
    @Bind(R.id.label_current_balance)
    TextView labelCurrentBalance;
    @Bind(R.id.tv_current_balance)
    TextView tvCurrentBalance;
    @Bind(R.id.tv_balance_diff)
    TextView tvBalanceDiff;
    @Bind(R.id.tv_charge)
    TextView tvCharge;
    @Bind(R.id.btn_next)
    Button btnNext;

    @Override
    public String getActivityTitle() {
        return "这个标题栏是代码加进来的";
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_customtitleutil;
    }

    @Override
    public void onInitData() {
         edtPhone.setText("13267099664");
    }

}
