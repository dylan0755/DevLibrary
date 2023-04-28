package com.dylan.library.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2022/4/7
 * Desc:
 */

public class DocumentFileReader {

    private Context mContext;
    private List<DocumentFile> documentFiles;

    public DocumentFileReader(Context context) {
        mContext = context;
        documentFiles = new ArrayList<>();
    }


    public List<DocumentFile> getFileFromAndroidDataDirectory (String dirPath) throws Exception {//从 指定文件夹加载路径
        documentFiles.clear();
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
        if (!dirPath.contains("Android/data"))throw new Exception("Invalid path, dir path format :  Android/data/xxxx");
        String prefix="Android/data/";
        dirPath=dirPath.substring(dirPath.indexOf(prefix)+prefix.length());
        final String[] dirsName=dirPath.split("/");
        if (EmptyUtils.isEmpty(dirsName))return documentFiles;
        DocumentFile documentFile = DocumentFile.fromTreeUri(mContext, uri);
        DocumentFile[] files = documentFile.listFiles();
        for (DocumentFile file : files) {
            if (dirsName[0].equals(file.getName())) {
                DocumentFile lastStep=file;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (int i=1;i<dirsName.length;i++){
                        DocumentFile targetFile=findSubDir(lastStep.getUri(),dirsName[i]);
                        if (targetFile==null){
                            return documentFiles;
                        }
                        lastStep=targetFile;
                        if (i==dirsName.length-1){
                            loadFile(lastStep.getUri());
                        }
                    }
                }
                break;
            }
        }
        return documentFiles;
    }



    //查找子目录
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private DocumentFile findSubDir(Uri dirUri, String dirName) {
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri));
        Cursor cursor = mContext.getContentResolver().query(childrenUri, new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String documentId = cursor.getString(0);
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(dirUri, documentId);
                DocumentFile file = DocumentFile.fromSingleUri(mContext, uri);
                if (dirName.equals(file.getName())) {
                    cursor.close();
                    return file;
                }
            }
            cursor.close();
        }
        return null;
    }







    public void readWxVideoCache() {// com.tencent.mm->MicroMsg->
        documentFiles.clear();
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
        DocumentFile documentFile = DocumentFile.fromTreeUri(mContext, uri);
        DocumentFile[] files = documentFile.listFiles();
        for (DocumentFile file : files) {
            if ("com.tencent.mm".equals(file.getName())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    readMicroMsg(file.getUri());
                }
                break;
            }
        }

        if (EmptyUtils.isEmpty(documentFiles)){
            readWxVideoCache2();
        }
    }

    private void readWxVideoCache2() {// com.tencent.mm->cache->
        documentFiles.clear();
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
        DocumentFile documentFile = DocumentFile.fromTreeUri(mContext, uri);
        DocumentFile[] files = documentFile.listFiles();
        for (DocumentFile file : files) {
            if ("com.tencent.mm".equals(file.getName())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    readCache(file.getUri());
                }
                break;
            }
        }
    }

    public void readWxDownLoadDir() {
        documentFiles.clear();
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
        DocumentFile documentFile = DocumentFile.fromTreeUri(mContext, uri);
        DocumentFile[] files = documentFile.listFiles();
        for (DocumentFile file : files) {
            if ("com.tencent.mm".equals(file.getName())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    readMicroMsgAndDownLoad(file.getUri());
                }
                break;
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void readMicroMsgAndDownLoad(Uri dirUri) {
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri));
        Cursor cursor = mContext.getContentResolver().query(childrenUri, new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String documentId = cursor.getString(0);
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(dirUri, documentId);
                DocumentFile f = DocumentFile.fromSingleUri(mContext, uri);
                if ("MicroMsg".equals(f.getName())) {
                    readDownLoad(f.getUri());
                    break;
                }
            }
            cursor.close();
        }
    }





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void readMicroMsg(Uri dirUri) {
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri));
        Cursor cursor = mContext.getContentResolver().query(childrenUri, new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String documentId = cursor.getString(0);
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(dirUri, documentId);
                DocumentFile f = DocumentFile.fromSingleUri(mContext, uri);
                if ("MicroMsg".equals(f.getName())) {
                    readMD5Dir(f.getUri());
                    break;
                }
            }
            cursor.close();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void readCache(Uri dirUri) {
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri));
        Cursor cursor = mContext.getContentResolver().query(childrenUri, new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String documentId = cursor.getString(0);
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(dirUri, documentId);
                DocumentFile f = DocumentFile.fromSingleUri(mContext, uri);
                if ("cache".equals(f.getName())) {
                    readMD5Dir(f.getUri());
                    break;
                }
            }
            cursor.close();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void readMD5Dir(Uri dirUri) {
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri));
        Cursor cursor = mContext.getContentResolver().query(childrenUri, new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String documentId = cursor.getString(0);
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(dirUri, documentId);
                DocumentFile f = DocumentFile.fromSingleUri(mContext, uri);
                String listname = f.getName();
                String var = listname.substring(listname.lastIndexOf("/") + 1, listname.length());
                if (var.length() == 32) {
                    readFinder(f.getUri());
                }
            }
            cursor.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void readDownLoad(Uri dirUri) {
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri));
        Cursor cursor = mContext.getContentResolver().query(childrenUri, new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String documentId = cursor.getString(0);
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(dirUri, documentId);
                DocumentFile f = DocumentFile.fromSingleUri(mContext, uri);
                if ("Download".equals(f.getName())) {
                    loadFileInDownLoad(f.getUri());
                    break;
                }
            }
            cursor.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadFileInDownLoad(Uri dirUri) {
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri));
        Cursor cursor = mContext.getContentResolver().query(childrenUri, new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String documentId = cursor.getString(0);
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(dirUri, documentId);
                DocumentFile f = DocumentFile.fromSingleUri(mContext, uri);
                if (f.isFile()){
                    documentFiles.add(f);
                }

            }
            cursor.close();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void readFinder(Uri dirUri) {
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri));
        Cursor cursor = mContext.getContentResolver().query(childrenUri, new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String documentId = cursor.getString(0);
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(dirUri, documentId);
                DocumentFile f = DocumentFile.fromSingleUri(mContext, uri);
                if ("finder".equals(f.getName())) {
                    cursor.close();
                    readVideoDir(f.getUri());
                    return;
                }
            }
            cursor.close();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void readVideoDir(Uri dirUri) {
        Logger.e("dirUri=" + dirUri.toString());
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri));
        Cursor cursor = mContext.getContentResolver().query(childrenUri, new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String documentId = cursor.getString(0);
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(dirUri, documentId);
                DocumentFile f = DocumentFile.fromSingleUri(mContext, uri);
                if ("video".equals(f.getName())) {
                    cursor.close();
                    loadFile(f.getUri());
                    return;
                }
            }
            cursor.close();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadFile(Uri dirUri) {
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri));
        Cursor cursor = mContext.getContentResolver().query(childrenUri, new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String documentId = cursor.getString(0);
                Uri uri = DocumentsContract.buildDocumentUriUsingTree(dirUri, documentId);
                DocumentFile f = DocumentFile.fromSingleUri(mContext, uri);
                documentFiles.add(f);
            }
            cursor.close();
        }
    }


    public List<DocumentFile> getDocumentFiles() {
        return documentFiles;
    }




    public interface OnDirScanListener{
        void onFound(DocumentFile file);
        void notFound();
    }
}
