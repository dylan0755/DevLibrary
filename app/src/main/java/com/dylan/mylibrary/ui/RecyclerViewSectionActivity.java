package com.dylan.mylibrary.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dylan.common.BaseActivity;
import com.dylan.library.utils.DensityUtils;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.CeilingAdapter;
import com.dylan.mylibrary.bean.CeilingBean;
import com.dylan.mylibrary.bean.DataUtil;
import com.dylan.mylibrary.listener.PowerGroupListener;
import com.dylan.mylibrary.widget.SectionDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2025/1/3
 * Desc:
 */
public class RecyclerViewSectionActivity extends BaseActivity implements CeilingAdapter.OnItemClickListener{
    RecyclerView mRecyclerView;
    CeilingAdapter mAdapter;
    /**
     * 该集合用来盛放所有item对应悬浮栏的内容
     */
    private List<DataUtil> dataList;
    /**
     * 用来盛放接口返回的数据
     **/
    private List<CeilingBean.BaseBean> beanList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_scrolling;
    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        initRecyclerView();
    }

    /**
     * 制造虚拟数据
     */
    private void initData() {
        beanList=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CeilingBean.ResultBean.FirstBean bean = new CeilingBean.ResultBean.FirstBean();
            bean.setgName("类别一");
            bean.setUname("u1");
            beanList.add(bean);
        }
        for (int i = 0; i < 10; i++) {
            CeilingBean.ResultBean.SecondBean bean = new CeilingBean.ResultBean.SecondBean();
            bean.setgName("类别二");
            bean.setUname("u2");
            beanList.add(bean);
        }
        for (int i = 0; i < 16; i++) {
            CeilingBean.ResultBean.ThirdBean bean = new CeilingBean.ResultBean.ThirdBean();
            bean.setgName("类别三");
            bean.setUname("u3");
            beanList.add(bean);
        }

        setPullAction();
    }

    /**
     * 给子item添加父布局
     */
    private void setPullAction() {
        dataList = new ArrayList<>();
        if(beanList!=null && beanList.size()>0){
            for (int i=0;i<beanList.size();i++)
            {
                DataUtil data = new DataUtil();
                String gName = beanList.get(i).getgName();
                if("类别一".equals(gName))
                {
                    data.setName("最新类别1");
                    data.setIcon(R.drawable.ic_text);
                }else if("类别二".equals(gName)){
                    data.setName("最新类别2");
                    data.setIcon(R.drawable.ic_picture_pressed);
                }else if("类别三".equals(gName)){
                    data.setName("最新类别3");
                    data.setIcon(R.drawable.ic_video_pressed);
                }
                dataList.add(data);
            }
        }
    }
    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rlv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();
        mAdapter = new CeilingAdapter(this, beanList);
        mRecyclerView.setAdapter(mAdapter);
        //添加悬浮布局
        initDecoration();
        mAdapter.setOnItemClickListener(this);
    }
    /**
     * 添加悬浮布局
     */
    private void initDecoration() {
        SectionDecoration decoration = SectionDecoration.Builder
                .init(new PowerGroupListener() {
                    @Override
                    public String getGroupName(int position) {
                        //获取组名，用于判断是否是同一组
                        if (dataList.size() > position) {
                            return dataList.get(position).getName();
                        }
                        return null;
                    }
                    @Override
                    public View getGroupView(int position) {
                        //获取自定定义的组View
                        if (dataList.size() > position) {
                            View view = getLayoutInflater().inflate(R.layout.item_group, null, false);
                            ((TextView) view.findViewById(R.id.tv)).setText(dataList.get(position).getName());
                            ((ImageView) view.findViewById(R.id.iv)).setImageResource(dataList.get(position).getIcon());
                            return view;
                        } else {
                            return null;
                        }
                    }
                })
                //设置高度
                .setGroupHeight(DensityUtils.dp2px(this, 40))
                .build();
        mRecyclerView.addItemDecoration(decoration);
    }
    /**
     * item的单击事件
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "你点击的位置是"+position, Toast.LENGTH_SHORT).show();
    }
}
