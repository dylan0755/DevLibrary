package com.dylan.library.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2016/9/26.
 */
public class SearchTool {
    private SharedPreferences sp_history;
    private SharedPreferences sp_hotsearch;//热词

    private final String DEFAULT_NAME = "search_history";
    private String mHistoryPreferenceName = DEFAULT_NAME;
    private String mHotSearchPreferenceName = "hot_search";
    private List<String> mFilterList;
    private int historyItemCount = 5;//显示历史搜素的个数
    private int hotSearchItemCount = 8;
    private BaseAdapter mHistoryAdapter;

    public SearchTool(Context context, List<String> filterList) {
        sp_history = context.getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
        sp_hotsearch = context.getSharedPreferences(mHotSearchPreferenceName, Context.MODE_PRIVATE);
        mFilterList = filterList;
    }

    public SearchTool(Context context, String historyprefrencename, String hostsearch_prefrenname, List<String> filterList) {
        mHistoryPreferenceName = historyprefrencename;
        mHotSearchPreferenceName = hostsearch_prefrenname;
        sp_history = context.getSharedPreferences(mHistoryPreferenceName, Context.MODE_PRIVATE);
        mFilterList = filterList;
    }

    public void setShowHistoryCount(int count) {
        historyItemCount = count;
    }

    public void setShowHotSearchItemCount(int count) {
        hotSearchItemCount = count;
    }


    public void saveSearchFilter() {
        if (mFilterList.size()>1){
            if (mFilterList.get(0).equals(mFilterList.get(1))){
                mFilterList.remove(0);//去重
            }
        }
        SharedPreferences.Editor editor = sp_history.edit();
        for (int i = 0; i < mFilterList.size(); i++) {
            editor.putString(String.valueOf(i), mFilterList.get(i));
        }
        editor.commit();
        if (mHistoryAdapter!=null){
            mHistoryAdapter.notifyDataSetChanged();
        }
    }

    public void saveHotSearch(List<String> list) {
        if (list != null && list.size() > 0) {
            SharedPreferences.Editor editor = sp_hotsearch.edit();
            for (int i = 0; i < list.size(); i++) {
                editor.putString(String.valueOf(i), list.get(i));
            }
            editor.commit();
        }
    }

    public List<String> readHotSearch() {
        List<String> list = new ArrayList();
        for (int i = 0; i < hotSearchItemCount; i++) {
            String filter = sp_hotsearch.getString(String.valueOf(i), null);
            if (isValid(filter)) {
                list.add(filter);
            }
        }
        return list;
    }

    public void clearAllHotSearch() {
        SharedPreferences.Editor editor = sp_hotsearch.edit();
        editor.clear();
        editor.commit();

    }


    public void removeSearchItem(int i){
        mFilterList.remove(i);
        if (mHistoryAdapter!=null){
            mHistoryAdapter.notifyDataSetChanged();
        }
    }

    public void removeSearchItem(String name){
        mFilterList.remove(name);
        if (mHistoryAdapter!=null){
            mHistoryAdapter.notifyDataSetChanged();
        }
    }

    public void attachHistoryAdapter(BaseAdapter baseAdapter){
        mHistoryAdapter =baseAdapter;
    }

    public void readSearchFilter() {
        mFilterList.clear();
        for (int i = 0; i < historyItemCount; i++) {
            String filter = sp_history.getString(String.valueOf(i), null);
            if (isValid(filter)) {
                mFilterList.add(filter);
            }
        }
        if (mHistoryAdapter!=null){
            mHistoryAdapter.notifyDataSetChanged();
        }
    }

    public void clearSearchHistory() {
        SharedPreferences.Editor editor = sp_history.edit();
        editor.clear();
        editor.commit();
    }


    public boolean isValid(String str) {
        if (str != null && !str.isEmpty()) {
            return true;
        } else if (str == null) {
            return false;
        } else {
            return false;
        }
    }


}
