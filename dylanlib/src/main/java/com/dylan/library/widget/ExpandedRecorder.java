package com.dylan.library.widget;

import java.util.ArrayList;

/**
 * 该类会记录是哪个ListSlidingLayout正在展开
 * 1.展开时加进来，关闭则从记录器删除
 */
public class ExpandedRecorder {
    private static ExpandedRecorder mRecorder;

    private  ExpandedRecorder(){

    }

    public static ExpandedRecorder getExpandRecorderInstance(){
          if (mRecorder==null){
              mRecorder=new ExpandedRecorder();
          }

        return mRecorder;
    }
    public  ArrayList<ExpandableListItemLayout> layoutList=new ArrayList<ExpandableListItemLayout>();


    public  void deleteExpandingObject(){
        if (layoutList.size()>0){

                  if(layoutList.get(0).expanded){
                      layoutList.get(0).close();
                  }

            layoutList.clear();
        }
    }
    /**
     * 当完全展开时保存这个展开对象
     *
     * */
    public  void Expanding(ExpandableListItemLayout lsl){
        layoutList.add(lsl);
    }
    /**
     * 当完全关闭时将记录删除
     *
     * */
    public  void UnExpanding(ExpandableListItemLayout lsl){
        layoutList.remove(lsl);
    }
    /**
     * 获取记录
     * */
    public ExpandableListItemLayout getExpandingObj(){
        ExpandableListItemLayout lsl=null;
        if (layoutList.size()>0){
            lsl=layoutList.get(0);
        }
        return lsl;
    }
}
