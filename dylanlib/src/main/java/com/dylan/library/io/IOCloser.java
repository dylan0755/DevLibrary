package com.dylan.library.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Dylan on 2017/1/1.
 */

//  IOCloser IO闭合器类
public class IOCloser {

    public static void closeIOArray(Closeable ...closeables){
           if (closeables!=null&&closeables.length>0){
               for (Closeable closeable:closeables){
                    closeIO(closeable);
               }
           }
    }

    public static void closeIO(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
