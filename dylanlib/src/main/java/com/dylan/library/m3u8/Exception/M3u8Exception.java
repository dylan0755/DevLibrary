package com.dylan.library.m3u8.Exception;

/**
 * @author liyaling
 * @email ts_liyaling@qq.com
 * @date 2019/12/14 16:23
 */

public class M3u8Exception extends RuntimeException {
    public M3u8Exception() {
        super();
    }
    public M3u8Exception(String msg) {
        super(msg);
    }

    public M3u8Exception(Exception e){
        super(e);
    }


    public M3u8Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
