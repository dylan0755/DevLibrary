package com.dylan.library.utils;

import android.util.Log;


import com.dylan.library.exception.ELog;
import com.dylan.library.media.MimeTypeFile;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkServerTrusted");
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String url) throws IOException {
        return get( url,null, commonHeaders);
    }

    public static String get(String url, Map<String, String> params) throws IOException {
        return get( url,params, commonHeaders);
    }

    public static String get(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
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
            if (urlObject.getProtocol().toUpperCase().equals("HTTPS")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) urlObject.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) urlObject.openConnection();
            }
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




    public static String post(String url,Map<String, String> params) throws IOException {
        HttpURLConnection connection=ObtainPostRequest(url,params,commonHeaders);
        return getResponseString(connection);
    }
    public static String post(String url,Map<String, String> params,Map<String, String> headers) throws IOException {
        HttpURLConnection connection=ObtainPostRequest(url,params,headers);
        return getResponseString(connection);
    }


    public static String postJson(String reqUrl, String json){
        String result = "";
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
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
            os.write(json.getBytes("utf-8"));
            os.flush();
            os.close();
            if (httpURLConnection.getResponseCode() == 200) {
                reader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream())
                );
                String data = "";
                StringBuilder builder = new StringBuilder();
                while ((data = reader.readLine()) != null) {
                    builder.append(data);
                }
                result = builder.toString();
            }
        } catch (Exception e) {
            ELog.e(e);
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
        return result;
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
            if (urlObject.getProtocol().toUpperCase().equals("HTTPS")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) urlObject.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) urlObject.openConnection();
            }
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
            if (urlObject.getProtocol().toUpperCase().equals("HTTPS")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) urlObject.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) urlObject.openConnection();
            }
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

    public static String getResponseString(URLConnection urlConnection) throws IOException {

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine;

        try {
            if (((HttpURLConnection)urlConnection).getResponseCode() >= 300) {
                throw new IOException("HTTP Request is not success, Response code is " + ((HttpURLConnection)urlConnection).getResponseCode());
            }
            inputStream = urlConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, defaultCharset);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine+"\n");
            }
            return resultBuffer.toString();
        }finally {

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
            if (urlObject.getProtocol().toUpperCase().equals("HTTPS")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) urlObject.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) urlObject.openConnection();
            }
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


    public static ResponseBody postParamAndFiles(String requestUrl, final Map<String, String> strParams, final List<File> fileList) {
        HttpURLConnection conn = null;
        ResponseBody body=new ResponseBody();
        try {
            URL url = new URL(requestUrl);
            if (url.getProtocol().toUpperCase().equals("HTTPS")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);//Post 请求不能使用缓存
            //设置请求头参数
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE+";boundary=" + BOUNDARY);
            /**
             * 请求体
             */
            //上传参数
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //getStrParams()为一个
            dos.write( getStrParams(strParams).toString().getBytes(CHARSET) );
            dos.flush();
            //文件上传
            StringBuilder fileSb = new StringBuilder();
            for (File file:fileList){
                String contentType= MimeTypeFile.getMimeTypeForFile(file.getPath());
                fileSb.append(PREFIX)
                        .append(BOUNDARY)
                        .append(LINE_END)
                        .append("Content-Disposition: form-data; name=\"file\"; filename=\""
                                + file.getName() + "\"" + LINE_END)
                        .append("Content-Type: "+contentType + LINE_END) //此处的ContentType不同于 请求头 中Content-Type
                        .append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容
                dos.writeBytes(fileSb.toString());
                dos.flush();
                InputStream is = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1){
                    dos.write(buffer,0,len);
                }
                is.close();
                dos.writeBytes(LINE_END);
            }
            //请求结束标志
            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
            dos.flush();
            dos.close();
            //读取服务器返回信息
            InputStream in;
            if (conn.getResponseCode() == 200) {
                in= conn.getInputStream();
            }else{
                in= conn.getErrorStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            in.close();
            body.status=conn.getResponseCode();
            body.result=response.toString();
        } catch (Exception e) {
            ELog.e(e);
            body.status=-100;
            body.result=e.getMessage();
        }finally {
            if (conn!=null){
                conn.disconnect();
            }
        }
        return body;
    }

    /**
     * 对post参数进行编码处理
     * */
    private static StringBuilder getStrParams(Map<String,String> strParams){
        StringBuilder strSb = new StringBuilder();
        for (Map.Entry<String, String> entry : strParams.entrySet() ){
            strSb.append(PREFIX)
                    .append(BOUNDARY)
                    .append(LINE_END)
                    .append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END)
                    .append("Content-Type: text/plain; charset=" + CHARSET + LINE_END)
                    .append(LINE_END)// 参数头设置完以后需要两个换行，然后才是参数内容
                    .append(entry.getValue())
                    .append(LINE_END);
        }
        return strSb;
    }



    public static class ResponseBody{
        public int status;
        public String result;

        @Override
        public String toString() {
            return "ResponseBody{" +
                    "status=" + status +
                    ", result='" + result + '\'' +
                    '}';
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
            if (urlObject.getProtocol().toUpperCase().equals("HTTPS")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) urlObject.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) urlObject.openConnection();
            }
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
}
