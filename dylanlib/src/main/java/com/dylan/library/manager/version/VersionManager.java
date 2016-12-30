package com.dylan.library.manager.version;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.dylan.library.bean.VersionBean;
import com.dylan.library.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by AD on 2016/5/23.
 */
public abstract class VersionManager {
    public static int storagePermission;
    public static final  int ALREADY_LATEST = 12;
    public static final int NET_WORK_ERRO=13;
    private final int UPDATA_CLIENT = 10;
    private final int GET_UNDATAINFO_ERROR = 11;
    private Context mContext;
    private int localVersionCode;
    private Handler mHandler;
    private AlertDialog mAlertDialog;
    private String version_url;
    private String remoteVersionName;
    private String description;
    private String download_url;
    private Activity mActivity;
    private boolean isForced = false;
    private String downloadFileName;

    public VersionManager(final Activity context, String version_url, String downloadFileName) {
        mContext = context;
        mActivity = context;
        this.version_url=version_url;
        this.downloadFileName=downloadFileName;
        localVersionCode=getVerCode(mContext);
        //handler
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == UPDATA_CLIENT) {//更新客户端
                    showUpDateDialog();
                    onResult(0);
                } else if (msg.what == ALREADY_LATEST) {//已是最新版本
                    onResult(ALREADY_LATEST);
                } else if (msg.what==GET_UNDATAINFO_ERROR){
                    onResult(GET_UNDATAINFO_ERROR);
                }

                else {//联网错误
                    onResult(NET_WORK_ERRO);
                }
            }
        };
    }



    private void showUpDateDialog() {
        if (!isForced) {//不是强制更新
            mAlertDialog = new AlertDialog.Builder(mContext)
                    .setTitle("新版本"+remoteVersionName)
                    .setMessage(description)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (download_url==null||download_url.isEmpty()){
                                ToastUtil.toToast("下载地址为空");
                                mAlertDialog.dismiss();
                                return;
                            }
                            startDownLoad(download_url);
                        }
                    })//取消按钮事件
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).create();

        } else {//强制更新
            mAlertDialog = new AlertDialog.Builder(mContext)
                    .setTitle("新版本"+remoteVersionName)
                    .setMessage(description)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (download_url==null||download_url.isEmpty()){
                                ToastUtil.toToast("下载地址为空");
                                mAlertDialog.dismiss();
                                return;
                            }
                            startDownLoad(download_url);
                            mActivity.finish();
                        }
                    }).create();
            mAlertDialog.setCancelable(false);

        }
        mAlertDialog.setCanceledOnTouchOutside(false);
        if (!mActivity.isFinishing())
        mAlertDialog.show();
    }


    private void setDownLoadInfo(String remoteVersionName, String description,String download_url){
        this.remoteVersionName=remoteVersionName;
        this.description=description;
        this.download_url=download_url;
    }
    public void query() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                toquery();
            }
        }).start();
    }

    private void toquery() {
        try {
            String remoteUrl =version_url;
            Log.e("toquery: ","remoteUrl "+remoteUrl );
            //包装成url的对象
            URL url = new URL(remoteUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            String queryResult = getJsonData(is);
            obtainRemoteVersionCode(queryResult);
        } catch (Exception e) {
            // 待处理
            Message msg = new Message();
            msg.what = GET_UNDATAINFO_ERROR;
            mHandler.sendMessage(msg);
            e.printStackTrace();
        }
    }



    public void comparetoDownLoad(VersionBean bean){
        if (bean==null)return;
        setDownLoadInfo(bean.getVersion_name(),bean.getDescription(),bean.getDownload_url());
        if (bean.getVersion_code()<=localVersionCode) {
            Message msg = Message.obtain();
            msg.what = ALREADY_LATEST;
            mHandler.sendMessage(msg);
        } else {
            Message msg = new Message();
            msg.what = UPDATA_CLIENT;
            mHandler.sendMessage(msg);
        }
    }


    public abstract void obtainRemoteVersionCode(String jsonData);
    public abstract void verifyStoragePermissions();//检查
    public abstract void onResult(int resultCode);

    //调用系统的下载管理器
    private void startDownLoad(final String remoteUrl) {
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(remoteUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //显示下载界面
        request.setVisibleInDownloadsUi(true);
        //设置缓存目录
//        if (Integer.valueOf(Build.VERSION.SDK)>22)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyStoragePermissions();
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                request.setDestinationInExternalPublicDir("download",downloadFileName);
                //调用enqueue方法之后，只要数据连接可用并且Download Manager可用，下载就会开始。
                request.setMimeType("application/vnd.android.package-archive");
                // 设置为可被媒体扫描器找到
                request.allowScanningByMediaScanner();
                long id = downloadManager.enqueue(request);
                // 把当前下载的ID保存起来
                SharedPreferences sPreferences = mContext.getSharedPreferences("downloadmanager", -10);
                sPreferences.edit().putLong("downloadid", id).commit();
            }
        } else {
            request.setDestinationInExternalPublicDir("download", downloadFileName);
            //调用enqueue方法之后，只要数据连接可用并且Download Manager可用，下载就会开始。
            request.setMimeType("application/vnd.android.package-archive");
            // 设置为可被媒体扫描器找到
            request.allowScanningByMediaScanner();
            long id = downloadManager.enqueue(request);
            // 把当前下载的ID保存起来
            SharedPreferences sPreferences = mContext.getSharedPreferences("downloadmanager", -10);
            sPreferences.edit().putLong("downloadid", id).commit();
        }
    }

    private String getJsonData(InputStream is) {
        StringBuffer buffer = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        String readContent = "";
        try {
            while ((readContent = reader.readLine()) != null) {
                buffer.append(readContent);
            }
            isr.close();
            reader.close();
            return buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg", e.getMessage());
        }
        return verCode;
    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg", e.getMessage());
        }
        return verName;
    }




    public static class CompleteReceiver extends BroadcastReceiver {

        private DownloadManager downloadManager;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                SharedPreferences sPreferences = context.getSharedPreferences("downloadmanager", 0);
                long downloadid = sPreferences.getLong("downloadid", 0);
                //安装apk, //判断这个id与之前的id是否相等，如果相等说明是之前的那个要下载的文件
                if (id == downloadid) {
                    File targetApkFile = null;
                    String serviceString = Context.DOWNLOAD_SERVICE;
                    downloadManager = (DownloadManager) context.getSystemService(serviceString);

                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadid);
                    query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                    Cursor cur = downloadManager.query(query);
                    if (cur != null) {
                        if (cur.moveToFirst()) {
                            String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            if (!TextUtils.isEmpty(uriString)) {
                                targetApkFile = new File(Uri.parse(uriString).getPath());
                            }
                        }
                        cur.close();
                    }
//                if (targetApkFile!=null){
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(Uri.fromFile(targetApkFile), "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(install);
//                }
                }


            }
        }
    }
}
