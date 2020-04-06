package com.dylan.library.widget.irecycler.paging;

import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/3/27
 * Desc:
 */
public class IRecyclerPage {
    private int total; //总数
    private int pageNum;//当前页数
    private int pageSize; //一页数量
    private int currentSize;//当前页数量
    private boolean isLastPage;
    private List<?> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
