package com.dylan.library.exception;

import android.util.Log;

import com.dylan.library.io.IOCloser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by Dylan on 2017/1/2.
 */

public class ELog {
    public static void  e(Exception e){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PrintStream printStream=new PrintStream(baos);
        e.printStackTrace(printStream);
        String exception = baos.toString();
        IOCloser.closeIOArray(baos,printStream);
        Log.e("ELog ->e ",""+exception );
    }

    public static void  e(Throwable t){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PrintStream printStream=new PrintStream(baos);
        t.printStackTrace(printStream);
        String exception = baos.toString();
        IOCloser.closeIOArray(baos,printStream);
        Log.e("ELog ->e ",""+exception );
    }

    public static String getExceptionContent(Exception e){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PrintStream printStream=new PrintStream(baos);
        e.printStackTrace(printStream);
        String exception = baos.toString();
        IOCloser.closeIOArray(baos,printStream);
        return exception;
    }

    public static String getThrowableContent(Throwable e){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PrintStream printStream=new PrintStream(baos);
        e.printStackTrace(printStream);
        String exception = baos.toString();
        IOCloser.closeIOArray(baos,printStream);
        return exception;
    }



}
