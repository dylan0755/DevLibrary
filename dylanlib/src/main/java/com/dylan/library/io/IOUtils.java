package com.dylan.library.io;

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
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
