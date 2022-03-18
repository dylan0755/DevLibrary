package com.xm.vbrowser.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dylan.library.io.FileDownLoader;
import com.dylan.library.io.FileUtils;
import com.dylan.library.m3u8.download.M3u8DownLoader;
import com.dylan.library.m3u8.download.M3u8DownloadFactory;
import com.dylan.library.m3u8.listener.DownloadListener;
import com.dylan.library.widget.progressbar.ProgressBarDrawableDecorator;
import com.dylan.mylibrary.R;
import com.xm.vbrowser.app.entity.VideoFormat;
import com.xm.vbrowser.app.entity.VideoInfo;
import com.xm.vbrowser.app.util.FileUtil;
import com.xm.vbrowser.app.util.TimeUtil;
import com.zhy.autolayout.utils.L;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Author: Dylan
 * Date: 2022/3/17
 * Desc:
 */

public class FoundVideoItemAdapter extends BaseAdapter {
    public static final String dirPath= Environment.getExternalStorageDirectory().toString();
    private LayoutInflater mInflater;
    private SortedMap<String, VideoInfo> foundVideoInfoMap;
    private List<VideoInfo> dataList;
    private  static int MSG_UPDATE =10;
    private int colorBlack= Color.parseColor("#666666");
    private int colorGray= Color.parseColor("#999999");
    private int colorRed=Color.parseColor("#EB0636");
    private  boolean isActivityBack;

