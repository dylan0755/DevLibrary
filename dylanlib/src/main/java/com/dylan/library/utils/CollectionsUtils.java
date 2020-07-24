package com.dylan.library.utils;

import android.util.SparseArray;

import com.dylan.library.bean.MergeCell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/2/12
 * Desc:
 */
public class CollectionsUtils {


    /**
     * 两个集合合并且去重处理
     *
     * @param firstList
     * @param secondList
     * @return
     */
    public static List<MergeCell> mergeList(List<? extends MergeCell> firstList, List<? extends MergeCell> secondList) {
        if (EmptyUtils.isEmpty(firstList) && EmptyUtils.isEmpty(secondList))
            return Collections.emptyList();


        //直接返回第一个集合
        if (EmptyUtils.isNotEmpty(firstList) && EmptyUtils.isEmpty(secondList)) {
            return (List<MergeCell>) firstList;
        }

        //直接返回第二个集合
        if (EmptyUtils.isEmpty(firstList) && EmptyUtils.isNotEmpty(secondList)) {
            return (List<MergeCell>) secondList;
        }

        HashMap<String, MergeCell> targetMap = new HashMap<>();
        //两份数据合并
        for (MergeCell mergeCell : firstList) {
            targetMap.put(mergeCell.getKey(), mergeCell);
        }
        for (MergeCell mergeCell : secondList) {
            if (!targetMap.containsKey(mergeCell.getKey())) {
                targetMap.put(mergeCell.getKey(), mergeCell);
            }
        }

        List<MergeCell> targeList = new ArrayList<>();
        Iterator<String> iterator = targetMap.keySet().iterator();
        while (iterator.hasNext()) {
            String ID = iterator.next();
            MergeCell mergeCell = targetMap.get(ID);
            targeList.add(mergeCell);
        }
        return targeList;
    }


    public static <T> List<T> createEntityList(Class<T> clazz, int count) {
        if (count <= 0) count = 1;
        List<T> entityList = new ArrayList<>(count);
        try {
            for (int i = 0; i < count; i++) {
                entityList.add(clazz.newInstance());
            }
            return entityList;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();

    }


    public static <T> SparseArray<List<T>> grouping(List<T> sourceList, int countPerPage) {
        SparseArray<List<T>> sparseArray = new SparseArray();
        if (EmptyUtils.isEmpty(sourceList)) return sparseArray;

        int totalSize = sourceList.size();
        int pageCount = totalSize / countPerPage;//计算可以分多少页
        int remain = totalSize % countPerPage;               //剩余不够一页的作为1页
        if (remain != 0) pageCount += 1;


        if (pageCount == 1) {
            sparseArray.put(0, sourceList);
            sparseArray.put(1, new ArrayList<T>());
        } else {
            for (int i = 0; i < totalSize; i++) {
                //计算该元素是第n页
                int item = (i + 1) /countPerPage;
                int itemRemain=(i+1)%countPerPage;
                if (itemRemain!=0)item+=1;


                int belongPageIndex=item-1;
                List<T> pageList=sparseArray.get(belongPageIndex);
                if (pageList==null){
                    pageList=new ArrayList<>();
                    sparseArray.put(belongPageIndex,pageList);
                }
                pageList.add(sourceList.get(i));
            }
        }

        return sparseArray;

    }




}
