package com.dylan.library.media;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.provider.DocumentFile;

import com.dylan.library.dialog.AndroidRStorageAccessPermDialog;
import com.dylan.library.dialog.LoadingDialog;
import com.dylan.library.io.FileUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.PermissionUtils;
import com.dylan.library.utils.WeChatDocumentFileReader;
import com.dylan.library.utils.thread.ThreadPools;
import com.dylan.library.utils.thread.ThreadUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2022/4/6
 * Desc:
 */

public class FileSearcher {
    private AndroidRStorageAccessPermDialog dialog;
    private boolean accessWeChatCache = true;

    public void setAccessWeChatCache(boolean accessWeChatCache) {
        this.accessWeChatCache = accessWeChatCache;
    }

    public void toSearch(final Context context, final String[] suffix, final OnSearchCompleteListener listener) {
        toSearch(context, null, suffix, listener);
    }

    public void toSearch(final Context context, final String[] customScanDir, final String[] suffix, final OnSearchCompleteListener listener) {
        if (Build.VERSION.SDK_INT >= 30 && accessWeChatCache) {
            if (PermissionUtils.isGrantAndroidData(context)) {
                loadFile(context, customScanDir, suffix, listener);
            } else {
                dialog = new AndroidRStorageAccessPermDialog(context);
                dialog.show(new AndroidRStorageAccessPermDialog.GrantPermissionCallBack() {
                    @Override
                    public void hasGrant() {
                        loadFile(context, customScanDir, suffix, listener);
                    }

                    @Override
                    public void hasReject() {

                    }
                });
            }
        } else {
            loadFile(context, customScanDir, suffix, listener);
        }
    }


    private void loadFile(final Context context, final String[] customScanDir, final String[] suffixs, final OnSearchCompleteListener listener) {
        final LoadingDialog loadingDialog = new LoadingDialog.Builder(context).build();
        loadingDialog.setCancelable(false);
        if (loadingDialog.getBackgroundView() != null && loadingDialog.getBackgroundView().getBackground() != null) {
            loadingDialog.getBackgroundView().getBackground().setAlpha(200);
        }
        final List<String> pathLists = new ArrayList<>();
        pathLists.add("");
        if (EmptyUtils.isNotEmpty(customScanDir)) {
            Collections.addAll(pathLists, customScanDir);
        } else {
            pathLists.add(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.tencent.mm/MicroMsg/Download");
            pathLists.add(Environment.getExternalStorageDirectory().toString());
            pathLists.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        }


        final List<File> totalList = new ArrayList<>();
        final int[] count = new int[1];
        for (final String path : pathLists) {
            ThreadPools.getInstance().fixedThreadPoolRun(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    List<File> finalList = new ArrayList<>();
                    if (EmptyUtils.isEmpty(path)) {
                        List<MediaStoreFile> fileList = MediaStoreLoader.getFilesFromFileStore(context, suffixs, null);
                        if (EmptyUtils.isNotEmpty(fileList)) {
                            for (MediaStoreFile file : fileList) {
                                File f = new File(file.getPath());
                                if (!check(f)) continue;
                                finalList.add(f);
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT>=30&&path.contains("/Android/data/com.tencent.mm/MicroMsg/Download")) {
                            WeChatDocumentFileReader fileReader = new WeChatDocumentFileReader(context);
                            fileReader.readWxDownLoadDir();
                            List<DocumentFile> documentFiles = fileReader.getDocumentFiles();
                            try {
                                for (DocumentFile documentFile : documentFiles) {
                                    File originFile = FileUtils.getFileByUri(documentFile.getUri(), context);//微信
                                    boolean matchSuffix=false;
                                    for (String suffix : suffixs) {
                                        if (originFile==null||!originFile.getAbsolutePath().endsWith(suffix)) {
                                            continue;
                                        }
                                        matchSuffix=true;
                                    }
                                    if (!matchSuffix)continue;
                                    String DesDir = context.getExternalCacheDir() + "/wx/DownLoad";
                                    FileUtils.createDirIfNotExists(DesDir);
                                    File desFile = new File(DesDir + "/" + FileUtils.getFileNameFromPath(originFile.getPath()));
                                    if (!desFile.exists()) {
                                        InputStream fis = context.getContentResolver().openInputStream(documentFile.getUri());
                                        FileUtils.copyFile(fis, desFile);
                                    }
                                    finalList.add(desFile);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            List<File> fileList = FileUtils.getSpecFilesFromDir(path, suffixs);
                            if (EmptyUtils.isNotEmpty(fileList)) {
                                for (File file : fileList) {
                                    if (!check(file)) continue;
                                    finalList.add(file);
                                }
                            }
                        }

                    }
                    count[0]++;

                    totalList.addAll(finalList);
                    if (count[0] == pathLists.size()) {
                        //处理重复
                        HashMap<String, File> map = new HashMap<>();
                        for (File file : totalList) {
                            map.put(file.getAbsolutePath(), file);
                        }
                        final List<File> beanList = new ArrayList<>(map.values());
                        Collections.sort(beanList, new Comparator<File>() {
                            @Override
                            public int compare(File o1, File o2) {
                                if (o1.lastModified() > o2.lastModified()) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        });

                        ThreadUtils.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                if (listener != null) listener.onComplete(beanList);
                            }
                        });
                    }
                }
            });
        }


    }

    private boolean check(File file) {
        if (file.length() == 0) return false;
        if (EmptyUtils.isNotEmpty(file.getName())) {
            if (file.getName().contains("log")
                    || file.getName().contains("webview")
                    || file.getName().startsWith("tbs_")
                    || file.getName().contains("Log")
                    || file.getName().startsWith(".")
                    || file.getName().startsWith("temp")
                    || file.getName().startsWith("cuid"))
                return false;
        } else {
            return false;
        }
        if (file.isDirectory()) return false;
        return true;
    }


    public interface OnSearchCompleteListener {
        void onComplete(List<File> list);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (dialog != null) dialog.onActivityResult(requestCode, resultCode, data);
    }


}
