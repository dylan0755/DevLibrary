package com.dylan.library.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import com.hjq.toast.Toaster;

/**
 * Created by Dylan on 2017/12/9.
 */

public class ClipBoardUtils {



    public static void copy(Context context,String text){
        if (context==null||(text==null||text.isEmpty()))return;
        ClipboardManager myClipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        if (Toaster.isInit()){
            Toaster.show("复制成功");
        }else{
            Toast toast=Toast.makeText(context, null, Toast.LENGTH_SHORT);
            toast.setText("复制成功");
            toast.show();
        }

    }


    public static void copy(Context context,String text,String tip){
        if (context==null||(text==null||text.isEmpty()))return;
        ClipboardManager myClipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        if (Toaster.isInit()){
            Toaster.show(tip);
        }else{
            Toast toast=Toast.makeText(context, null, Toast.LENGTH_SHORT);
            toast.setText(tip);
            toast.show();
        }

    }


    public static void copyNoToast(Context context,String text){
        if (context==null||(text==null||text.isEmpty()))return;
        ClipboardManager myClipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
    }


    public static String getPasteString(final Context context){
        String pasteString="";
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            CharSequence text = clipData.getItemAt(0).getText();
            if (text!=null){
                pasteString=text.toString();
                return pasteString;
            }
        }
        return pasteString;
    }

}
