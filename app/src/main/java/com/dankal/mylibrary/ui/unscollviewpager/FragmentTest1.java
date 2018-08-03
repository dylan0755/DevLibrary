package com.dankal.mylibrary.ui.unscollviewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dankal.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2018/6/23.
 */

public class FragmentTest1 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_test,container,false);
        TextView tvTitle= (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("fragment1");
        List<String> arrayList=new ArrayList<>();
        for (int i=0;i<30;i++){
            arrayList.add(""+i);
        }
        ListView listView= (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(new ArrayAdapter<String>(view.getContext(),R.layout.lvitem_string_list,R.id.tv_listItem,arrayList));
        return view;
    }



}
