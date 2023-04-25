package com.dylan.library.http;

import android.util.Log;

import com.dylan.library.exception.ELog;
import com.dylan.library.media.MimeTypeFile;
import com.dylan.library.utils.EmptyUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Author: Dylan
 * Date: 2023/4/25
 * Desc:
 */
public class MultiPartPoster {
    public static final int TIME_OUT = 8 * 1000;                          //超时时间
    public static final String CHARSET = "UTF-8";                         //编码格式
    private static final String PREFIX = "--";                            //前缀
    private static final String BOUNDARY = UUID.randomUUID().toString();  //边界标识 随机生成
    private static final String CONTENT_TYPE = "multipart/form-data";     //内容类型
    private static final String LINE_END = "\r\n";
    private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public static ResponseBody postJson(String requestUrl, final FormDataJSONParam paramJson, final FormDataFile formDataFile) {
        List<FormDataFile> formDataFiles = new ArrayList<>();
        formDataFiles.add(formDataFile);
        return postJson(requestUrl, paramJson, formDataFiles);
    }

    public static ResponseBody postJson(String requestUrl, final FormDataJSONParam paramJson, final List<FormDataFile> fileList) {
        HttpURLConnection conn = null;
        ResponseBody body = new ResponseBody();
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
            conn.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" + BOUNDARY);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //注意请求配置完成后需要两次换行才能 拼接参数内容
            String parmSB = PREFIX +
                    BOUNDARY +
                    LINE_END +
                    "Content-Disposition: form-data; name=\"" + paramJson.formDataName + "\";" + LINE_END +
                    "Content-Type: application/json;" + LINE_END +
                    LINE_END +
                    paramJson.jsonParam + LINE_END;
            dos.write(parmSB.getBytes(CHARSET));
            dos.flush();
            //文件上传
            StringBuilder fileSb = new StringBuilder();
            for (FormDataFile formDataFile : fileList) {
                String contentType = MimeTypeFile.getMimeTypeForFile(formDataFile.file.getPath());
                fileSb.append(PREFIX)
                        .append(BOUNDARY)
                        .append(LINE_END)
                        .append("Content-Disposition: form-data; name=\"" + formDataFile.formDataName + "\"; filename=\""
                                + formDataFile.file.getName() + "\"" + LINE_END)
                        .append("Content-Type: " + contentType + ";" + "\r\n")
                        .append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容
                dos.writeBytes(fileSb.toString());
                dos.flush();
                InputStream is = new FileInputStream(formDataFile.file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    dos.write(buffer, 0, len);
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
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            in.close();
            body.status = conn.getResponseCode();
            body.result = response.toString();
        } catch (Exception e) {
            ELog.e(e);
            body.status = -100;
            body.result = e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return body;
    }

    public static ResponseBody postForm(String requestUrl, final Map<String, String> strParams, final FormDataFile formDataFile) {
        List<FormDataFile> formDataFiles = new ArrayList<>();
        formDataFiles.add(formDataFile);
        return postForm(requestUrl,strParams,formDataFiles);
    }
    public static ResponseBody postForm(String requestUrl, final Map<String, String> strParams, final List<FormDataFile> fileList) {
        HttpURLConnection conn = null;
        ResponseBody body = new ResponseBody();
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
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            /**
             * 请求体
             */
            //上传参数
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //getStrParams()为一个
            dos.write(getStrParams(strParams).toString().getBytes(CHARSET));
            dos.flush();
            //文件上传
            StringBuilder fileSb = new StringBuilder();
            for (FormDataFile formDataFile : fileList) {
                String fileName= EmptyUtils.isNotEmpty(formDataFile.getFileName())?formDataFile.getFileName():formDataFile.file.getName();
                String contentType = MimeTypeFile.getMimeTypeForFile(formDataFile.file.getPath());
                if (EmptyUtils.isEmpty(contentType))contentType=formDataFile.getContentType();
                if (EmptyUtils.isEmpty(contentType))contentType="application/octet-stream";
                fileSb.append(PREFIX)
                        .append(BOUNDARY)
                        .append(LINE_END)
                        .append("Content-Disposition: form-data; name=\"" + formDataFile.formDataName + "\"; filename=\""
                                +fileName+ "\"" + LINE_END)
                        .append("Content-Type: " + contentType + ";" + "\r\n")
                        .append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容
                dos.writeBytes(fileSb.toString());
                dos.flush();
                InputStream is = new FileInputStream(formDataFile.file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    dos.write(buffer, 0, len);
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
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            in.close();
            body.status = conn.getResponseCode();
            body.result = response.toString();
        } catch (Exception e) {
            ELog.e(e);
            body.status = -100;
            body.result = e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return body;
    }

    /**
     * 对post参数进行编码处理
     */
    private static StringBuilder getStrParams(Map<String, String> strParams) {
        StringBuilder strSb = new StringBuilder();
        for (Map.Entry<String, String> entry : strParams.entrySet()) {
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


    public static class ResponseBody {
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


    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkServerTrusted");
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class FormDataFile {
        private String formDataName = "file";
        private File file;
        private String fileName;
        private String contentType;

        private FormDataFile() {

        }

        public FormDataFile(String formDataName, File file) {
            this.formDataName = formDataName;
            this.file = file;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentType() {
            return contentType;
        }
    }

    public static class FormDataJSONParam {
        private String formDataName = "file";
        private String jsonParam;

        private FormDataJSONParam() {

        }

        public FormDataJSONParam(String formDataName, String jsonParam) {
            this.formDataName = formDataName;
            this.jsonParam = jsonParam;
        }
    }
}