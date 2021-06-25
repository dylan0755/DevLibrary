package com.dylan.library.exception;

/**
 * Author: Dylan
 * Date: 2019/9/30
 * Desc:
 */
public class OnNextBussinesException extends Exception {
     public Exception target;

    public OnNextBussinesException(Exception e){
        target =e;
    }


    @Override
    public String getMessage() {
        return target.getMessage();
    }


}
