package com.dylan.library.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Dylan on 2017/12/9.
 */

public class ClipBoardUtils {



    public static void copy(Context context,String text){
        if (context==null||(text==null||text.isEmpty()))return;
        ClipboardManager myClipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(context,"复制成功",Toast.LENGTH_SHORT).show();
    }


    public static void copy(Context context,String text,String tip){
        if (context==null||(text==null||text.isEmpty()))return;
        ClipboardManager myClipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(context,""+tip,Toast.LENGTH_SHORT).show();
    }

}
