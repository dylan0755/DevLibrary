package com.dylan.library.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MultiPartPoster {
    public static  int TIME_OUT = 12000;
    private final String boundary;
    private static final String LINE_Feed = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
    private ResponseBody responseBody;
    private long startTime;

    public MultiPartPoster(String requestURL) throws IOException {
        this( requestURL,"utf-8");
    }

    public MultiPartPoster(String requestURL, String charset)
            throws IOException {
        this.charset = charset;
        startTime =System.currentTimeMillis();
        // creates a unique boundary based on time stamp
        boundary = "===" +startTime+ "===";
        responseBody=new ResponseBody();
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setUseCaches(false);//Post 请求不能使用缓存
        httpConn.setRequestMethod("POST");
        httpConn.setReadTimeout(TIME_OUT);
        httpConn.setConnectTimeout(TIME_OUT);
        httpConn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent","CodeJava Agent");
        httpConn.setRequestProperty("Test","Bonjour");
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream,charset),true);
    }

    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormFieldByText(String name,String value) {
        writer.append("--" + boundary).append(LINE_Feed);
        writer.append("Content-disposition: form-data; name=\"" + name + "\"")
                .append(LINE_Feed);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_Feed);
        writer.append(LINE_Feed);
        writer.append(value).append(LINE_Feed);
        writer.flush();
    }
    public void addFormFieldByJson(String name, String value) {
        writer.append("--" + boundary).append(LINE_Feed);
        writer.append("Content-disposition: form-data; name=\"" + name + "\"")
                .append(LINE_Feed);
        writer.append("Content-Type: application/json; charset=" + charset).append(
                LINE_Feed);
        writer.append(LINE_Feed);
        writer.append(value).append(LINE_Feed);
        writer.flush();
    }


    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_Feed);
        writer.append(
                "Content-disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_Feed);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_Feed);
        writer.append("Content-transfer-encoding: binary").append(LINE_Feed);
        writer.append(LINE_Feed);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer,0,bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_Feed);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name,String value) {
        writer.append(name + ": " + value).append(LINE_Feed);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     * @return a list of Strings as response in case the server returned
     * status OK,otherwise an exception is thrown.
     * @throws IOException
     */
    public ResponseBody getResponseBody() throws IOException {
        writer.append(LINE_Feed).flush();
        writer.append("--" + boundary + "--").append(LINE_Feed);
        writer.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        responseBody.status=status;
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            StringBuilder builder=new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            httpConn.disconnect();
            responseBody.result=builder.toString();
        }
        responseBody.duration=(System.currentTimeMillis()- startTime)*1.0f/1000+"s";
        if (responseBody.status==200){
            responseBody.byteSize=ResponseBody.getFormatFileSize(responseBody.result.getBytes().length);
        }
        return responseBody;
    }
}
