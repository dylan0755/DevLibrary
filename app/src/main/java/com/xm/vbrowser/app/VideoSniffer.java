package com.xm.vbrowser.app;


import com.alibaba.fastjson.JSON;
import com.dylan.library.bean.EventBundle;
import com.dylan.library.m3u8.entry.M3U8;
import com.dylan.library.m3u8.utils.M3U8Utils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.http.Http;
import com.xm.vbrowser.app.activity.WebVideoGrabActivity;
import com.xm.vbrowser.app.entity.DetectedVideoInfo;
import com.xm.vbrowser.app.entity.VideoFormat;
import com.xm.vbrowser.app.entity.VideoInfo;
import com.xm.vbrowser.app.util.UUIDUtil;
import com.xm.vbrowser.app.util.VideoFormatUtil;
import com.xm.vbrowser.app.util.VideoSnifferLogger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xm on 17-8-17.
 */
public class VideoSniffer {
    private LinkedBlockingQueue<DetectedVideoInfo> detectedTaskUrlQueue;
    private int threadPoolSize;
    private int retryCountOnFail;
    private SortedMap<String, VideoInfo> foundVideoInfoMap = Collections.synchronizedSortedMap(new TreeMap<String, VideoInfo>());
    private List<Thread> threadList = new ArrayList<Thread>();


    public VideoSniffer(LinkedBlockingQueue<DetectedVideoInfo> detectedTaskUrlQueue, int threadPoolSize, int retryCountOnFail) {
        this.detectedTaskUrlQueue = detectedTaskUrlQueue;
        this.threadPoolSize = threadPoolSize;
        this.retryCountOnFail = retryCountOnFail;

    }

    public void startSniffer() {
        stopSniffer();
        threadList = new ArrayList<Thread>();
        for (int i = 0; i < threadPoolSize; i++) {
            WorkerThread workerThread = new WorkerThread(detectedTaskUrlQueue, foundVideoInfoMap, retryCountOnFail);
            threadList.add(workerThread);
        }
        for (Thread thread : threadList) {
            try {
                thread.start();
            } catch (IllegalThreadStateException e) {
                VideoSnifferLogger.d("VideoSniffer", "线程已启动, Pass");
            }
        }
    }


    public void stopSniffer() {
        for (Thread thread : threadList) {
            try {
                thread.interrupt();
            } catch (Exception e) {
                VideoSnifferLogger.d("VideoSniffer", "线程已中止, Pass");
            }
        }
    }

    private class WorkerThread extends Thread {
        private LinkedBlockingQueue<DetectedVideoInfo> detectedTaskUrlQueue;
        private SortedMap<String, VideoInfo> foundVideoInfoMap;
        private int retryCountOnFail;

        WorkerThread(LinkedBlockingQueue<DetectedVideoInfo> detectedTaskUrlQueue, SortedMap<String, VideoInfo> foundVideoInfoMap, int retryCountOnFail) {
            this.detectedTaskUrlQueue = detectedTaskUrlQueue;
            this.foundVideoInfoMap = foundVideoInfoMap;
            this.retryCountOnFail = retryCountOnFail;
        }

        @Override
        public void run() {
            super.run();
            VideoSnifferLogger.d("WorkerThread", "thread (" + Thread.currentThread().getId() + ") :start");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DetectedVideoInfo detectedVideoInfo = detectedTaskUrlQueue.take();
                    VideoSnifferLogger.d("WorkerThread", "start taskUrl=" + detectedVideoInfo.getUrl());
                    int failCount = 0;
                    while (!detectUrl(detectedVideoInfo)) {
                        //如果检测失败
                        failCount++;
                        if (failCount >= retryCountOnFail) {
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    VideoSnifferLogger.d("WorkerThread", "thread (" + Thread.currentThread().getId() + ") :Interrupted");
                    return;
                }
            }
            VideoSnifferLogger.d("WorkerThread", "thread (" + Thread.currentThread().getId() + ") :exited");
        }

