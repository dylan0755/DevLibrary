package com.dylan.library.m3u8.utils;

import android.util.Log;

import com.dylan.library.m3u8.M3U8;
import com.dylan.library.utils.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class M3U8Utils {
    public static double figureM3U8Duration(String url) throws IOException {
        String m3U8Content = HttpRequestUtils.getResponseString(HttpRequestUtils.sendGetRequest(url));
        boolean isSubFileFound = false;
        double totalDuration = 0d;
        for(String lineString:m3U8Content.split("\n")){
            lineString = lineString.trim();
            if(isSubFileFound){
                if(lineString.startsWith("#")){
                    //格式错误 直接返回时长0
                    Log.d("M3U8Util", "格式错误1");
                    return 0d;
                }else{
                    String subFileUrl = new URL(new URL(url), lineString).toString();
                    return figureM3U8Duration(subFileUrl);
                }
            }
            if(lineString.startsWith("#")){
                if(lineString.startsWith("#EXT-X-STREAM-INF")){
                    isSubFileFound = true;
                    continue;
                }
                if(lineString.startsWith("#EXTINF:")){
                    int sepPosition = lineString.indexOf(",");
                    if(sepPosition<="#EXTINF:".length()){
                        sepPosition = lineString.length();
                    }
                    double duration = 0d;
                    try {
                        duration = Double.parseDouble(lineString.substring("#EXTINF:".length(), sepPosition).trim());
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        //格式错误 直接返回时长0
                        Log.d("M3U8Util", "格式错误3");
                        return 0d;
                    }
                    totalDuration += duration;
                }
            }

        }
        return totalDuration;
    }


    public static M3U8 parse(String url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) HttpRequestUtils.sendGetRequest(url);
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        M3U8 ret = new M3U8();
        String line;
        double seconds = 0;
        double totalDuration = 0d;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                if (line.startsWith("#EXTINF:")) {
                    line = line.substring(8);
                    if (line.endsWith(",")) {
                        line = line.substring(0, line.length() - 1);
                    }
                    if (line.contains(",")) {
                        line = line.substring(0, line.indexOf(","));
                    }
                    //解析每个分段的长度
                    seconds = Double.parseDouble(line);
                }
                continue;
            }
            ret.addTs(new M3U8.M3U8Ts(line, seconds));
            totalDuration+=seconds;
            seconds = 0;
        }
        ret.setTotalDuration(totalDuration);
        reader.close();
        inputStream.close();
        return ret;
    }

}
