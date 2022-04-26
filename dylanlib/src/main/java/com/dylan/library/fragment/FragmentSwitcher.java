package com.dylan.library.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Dylan on 2016/11/6.
 */

public class FragmentSwitcher {
    public static String CURRENT_INDEX = "currentIndex";
    public static String FRAGMENT_KEY_ARRAY = "keyarray";
    private int mContainerId;
    private int currentIndex = -1;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;
    private List<Class<Fragment>> list = new ArrayList<>();
    private HashMap<Integer, Fragment> mReferenceMap = new HashMap<Integer, Fragment>();



    /**
     * @param containerId 用来盛放Fragment的布局
     */


    public FragmentSwitcher(FragmentManager manager, int containerId) {
        mContainerId = containerId;
        mFragmentManager = manager;
    }

    public void attachFragmentClass(Class... classes) {
        inits(classes);
    }


    //在Activity 中使用， Fragment 中不需要
    public int  restoreFragments(Bundle savedInstanceState) {
        if (mFragmentManager == null) return 0;
        int[] key_arr=savedInstanceState.getIntArray(FRAGMENT_KEY_ARRAY);
        if (key_arr==null)return 0;
        for (int i = 0; i < key_arr.length; i++) {
            int key=key_arr[i];
            Fragment fragment = mFragmentManager.getFragment(savedInstanceState, "fragment_"+key);
            if (fragment!=null) mReferenceMap.put(key, fragment);
        }
        int currentIndex = savedInstanceState.getInt(CURRENT_INDEX);
        select(currentIndex);
        return currentIndex;
    }


    //在Activity 中使用， Fragment 中不需要
    public void saveFragmentInstanceSate(Bundle outState) {
        if (mFragmentManager!= null && mReferenceMap.size() > 0) {
            int index = 0;
            int[] fragment_key = new int[mReferenceMap.size()];
            Iterator iter = mReferenceMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                int key = (int) entry.getKey();
                Fragment fragment=getFragment(key);
                if (fragment!=null&&fragment.isAdded()){
                    mFragmentManager.putFragment(outState, "fragment_" +key, fragment);
                    fragment_key[index] = key;
                }
                index++;
            }
            outState.putIntArray(FRAGMENT_KEY_ARRAY,fragment_key);
            outState.putInt(CURRENT_INDEX, currentIndex);
        }
    }


    private void inits(Class... classes) {
        for (Class c : classes) {
            list.add(c);
        }
    }


    public void select(int index) {
       select(index,null);
    }

    public void select(int index,Bundle bundle){
        if (currentIndex == index) return;
        mTransaction = mFragmentManager.beginTransaction();
        hide();
        if (mReferenceMap.get(index) == null) {
            Fragment fragment=newInstance(index);
            if (fragment!=null&&bundle!=null)fragment.setArguments(bundle);
            mTransaction.add(mContainerId,fragment);
        } else {
            mTransaction.show(mReferenceMap.get(index));
        }
        mTransaction.commitAllowingStateLoss();
        currentIndex = index;
    }

    private void hide() {//隐藏所有
        Iterator iter = mReferenceMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            int key = (int) entry.getKey();
            mTransaction.hide(mReferenceMap.get(key));
        }
    }


    private Fragment newInstance(int position) {
        try {
            Fragment fragment = list.get(position).newInstance();
            mReferenceMap.put(position, fragment);
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public Fragment getFragment(int index) {
        Fragment fragment = mReferenceMap.get(index);
        return fragment;
    }

    public List<Fragment> getFragments(){
        Iterator iterator=mReferenceMap.entrySet().iterator();
        List<Fragment> fragmentList=null;
        while (iterator.hasNext()){
            if (fragmentList==null)fragmentList=new ArrayList<>();
            Map.Entry<Integer,Fragment> entry= (Map.Entry<Integer, Fragment>) iterator.next();
            fragmentList.add(entry.getValue());
        }
        return fragmentList;
    }


    public void replace(int index){
        Fragment fragment=getFragment(index);
        if (fragment!=null){
            Fragment newFragment=newInstance(index);
            mFragmentManager.beginTransaction().replace(mContainerId,newFragment).commit();
        }
    }
}
