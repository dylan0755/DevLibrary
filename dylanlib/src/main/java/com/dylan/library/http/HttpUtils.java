package com.dylan.library.http;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


/**
 * Author: Dylan
 * Date: 2021/12/13
 * Desc:
 */
public class HttpUtils {
    public static final int TIME_OUT = 8 * 1000;                          //超时时间
    public static final String CHARSET = "UTF-8";                         //编码格式
    private static final String PREFIX = "--";                            //前缀
    private static final String BOUNDARY = UUID.randomUUID().toString();  //边界标识 随机生成
    private static final String CONTENT_TYPE = "multipart/form-data";     //内容类型
    private static final String LINE_END = "\r\n";
    private static final String TAG = "HttpRequestUtil";
    private static final String defaultCharset = "UTF-8";//"GBK"
    private static final int readTimeout = 60000;//60s
    private static final int connectTimeout = 60000;//60s
    private static final int maxRedirects = 4;//最大重定向次数

    private static Map<String, String> commonHeaders;

    private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    static{
        commonHeaders = new HashMap<String, String>();
        commonHeaders.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
    }


    public static ResponseBody get(String url) throws IOException {
        return get( url,null, commonHeaders);
    }

    public static ResponseBody get(String url, Map<String, String> params) throws IOException {
        return get( url,params, commonHeaders);
    }

    public static ResponseBody get(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        HttpURLConnection connection=obtainGetRequest(url,params,headers);
        return getResponseString(connection);
    }


