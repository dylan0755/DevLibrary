package com.dylan.library.widget.irecycler.paging;

import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/3/27
 * Desc:
 */
public class IRecyclerPage {
    private boolean isLastPage;
    private List<?> list;
    private boolean isSucceed;
    private String failureMsg;


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


    public boolean isSucceed() {
        return isSucceed;
    }

    public void setSucceed(boolean succeed) {
        isSucceed = succeed;
    }

    public String getFailureMsg() {
        return failureMsg;
    }

    public void setFailureMsg(String failureMsg) {
        this.failureMsg = failureMsg;
    }


}
