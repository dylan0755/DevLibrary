package com.dylan.mylibrary.ui.customtitle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dylan.mylibrary.R;
import com.zhy.autolayout.AutoLinearLayout;

import butterknife.BindView;


/**
 * Created by Dylan on 2016/12/10.
 */

public class CustomTittleUitlActivity extends BaseTitleActivity {
    @BindView(R.id.cv_avatar)
    ImageView cvAvatar;
    @BindView(R.id.tv_doctorname)
    TextView tvDoctorname;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.iv_reduce)
    ImageView ivReduce;
    @BindView(R.id.edt_time)
    EditText edtTime;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_date_select)
    AutoLinearLayout llDateSelect;
    @BindView(R.id.tv_coupons)
    TextView tvCoupons;
    @BindView(R.id.ll_coupons)
    AutoLinearLayout llCoupons;
    @BindView(R.id.tv_pay)
    TextView tvPay;
    @BindView(R.id.label_current_balance)
    TextView labelCurrentBalance;
    @BindView(R.id.tv_current_balance)
    TextView tvCurrentBalance;
    @BindView(R.id.tv_balance_diff)
    TextView tvBalanceDiff;
    @BindView(R.id.tv_charge)
    TextView tvCharge;
    @BindView(R.id.btn_next)
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
