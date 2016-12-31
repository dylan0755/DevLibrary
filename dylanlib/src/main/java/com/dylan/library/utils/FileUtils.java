package com.dylan.library.utils;

import android.content.Context;
import android.util.Log;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dylan on 2016/12/31.
 */

public class FileUtils {
    /**
     *
     * @param context
     * @param assetsFileName
     * @param outputFilePath 必须是一个文件的路径，而不是文件夹的路径
     */
    public static void copyAssets2SDcard(Context context, String assetsFileName, String outputFilePath) {
        if (context==null)return;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFilePath);
            byte[] buffer = new byte[1024];
            InputStream in = context.getAssets().open(assetsFileName);
            int count;
            while((count= in.read(buffer))!=-1){
                out.write(buffer, 0, count);
            }
            in.close();

            Log.e("copyAssets2SDcard: ","测试" );
            out.close();
           /* closeIO(in);
            closeIO(out);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void closeIO(Closeable closeable){
         if (closeable!=null){
             try {
                 closeable.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
    }


}