    private static HttpURLConnection obtainGetRequest(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        StringBuilder buf = new StringBuilder("");
        URL urlObject = new URL(url);
        buf.append(urlObject.getProtocol()).append("://").append(urlObject.getHost()).append(((urlObject.getPort()==-1) || (urlObject.getPort()!=urlObject.getDefaultPort()))?"":":"+urlObject.getPort()).append(urlObject.getPath());
        String query = urlObject.getQuery();
        if(params == null ){
            params = new HashMap<String, String>();
        }
        boolean isQueryExist = false;
        if(!(query == null || query.length() == 0) || params.size() > 0){
            buf.append("?");
            isQueryExist = true;
        }
        if(!(query == null || query.length() == 0)){
            buf.append(query);
            buf.append("&");
        }
        Set<Map.Entry<String, String>> entrys = params.entrySet();
        for (Map.Entry<String, String> entry : entrys) {
            buf.append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), defaultCharset)).append("&");
        }
        if(isQueryExist){
            buf.deleteCharAt(buf.length() - 1);
        }
        System.out.println("before:"+url);
        System.out.println("after:"+buf.toString());
        urlObject = new URL(buf.toString());
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) urlObject.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (headers != null) {
                entrys = headers.entrySet();
                for (Map.Entry<String, String> entry : entrys) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.getResponseCode();
            return conn;
        }catch (IOException e){
            if(conn != null) {
                conn.disconnect();
            }
            throw e;
        }
    }

    private static URLConnection obtainGetRequest(String url) throws IOException {
        return obtainGetRequest(url, null,commonHeaders);
    }

    private static HttpURLConnection obtainGetRequest(String url,
                                                      Map<String, String> params) throws IOException {
        return obtainGetRequest(url,params,commonHeaders);
    }




    public static ResponseBody post(String url,Map<String, String> params) throws IOException {
        HttpURLConnection connection=ObtainPostRequest(url,params,commonHeaders);
        return getResponseString(connection);
    }
    public static ResponseBody post(String url,Map<String, String> params,Map<String, String> headers) throws IOException {
        HttpURLConnection connection=ObtainPostRequest(url,params,headers);
        return getResponseString(connection);
    }


    public static ResponseBody postJson(String reqUrl, String json){
        String result = "";
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        ResponseBody body=new ResponseBody();
        long start=System.currentTimeMillis();
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.connect();
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
            os.close();
            InputStream in;
            if (httpURLConnection.getResponseCode() == 200) {
                in = httpURLConnection.getInputStream();
            } else {
                in = httpURLConnection.getErrorStream();
            }
            reader = new BufferedReader(new InputStreamReader(in));
            String data = "";
            StringBuilder builder = new StringBuilder();
            while ((data = reader.readLine()) != null) {
                builder.append(data);
            }
            body.status = httpURLConnection.getResponseCode();
            body.result = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            body.status = -100;
            body.result = e.getMessage();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        body.duration=(System.currentTimeMillis()-start)*1.0f/1000+"s";
        if (body.status==200){
            body.byteSize=ResponseBody.getFormatFileSize(body.result.getBytes().length);
        }
        return body;
    }


    private static HttpURLConnection ObtainPostRequest(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        StringBuilder buf = new StringBuilder();
        if(params == null ){
            params = new HashMap<String, String>();
        }
        Set<Map.Entry<String, String>> entrys = params.entrySet();
        for (Map.Entry<String, String> entry : entrys) {
            buf.append("&").append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), defaultCharset));
        }
        buf.deleteCharAt(0);
        URL urlObject = new URL(url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) urlObject.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (headers != null) {
                entrys = headers.entrySet();
                for (Map.Entry<String, String> entry : entrys) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            //System.out.println("buf.toString():"+buf.toString());
            out.write(buf.toString().getBytes(defaultCharset));
            out.flush();
            conn.getResponseCode(); // 为了发送成功
            return conn;
        }catch (IOException e){
            if(conn != null) {
                conn.disconnect();
            }
            throw e;
        }
    }

    private static HttpURLConnection ObtainPostRequest(String url,
                                                       Map<String, String> params) throws IOException {
        try {
            return ObtainPostRequest(url, params, commonHeaders);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static URLConnection sendStringPostRequest(String url, String postDataString, Map<String, String> headers) throws IOException {
        if(postDataString == null ){
            postDataString = "";
        }
        Set<Map.Entry<String, String>> entrys;
        URL urlObject = new URL(url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) urlObject.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (headers != null) {
                entrys = headers.entrySet();
                for (Map.Entry<String, String> entry : entrys) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            //System.out.println("buf.toString():"+buf.toString());
            out.write(postDataString.getBytes(defaultCharset));
            out.flush();
            conn.getResponseCode(); // 为了发送成功
            return conn;
        }catch (IOException e){
            if(conn != null) {
                conn.disconnect();
            }
            throw e;
        }
    }

    private static URLConnection sendStringPostRequest(String url, String postDataString) throws IOException {
        try {
            return sendStringPostRequest(url, postDataString, commonHeaders);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ResponseBody getResponseString(URLConnection urlConnection) throws IOException {

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine;
        ResponseBody body=new ResponseBody();
        long start=System.currentTimeMillis();
        try {
            HttpURLConnection connection= (HttpURLConnection) urlConnection;
            if (connection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }


            inputStreamReader = new InputStreamReader(inputStream, defaultCharset);
            reader = new BufferedReader(inputStreamReader);
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine+"\n");
            }
            body.status = connection.getResponseCode();
            body.result = resultBuffer.toString();
        }catch (Exception e){
            e.printStackTrace();
            body.status = -100;
            body.result = e.getMessage();
        }  finally {
            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }
            ((HttpURLConnection) urlConnection).disconnect();
        }
        body.duration=(System.currentTimeMillis()-start)*1.0f/1000+"s";
        if (body.status==200){
            body.byteSize=ResponseBody.getFormatFileSize(body.result.getBytes().length);
        }
        return body;
    }




    public static HeadRequestResponse performHeadRequest(String url) throws IOException {
        return performHeadRequest(url, commonHeaders);
    }

    private static HeadRequestResponse performHeadRequest(String url, Map<String, String> headers) throws IOException {
        return performHeadRequestForRedirects(url, headers, 0);
    }

    private static HeadRequestResponse performHeadRequestForRedirects(String url, Map<String, String> headers, int redirectCount) throws IOException {
        URL urlObject = new URL(url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) urlObject.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (headers != null) {
                Set<Map.Entry<String, String>> entrySet = headers.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            if (responseCode == 302) {
                if (redirectCount >= maxRedirects) {
                    return new HeadRequestResponse(url, new HashMap<String, List<String>>());
                } else {
                    String location = headerFields.get("Location").get(0);
                    return performHeadRequestForRedirects(location, headers, redirectCount + 1);
                }
            } else {
                return new HeadRequestResponse(url, headerFields);
            }
        }finally {
            if(conn != null){
                conn.disconnect();
            }
        }
    }

    public static class HeadRequestResponse{
        private String realUrl;
        private Map<String, List<String>> headerMap;

        public HeadRequestResponse() {
        }

        public HeadRequestResponse(String realUrl, Map<String, List<String>> headerMap) {
            this.realUrl = realUrl;
            this.headerMap = headerMap;
        }

        public String getRealUrl() {
            return realUrl;
        }

        public void setRealUrl(String realUrl) {
            this.realUrl = realUrl;
        }

        public Map<String, List<String>> getHeaderMap() {
            return headerMap;
        }

        public void setHeaderMap(Map<String, List<String>> headerMap) {
            this.headerMap = headerMap;
        }
    }





    public static void save2File(URLConnection urlConnection,String saveFilePath) throws IOException {

        DataInputStream dis = null;
        FileOutputStream fos = null;

        try {
            dis = new DataInputStream(urlConnection.getInputStream());
            //建立一个新的文件
            fos = new FileOutputStream(new File(saveFilePath));
            byte[] buffer = new byte[1024];
            int length;
            //开始填充数据
            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }finally {
            if(dis != null){
                dis.close();
            }
            if(fos != null){
                fos.close();
            }
            ((HttpsURLConnection)urlConnection).disconnect();
        }
    }

    public static URLConnection getRequest(String url) throws IOException {
        return getRequest(url, null,commonHeaders);
    }

    public static URLConnection getRequest(String url,
                                           Map<String, String> params) throws IOException {
        return getRequest(url,params,commonHeaders);
    }

    public static URLConnection getRequest(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        StringBuilder buf = new StringBuilder("");
        URL urlObject = new URL(url);
        buf.append(urlObject.getProtocol()).append("://").append(urlObject.getHost()).append(((urlObject.getPort()==-1) || (urlObject.getPort()!=urlObject.getDefaultPort()))?"":":"+urlObject.getPort()).append(urlObject.getPath());
        String query = urlObject.getQuery();
        if(params == null ){
            params = new HashMap<String, String>();
        }
        boolean isQueryExist = false;
        if(!(query == null || query.length() == 0) || params.size() > 0){
            buf.append("?");
            isQueryExist = true;
        }
        if(!(query == null || query.length() == 0)){
            buf.append(query);
            buf.append("&");
        }
        Set<Map.Entry<String, String>> entrys = params.entrySet();
        for (Map.Entry<String, String> entry : entrys) {
            buf.append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), defaultCharset)).append("&");
        }
        if(isQueryExist){
            buf.deleteCharAt(buf.length() - 1);
        }
        System.out.println("before:"+url);
        System.out.println("after:"+buf.toString());
        urlObject = new URL(buf.toString());
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) urlObject.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (headers != null) {
                entrys = headers.entrySet();
                for (Map.Entry<String, String> entry : entrys) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.getResponseCode();
            return conn;
        }catch (IOException e){
            if(conn != null) {
                conn.disconnect();
            }
            throw e;
        }
    }

    private static String getRequestHeader(HttpURLConnection httpUrlCon){
        String requestInfo="Request Headers:"+"\n"
                +httpUrlCon.getRequestMethod() +"\n"+
                " Connection: " + httpUrlCon.getRequestProperty("Connection") +"\n"+
                " Accept: " + httpUrlCon.getRequestProperty("Connection") +"\n"+
                " User-Agent: " + httpUrlCon.getRequestProperty("User-Agent") +"\n"+
                " Accept-Encoding: " + httpUrlCon.getRequestProperty("Accept-Encoding") +"\n"+
                " Accept-Language: " + httpUrlCon.getRequestProperty("Accept-Language") +"\n"+
                " Cookie: " + httpUrlCon.getRequestProperty("Cookie") +"\n"+
                " Connection: " + httpUrlCon.getRequestProperty("Connection") +"\n";
        return requestInfo;
    }

}
