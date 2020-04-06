package com.dylan.library.search;

import android.content.Context;
import android.content.SharedPreferences;

import com.dylan.library.adapter.CommonBaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2016/9/26.
 */
public class SearchHelper {


    private SharedPreferences sp_history;

    private final String DEFAULT_NAME = "search_history";
    private List<String> keyWordList =new ArrayList<>();
    private int historyItemCount = 5;//显示历史搜素的个数
    private CommonBaseAdapter mHistoryAdapter;
    public static String CLEAR_RECORD_TEXT="清空搜索记录";

    public SearchHelper(Context context) {
        sp_history = context.getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
    }



    public void setShowHistoryCount(int count) {
        historyItemCount = count;
    }

    public void setClearTipText(String text){
        CLEAR_RECORD_TEXT=text;
    }


    private void saveSearchFilter() {
        if (keyWordList.size()>1){
            if (keyWordList.get(0).equals(keyWordList.get(1))){
                keyWordList.remove(0);//去重
            }
        }
        SharedPreferences.Editor editor = sp_history.edit();
        for (int i = 0; i < keyWordList.size(); i++) {
            editor.putString(String.valueOf(i), keyWordList.get(i));
        }
        editor.commit();

    }



    public void addSearchItem(String keyWord){
        if (keyWordList.isEmpty()){
            keyWordList.add(CLEAR_RECORD_TEXT);
        }
        if (keyWordList.size()>historyItemCount){
            keyWordList.remove(keyWordList.size()-2);
            keyWordList.add(0,keyWord);
        }else{
            keyWordList.add(0,keyWord);
        }

        saveSearchFilter();
        mHistoryAdapter.bind(keyWordList);

    }







    public void removeSearchItem(int i){
        keyWordList.remove(i);
        saveSearchFilter();
        if (mHistoryAdapter!=null){
            mHistoryAdapter.notifyDataSetChanged();
        }
    }

    public void removeSearchItem(String name){
        keyWordList.remove(name);
        saveSearchFilter();
        if (mHistoryAdapter!=null){
            mHistoryAdapter.notifyDataSetChanged();
        }
    }

    public void attachHistoryAdapter(CommonBaseAdapter baseAdapter){
        mHistoryAdapter =baseAdapter;
    }

    public void readSearchFilter() {
        keyWordList.clear();
        for (int i = 0; i < historyItemCount; i++) {
            String filter = sp_history.getString(String.valueOf(i), null);
            if (isValid(filter)) {
                keyWordList.add(filter);
            }
        }
        if (mHistoryAdapter!=null){
            mHistoryAdapter.bind(keyWordList);
        }
    }

    public void clearSearchHistory() {
        SharedPreferences.Editor editor = sp_history.edit();
        editor.clear();
        editor.commit();
        keyWordList.clear();
        if (mHistoryAdapter!=null){
            mHistoryAdapter.notifyDataSetChanged();
        }
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
