package com.dylan.mylibrary.ui.gridviewpager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.testdata.TestDatas;
import com.dylan.library.utils.ToastUtils;
import com.dylan.library.widget.GridViewPager;

import java.util.List;

/**
 * Created by Dylan on 2017/1/15.
 */

public class GridViewPagerActivity extends Activity {
    private GridViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridviewpager);
        mViewPager = (GridViewPager) findViewById(R.id.gvp_main);
        mViewPager.setAttachAdapterListener(new GridViewPager.AttachAdapterListener() {
            @Override
            public void attachAdapter(GridView gridView, List datasPerPage) {
                GridItemAdapter adapter = new GridItemAdapter();
                adapter.bind(datasPerPage);
                gridView.setOnItemClickListener(new ItemClickListener());
                gridView.setAdapter(adapter);
            }
        });

        mViewPager.setDataList(TestDatas.getGridItemData(), 4, 8);//一页4列，一页的数量
    }





    class ItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ToastUtils.show("position "+position);
        }
    }
}
