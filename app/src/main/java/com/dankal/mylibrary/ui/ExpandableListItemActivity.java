package com.dankal.mylibrary.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.dankal.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2017/3/24.
 */

public class ExpandableListItemActivity extends Activity {
    private ListView mListView;
    private List strList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandablelistitem);
        mListView= (ListView) findViewById(R.id.id_listview);
        strList=new ArrayList();
        for (int i='A';i<='Z';i++){
            strList.add(""+(char)i);
        }

        ListDataAdapter  adapter=new ListDataAdapter(this,strList);
        mListView.setAdapter(adapter);


    }
}