        private boolean detectUrl(DetectedVideoInfo detectedVideoInfo) {
            String url = detectedVideoInfo.getUrl();
            String sourcePageUrl = detectedVideoInfo.getSourcePageUrl();
            String sourcePageTitle = detectedVideoInfo.getSourcePageTitle();
            try {
                Http.HeadRequestResponse headRequestResponse = Http.performHeadRequest(url);
                url = headRequestResponse.getRealUrl();
                detectedVideoInfo.setUrl(url);
                Map<String, List<String>> headerMap = headRequestResponse.getHeaderMap();
                if (headerMap == null || !headerMap.containsKey("Content-Type")) {
                    //检测失败，未找到Content-Type
                    VideoSnifferLogger.d("WorkerThread", "fail 未找到Content-Type:" + JSON.toJSONString(headerMap) + " taskUrl=" + url);
                    return false;
                }
                VideoSnifferLogger.d("WorkerThread", "Content-Type:" + headerMap.get("Content-Type").toString() + " taskUrl=" + url);
                VideoFormat videoFormat = VideoFormatUtil.detectVideoFormat(url, headerMap.get("Content-Type").toString());
                if (videoFormat == null) {
                    //检测成功，不是视频
                    VideoSnifferLogger.d("WorkerThread", "fail not video taskUrl=" + url);
                    return true;
                }
                VideoInfo videoInfo = new VideoInfo();
                if ("m3u8".equals(videoFormat.getName())) {
                    M3U8 m3U8 = M3U8Utils.parse(url);
                    if (m3U8.getTotalSecondDuration() <= 0) {
                        //检测成功，不是m3u8的视频
                        VideoSnifferLogger.d("WorkerThread", "fail not m3u8 taskUrl=" + url);
                        return true;
                    }
                    videoInfo.setM3U8(m3U8);
                    videoInfo.setDuration(m3U8.getTotalSecondDuration());
                } else {
                    long size = 0;
                    VideoSnifferLogger.d("WorkerThread", JSON.toJSONString(headerMap));
                    if (headerMap.containsKey("Content-Length") && headerMap.get("Content-Length").size() > 0) {
                        try {
                            size = Long.parseLong(headerMap.get("Content-Length").get(0));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            VideoSnifferLogger.d("WorkerThread", "NumberFormatException", e);
                        }
                    }
                    videoInfo.setSize(size);
                }


                if (foundVideoInfoMap.containsKey(url)) {
                    VideoSnifferLogger.e("url: ", "视频已存在：" + url);
                    return false;//视频已存在
                } else {
                    Iterator<VideoInfo> iterator = foundVideoInfoMap.values().iterator();
                    while (iterator.hasNext()) {
                        VideoInfo element = iterator.next();
                        //同一个视频判断，来源相同，格式相同，大小相同,时长相同
                        if (EmptyUtils.isNotEmpty(element.getSourcePageUrl())
                                && element.getSourcePageUrl().equals(sourcePageUrl)) {
                            if (EmptyUtils.isNotEmpty(videoFormat.getName())
                                    && videoFormat.getName().equals(element.getVideoFormat().getName())
                                    && element.getSize() == videoInfo.getSize()
                                    && element.getDuration() == videoInfo.getDuration()) {
                                return false;//视频已存在
                            }
                        }
                    }
                }


                videoInfo.setUrl(url);
                videoInfo.setFileName(UUIDUtil.genUUID());
                videoInfo.setVideoFormat(videoFormat);
                videoInfo.setSourcePageTitle(sourcePageTitle);
                videoInfo.setSourcePageUrl(sourcePageUrl);
                foundVideoInfoMap.put(url, videoInfo);
                EventBundle bundle= new EventBundle(WebVideoGrabActivity.ACTION_DETECTED_NEW_VIDEO);
                bundle.setExtraData(videoInfo);
                EventBus.getDefault().post(bundle);
                //检测成功，是视频
                VideoSnifferLogger.d("WorkerThread", "found video taskUrl=" + url);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                VideoSnifferLogger.d("WorkerThread", "fail IO错误 taskUrl=" + url);
                return false;
            }
        }


    }
}
