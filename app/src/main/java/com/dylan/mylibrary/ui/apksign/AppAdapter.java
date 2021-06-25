package com.dylan.mylibrary.ui.apksign;

import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dylan.library.utils.ClipBoardUtils;
import com.dylan.library.utils.SignatureUtils;
import com.dylan.mylibrary.R;

import java.util.HashSet;


/**
 * Created by Dylan on 2017/12/9.
 */

public class AppAdapter extends BothItemAdapter<PackageInfo> {
    private int defColor;
    private HashSet<Integer> hashSet=new HashSet<>();
    public AppAdapter(){
        defColor= Color.parseColor("#666666");
    }
    @Override
    public boolean isNormalItem(int position) {
        if (!(dataList.get(position) instanceof PackInfoDivider)){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public View getViewAsTagItem(View convertView, PackageInfo packageInfo, ViewGroup parent, int position) {
        PackInfoDivider packInfoDivider = (PackInfoDivider) packageInfo;
        convertView=mInflater.inflate(R.layout.lvitem_divider_packtype,parent,false);
        TextView textView= (TextView) convertView.findViewById(R.id.tv_divider_packtype);
        if (packInfoDivider.isApp){
            textView.setText("第三方应用");
            textView.setTextColor(Color.parseColor("#333333"));
        }else{
            textView.setText("系统应用");
            textView.setTextColor(Color.parseColor("#333333"));
        }
        return textView;
    }

    @Override
    public View getViewAsNormalItem(View convertView, final PackageInfo packageInfo, ViewGroup parent, final int position) {
        ViewHolder holder=null;
        if (convertView==null){
            convertView=mInflater.inflate(R.layout.lvitem_packifno, parent, false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        final String versionName=packageInfo.versionName;
        final  String appName= (String) packageInfo.applicationInfo.loadLabel(mContext.getPackageManager());
        final String packName=packageInfo.packageName;
        Drawable drawable=packageInfo.applicationInfo.loadIcon(mContext.getPackageManager());
        holder.ivPackIcon.setImageDrawable(drawable);
        holder.tvAppName.setText(appName+"       V"+versionName);
        holder.tvPackName.setText(packName);

        holder.tvSignature.setText(SignatureUtils.getMD5Signature(mContext,packName));
        holder.tvSignature.setTextColor(defColor);
        final TextView tvSignature=holder.tvSignature;

        holder.btnCopy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String packInfo="应用名称："+appName+"\n版本："+versionName+"\n包名："+packName+"\n签名："+tvSignature.getText().toString();
                ClipBoardUtils.copy(mContext,packInfo);
            }
        });

        if (hashSet.contains(position)){
            tvSignature.setText(SignatureUtils.getMD5Signature(mContext,packName));
            tvSignature.setTextColor(Color.RED);
        }

        return convertView;
    }


    class ViewHolder {
        ImageView ivPackIcon;
        TextView tvAppName;
        TextView tvPackName;
        TextView tvSignature;
        Button btnCopy;
        public ViewHolder(View convertView) {
            ivPackIcon= (ImageView) convertView.findViewById(R.id.ivpacklogo);
            tvPackName= (TextView) convertView.findViewById(R.id.tvpackName);
            tvSignature= (TextView) convertView.findViewById(R.id.tvSignature);
            btnCopy= (Button) convertView.findViewById(R.id.btn_copy);
            tvAppName= (TextView) convertView.findViewById(R.id.tv_app_name);
        }
    }
}
