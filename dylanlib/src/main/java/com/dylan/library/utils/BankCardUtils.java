package com.dylan.library.utils;

import com.dylan.library.exception.ELog;
import com.dylan.library.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  校验银行卡号是否正确
 */

public class BankCardUtils {



    public BankCardUtils() {

    }




    public static boolean checkBankCardCode(String cardNo) {

        if(cardNo == null || cardNo.trim().length() == 0  ) {
            return false;
        }
        try {
            int[] cardNoArr = new int[cardNo.length()];
            for (int i = 0; i < cardNo.length(); i++) {
                cardNoArr[i] = Integer.valueOf(String.valueOf(cardNo.charAt(i)));
            }
            for (int i = cardNoArr.length - 2; i >= 0; i -= 2) {
                cardNoArr[i] <<= 1;
                cardNoArr[i] = cardNoArr[i] / 10 + cardNoArr[i] % 10;
            }
            int sum = 0;
            for (int i = 0; i < cardNoArr.length; i++) {
                sum += cardNoArr[i];
            }
            return sum % 10 == 0;
        } catch (Exception e) {
            return false;
        }

    }






    public static String getCardTypeText(String cardType) {
        if ("DC".equals(cardType)){
            return  "储蓄卡";
        }else if ("CC".equals(cardType)){
            return "信用卡";
        }else if ("SCC".equals(cardType)){
            return "准贷记卡";
        }else if ("PC".equals(cardType)){
            return "预付费卡";
        }else{
            return "";
        }
    }




    public static void getCardInfo(final String cardNo, final ObtainCardInfoCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardBinCheck=true&cardNo=" + cardNo);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(6000);
                    connection.setReadTimeout(6000);
                    connection.setRequestMethod("GET");
                    int reponseCode = connection.getResponseCode();
                    if (reponseCode == 200) {
                        String json = FileUtils.getStringFromInputStream(connection.getInputStream());
                        if (callBack != null) callBack.onSuccess(json);
                    } else {
                        String error = FileUtils.getStringFromInputStream(connection.getErrorStream());
                        if (callBack != null)
                            callBack.onError("reponseCode=" + reponseCode + " error=" + error);
                    }

                } catch (Exception e) {
                    if (callBack != null)
                        callBack.onError(e.getMessage());
                    ELog.e(e);
                }

            }
        }).start();
    }


    public void checkCardValid(final String cardNo, final CheckCardValidCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardBinCheck=true&cardNo=" + cardNo);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestMethod("GET");
                    int reponseCode = connection.getResponseCode();
                    if (reponseCode == 200) {
                        String json = FileUtils.getStringFromInputStream(connection.getInputStream());
                        JSONObject jsonObject=new JSONObject(json);
                        boolean validated=jsonObject.optBoolean("validated");
                        if (!validated){//校验不通过，再用工具类检查一遍
                            validated= BankCardUtils.checkBankCardCode(cardNo);
                        }
                        if (callBack!=null)callBack.onSuccess(validated);
                    } else {
                        boolean validated= BankCardUtils.checkBankCardCode(cardNo);
                        if (callBack!=null)callBack.onSuccess(validated);
                    }


                } catch (Exception e) {
                    boolean validated= checkBankCardCode(cardNo);
                    if (callBack!=null)callBack.onSuccess(validated);
                    ELog.e(e);
                }

            }
        }).start();
    }


    public interface ObtainCardInfoCallBack {
        void onSuccess(String json);
        void onError(String errorMsg);
    }

    public interface CheckCardValidCallBack{
        void onSuccess(boolean isValid);
    }




}
