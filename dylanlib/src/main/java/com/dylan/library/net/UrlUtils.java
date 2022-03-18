package com.dylan.library.net;

import android.webkit.MimeTypeMap;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;


 
public class UrlUtils {


     private static String TruncateUrlPage(String strURL){
         String strAllParam=null;
         String[] arrSplit;
         strURL=strURL.trim();
         arrSplit=strURL.split("[?]");
         if(strURL.length()>1){
           if(arrSplit.length>1){
               for (int i=1;i<arrSplit.length;i++){
                   strAllParam = arrSplit[i];
               }
           }
         }
         return strAllParam;   
     }
     

     public static Map<String, String> urlSplit(String URL){
         Map<String, String> mapRequest = new HashMap<String, String>();
         String[] arrSplit=null;
         String strUrlParam=TruncateUrlPage(URL);
         if(strUrlParam==null){
             return mapRequest;
         }
         arrSplit=strUrlParam.split("[&]");
         for(String strSplit:arrSplit){
               String[] arrSplitEqual=null;         
               arrSplitEqual= strSplit.split("[=]");
               //解析出键值
               if(arrSplitEqual.length>1){
                   //正确解析
                   mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
               }else{
                   if(arrSplitEqual[0]!=""){
                   //只有参数没有值，不加入
                   mapRequest.put(arrSplitEqual[0], "");       
                   }
               }
         }   
         return mapRequest;   
     }


    public static String  parseFileNameFormUrl(String url)  {
        try{
            String fileName="";
            URL mUrl=new URL(url);
            String urlString=mUrl.toString();
            String query=mUrl.getQuery();
            int lastSpart=urlString.lastIndexOf("/");
            if (query==null||query.isEmpty()){
                fileName=urlString.substring(lastSpart+1,urlString.length());
            }else{
                int queryParamStart=urlString.indexOf("?");
                fileName=urlString.substring(lastSpart+1,queryParamStart);
            }
            return fileName;
        }catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }


    public static String getSuffixFromUrl(String url){
        return MimeTypeMap.getFileExtensionFromUrl(url);
    }

}
