package com.dylan.library.io;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dylan on 2017/10/27.
 */

public class IOUtils {

    public static String readString(InputStream in){
        try {
            byte buff[] = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            do {
                int numread = in.read(buff);
                if (numread <= 0) {
                    break;
                }
                baos.write(buff, 0, numread);
            } while (true);
            in.close();
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while (-1 != (n = inputStream.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    public static byte[] getBytes(AssetManager assetManager, String fileName) {
        try {
            return getBytes(assetManager.open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public static String readTextFileFromResource(Context context, int resourceId) throws IOException {
        return new String(getBytes(context.getResources().openRawResource(resourceId)));
    }
}
