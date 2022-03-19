package com.dylan.library.m3u8.core;

import com.dylan.library.io.IOUtils;
import com.dylan.library.m3u8.Exception.M3u8Exception;
import com.dylan.library.m3u8.entry.VideoMeta;
import com.dylan.library.m3u8.entry.VideoTs;
import com.dylan.library.m3u8.utils.CommonUtil;
import com.dylan.library.utils.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

/**
 * 初始化下载链接
 */
class UrlParseInit {
  private String name;
  private String path;
  private String url;
  private BufferedReader bufferedReader;

  public UrlParseInit(String m3u8Url, String videoPath, String videoName) {
    if (!videoPath.endsWith("/")){
      videoPath += '/';
    }
    if (!m3u8Url.startsWith("http"))
      m3u8Url = "http://" + m3u8Url;

    url = m3u8Url;
    path = videoPath;
    name = videoName;
  }


  public void initUrl() throws Exception {
   HttpURLConnection connection= (HttpURLConnection) HttpRequestUtils.sendGetRequest(url);
   int code=connection.getResponseCode();
   if ( code>= 200 && code < 300) {
      if (code!= 200){
        connection.disconnect();
        throw new M3u8Exception("响应码异常：" + code);
      }
    }else {
     connection.disconnect();
      throw new M3u8Exception("HTTP请求失败");
    }
   bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
  }

  public List<VideoMeta> getVideoList() {
    String masterUrl = url;
    try {
      List<VideoMeta> resultVideo = new LinkedList<>();
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.startsWith("#EXT-X-STREAM-INF")){
          VideoMeta videoMeta = parseStreamVideo(masterUrl, line, bufferedReader.readLine());
          resultVideo.add(videoMeta);
        }
      }
      if (resultVideo.isEmpty()){
        VideoMeta videoMeta = parseVideoMeta(masterUrl, null);
        resultVideo.add(videoMeta);
      }
      return resultVideo;
    }catch (Exception e){
      throw new M3u8Exception(e);
    }
  }

  private VideoMeta parseStreamVideo(String masterUrl, String propertyLine, String streamUrl) throws IOException {
    String property = propertyLine.split(":", 2)[1];
    if (!streamUrl.startsWith("http")){
      streamUrl = masterUrl.substring(0, masterUrl.lastIndexOf("/") + 1) + streamUrl;
    }
    VideoMeta videoPlayItem = parseVideoMeta(streamUrl, property);
    return videoPlayItem;
  }

  public VideoMeta parseVideoMeta(String videoUrl, String property) throws IOException {
    VideoMeta videoMeta = new VideoMeta(path, name, videoUrl);
    videoMeta.setProperty(property);
    List<VideoTs> videoTsList = new LinkedList<>();

    HttpURLConnection connection= (HttpURLConnection) HttpRequestUtils.sendGetRequest(videoUrl);
    int code=connection.getResponseCode();
    if ( code>= 200 && code < 300) {
      if (code!= 200){
        connection.disconnect();
        throw new M3u8Exception("响应码异常：" + code);
      }
    }else {
      connection.disconnect();
      throw new M3u8Exception("HTTP请求失败");
    }


    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    try {
      String line;
      int fileId = 0;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.startsWith("#EXT-X-VERSION")){
          videoMeta.setVersion(Integer.parseInt(line.split(":")[1]));
        }else if (line.startsWith("#EXT-X-KEY")){
          if (videoMeta.getKeyUrl() == null){
            String keyProperty = line.split(":", 2)[1];
            String[] properties = keyProperty.split(",");
            for (String propertyItem : properties){
              String[] split = propertyItem.split("=", 2);
              String key = split[0];
              String value = split[1];
              if ("URI".equals(key)){
                value = value.replace("\"", "");

                HttpURLConnection connection2= (HttpURLConnection) HttpRequestUtils.sendGetRequest(videoUrl);
                int responseCode=connection.getResponseCode();
                if ( responseCode>= 200 && responseCode < 300) {
                  if (responseCode!= 200){
                    connection.disconnect();
                    throw new M3u8Exception("响应码异常：" + responseCode);
                  }
                }else {
                  connection.disconnect();
                  throw new M3u8Exception("HTTP请求失败");
                }


                videoMeta.setKeyByte(IOUtils.getBytes(connection2.getInputStream()));
                videoMeta.setKeyUrl(value);
                continue;
              }else if ("IV".equals(key)){
                if (value.startsWith("0x")){
                  value = value.substring(2);
                }
                videoMeta.setKeyIvByte(CommonUtil.hexStringToByteArray(value));
                continue;
              }
            }
          }
        }else if (line.startsWith("#EXTINF")) {
          String tsUrl = bufferedReader.readLine();
          if (!tsUrl.startsWith("http")){
            tsUrl = videoUrl.substring(0, videoUrl.lastIndexOf("/") + 1) + tsUrl;
          }
          VideoTs videoTs = new VideoTs(videoMeta, ++fileId, tsUrl);
          videoTsList.add(videoTs);
        }
      }
    }catch (Exception e){
      e.printStackTrace();
    }
    videoMeta.setVideoTsList(videoTsList);
    return videoMeta;
  }

}