    public void setActivityBack(boolean activityBack) {
        isActivityBack = activityBack;
    }

    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what== MSG_UPDATE){
                if (isActivityBack)return;
                notifyDataSetChanged();
            }
        }
    };

    public FoundVideoItemAdapter(Context context, SortedMap<String, VideoInfo> foundVideoInfoMap){
        this.mInflater = LayoutInflater.from(context);
        this.foundVideoInfoMap = foundVideoInfoMap;
        M3u8DownloadFactory.addProvider(new BouncyCastleProvider());
        prepareData();
    }

    @Override
    public void notifyDataSetChanged() {
        prepareData();
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        prepareData();
        super.notifyDataSetInvalidated();
    }

    private void prepareData(){
        dataList=new ArrayList<>(foundVideoInfoMap.values());
        //倒序  最新的在最前面
        Collections.sort(dataList, new Comparator<VideoInfo>() {
            @Override
            public int compare(VideoInfo o1, VideoInfo o2) {
                if (o1.genTimestamp>o2.genTimestamp){
                   return -1;
                }else{
                    return 1;
                }

            }
        });
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return dataList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return dataList.get(arg0).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder=new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_video_detected, null);
            holder.tvTitle =convertView.findViewById(R.id.tvTitle);
            holder.ivPic =convertView.findViewById(R.id.ivPic);
            holder.ivDownLoadStatus=convertView.findViewById(R.id.ivDownLoad);
            holder.progressBar = convertView.findViewById(R.id.progressBar);
            ProgressBarDrawableDecorator decorator=new ProgressBarDrawableDecorator(holder.progressBar);
            decorator.setBGSolidColor(Color.parseColor("#EEEEEE"));
            decorator.setProgressColor(Color.parseColor("#FADB22"));
            decorator.setCornerRadius(2);
            decorator.decorate();
            holder.tvDuration = convertView.findViewById(R.id.tvDuration);
            holder.tvSize = convertView.findViewById(R.id.tvSize);
            holder.tvDownLoad= convertView.findViewById(R.id.tvDownLoad);
            holder.llDownLoad =convertView.findViewById(R.id.llDownLoad);
            holder.rootView=convertView.findViewById(R.id.rootView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

       final VideoInfo videoInfo = dataList.get(position);
        VideoFormat videoFormat = videoInfo.getVideoFormat();
        holder.tvTitle.setText(TextUtils.isEmpty(videoInfo.getSourcePageTitle())?videoInfo.getFileName()+"."+videoFormat.getName():videoInfo.getSourcePageTitle()+"."+videoFormat.getName());
        if("m3u8".equals(videoFormat.getName())){
            holder.tvDuration.setVisibility(View.VISIBLE);
            holder.tvDuration.setText(TimeUtil.formatTime((int)videoInfo.getDuration()));
            holder.tvSize.setVisibility(View.GONE);
        }else{
            holder.tvSize.setVisibility(View.VISIBLE);
            holder.tvSize.setText(FileUtil.getFormatedFileSize(videoInfo.getSize()));
            holder.tvDuration.setVisibility(View.GONE);

        }

        holder.ivDownLoadStatus.setImageResource(R.drawable.icon_grabvideo_download);
        if (videoInfo.getDownLoadStatus()==0){
            holder.tvDownLoad.setText("点击下载");
            holder.tvDownLoad.setTextColor(colorBlack);
        }else if (videoInfo.getDownLoadStatus()==1){
            holder.tvDownLoad.setText("正在下载");
            holder.tvDownLoad.setTextColor(colorGray);
        }else if (videoInfo.getDownLoadStatus()==2){
            holder.tvDownLoad.setText("已下载");
            holder.tvDownLoad.setTextColor(colorGray);
            holder.ivDownLoadStatus.setImageResource(R.drawable.icon_grabvideo_downloadfinish);
        }else{
            holder.tvDownLoad.setText("下载失败");
            holder.tvDownLoad.setTextColor(colorRed);
        }
        holder.progressBar.setProgress(videoInfo.getDownLoadProgress());
        holder.rootView.setTag(videoInfo);
        final View finalConvertView = convertView;
        final ViewHolder viewHolder=holder;
        holder.llDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoInfo.getDownLoadStatus()==0){
                    toDownLoad(videoInfo, viewHolder,finalConvertView);
                }
            }
        });

        return convertView;
    }

    private void toDownLoad(final VideoInfo videoInfo,final ViewHolder viewHolder, final View finalConvertView) {
        if ("m3u8".equals(videoInfo.getVideoFormat().getName())){
            M3u8DownLoader.downLoad(videoInfo.getUrl(), dirPath, "grabVideo_"+System.currentTimeMillis(), new DownloadListener() {
                @Override
                public void start() {
                    finalConvertView.post(new Runnable() {
                        @Override
                        public void run() {
                            videoInfo.setDownLoadStatus(1);
                            videoInfo.setDownLoadProgress(0);
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });
                }

                @Override
                public void process(final String s, int i, int i1,final float v) {
                    finalConvertView.post(new Runnable() {
                        @Override
                        public void run() {
                            videoInfo.setDownLoadStatus(1);
                            videoInfo.setDownLoadProgress((int) v);
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });
                }

                @Override
                public void speed(String s) {

                }

                @Override
                public void onComplete(final String s) {
                    finalConvertView.post(new Runnable() {
                        @Override
                        public void run() {
                            FileUtils.notifyScanFile(finalConvertView.getContext(),s);
                            videoInfo.setDownLoadProgress(100);
                            videoInfo.setDownLoadStatus(2);
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    finalConvertView.post(new Runnable() {
                        @Override
                        public void run() {
                            videoInfo.setDownLoadStatus(3);
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });
                }
            });
        }else{
            FileDownLoader fileDownLoader=new FileDownLoader();
            fileDownLoader.downLoad(videoInfo.getUrl(),"grabVideo_"+System.currentTimeMillis()+".mp4");
            fileDownLoader.setDownLoadListener(new FileDownLoader.DownLoadListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onError(int i, String s) {
                    finalConvertView.post(new Runnable() {
                        @Override
                        public void run() {
                            videoInfo.setDownLoadStatus(3);
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });
                }

                @Override
                public void onProgress(long l, long l1,final int i) {
                    finalConvertView.post(new Runnable() {
                        @Override
                        public void run() {
                            videoInfo.setDownLoadStatus(1);
                            videoInfo.setDownLoadProgress(i);
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });

                }

                @Override
                public void onComplete(long l, final String s, boolean b) {
                    finalConvertView.post(new Runnable() {
                        @Override
                        public void run() {
                            FileUtils.notifyScanFile(finalConvertView.getContext(),s);
                            videoInfo.setDownLoadProgress(100);
                            videoInfo.setDownLoadStatus(2);
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });

                }
            });
        }
    }

    private static class ViewHolder{
        ImageView ivPic;
        ImageView ivDownLoadStatus;
        TextView tvTitle;
        ProgressBar progressBar;
        TextView tvDuration;
        TextView tvSize;
        TextView tvDownLoad;
        LinearLayout llDownLoad;
        LinearLayout rootView;

    }

}